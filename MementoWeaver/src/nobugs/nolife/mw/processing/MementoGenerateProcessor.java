package nobugs.nolife.mw.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.generator.SubGenerator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MementoGenerateProcessor {
	private static Logger logger = Logger.getGlobal();
	private EntityManager em = PersistenceUtil.getMWEntityManager();
	private Set<String> generatorSet = new TreeSet<>(); // インデックス等を1度だけ作成するために必要

	/**
	 * メメントを生成する
	 * @param generatorList
	 * @return
	 * @throws MWException
	 */
	public List<String> generateProcess() {
		List<String> generatedMemento = new ArrayList<>();

		while(true) {
			// ステージングされているタグが存在しなくなったらbreak
			TypedQuery<TaggedMaterial> stagedTagQuery = stagedTagQuery();
			List<TaggedMaterial> result = stagedTagQuery.getResultList();
			if(result.isEmpty()){
				break;
			}

			// 最初の1件を取得
			TaggedMaterial tm = result.get(0);

			// ジェネレータを生成
			Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());
			String generatorName = generator.getClass().getSimpleName();
			generatorSet.add(generatorName);
			logger.info("Generator:["+generatorName+"]を使用してメメントを生成します");

			// ジェネレータによる生成 と生成されたメメントに含まれるタグ付素材のリスト(updateTargetList)を取得
			Memento memento = generator.generate(tm);
			List<TaggedMaterial> updateTargetList = memento.getTaggedMaterials();

			// 生成されたメメントに含まれるタグ付素材の状態を更新(PUBLISHED)
			em.getTransaction().begin();
			for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
				inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
				em.merge(inUseTaggedMaterial);

				// 今回処理した素材にSTAGEDのタグが存在する場合、素材をステージングエリアからコピー. 存在しなければ移動してMaterialの状態を更新
				Material m = inUseTaggedMaterial.getMaterial();
				if(isExistStagedTagFor(m)){
					copyMaterial(m);
				} else {
					moveMaterial(m);
					m.setMaterialState(Constants.MATERIAL_STATE_IN_USE);
					em.merge(m);
				}
			}

			// Mementoテーブルの作成
			em.merge(memento);
			em.getTransaction().commit();

			generatedMemento.addAll(generator.getGeneratedMemento());
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]が生成したメメントは["+generator.getGeneratedMemento().toString()+"]です");

		}

		// その他の関連メメント(albumIndex等)の生成
		for(String generatorName:generatorSet){
			SubGenerator subGenerator = GeneratorFactory.getSubGenerator(generatorName);
			subGenerator.generate();
		}

		// TODO news.vmの処理

		return generatedMemento;
	}



	public List<Generator> getGeneratorList(){
		final List<Generator> generatorList = new ArrayList<>();
		whileOnTargetTag(new TargetTagHandler() {
			@Override
			public void process(Generator generator, Material m, String tag) {
				generator.prepare(m,tag);
				if (!generatorList.contains(generator) && generator!=null){
					generatorList.add(generator);
				}
			}
		});
		logger.info("generatorListは"+generatorList.toString()+"です");
		return generatorList;
	}

	//---------------------------------------------------------------- closure
	interface TargetTagHandler {
		void process(Generator generator, Material m, String tag);
	}

	private void whileOnTargetTag(TargetTagHandler handler){
		TypedQuery<TaggedMaterial> query = stagedTagQuery();
		for(TaggedMaterial tm:query.getResultList()){
			Material m = tm.getMaterial();
			String tag = tm.getId().getTag();

			Generator generator = GeneratorFactory.getGenerator(tag);
			handler.process(generator, m, tag);
		}
		//		em.close();
	}

	private TypedQuery<TaggedMaterial> stagedTagQuery(){
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE m.materialState = :materialState AND tm.tagState = :tagState",TaggedMaterial.class);
		query.setParameter("materialState", Constants.MATERIAL_STATE_STAGED);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);
		return query;
	}


	/**
	 * 素材をステージングエリアからProduction環境に配置(コピー)する。
	 * @param m
	 * @throws MWException
	 */
	private void copyMaterial(Material m) {
		Path sourcePath = PathUtil.getInstalledFilePath(m);
		// 素材がステージングエリアに存在しなければreturn.
		if(!Files.exists(sourcePath, LinkOption.NOFOLLOW_LINKS)){
			return;
		}
		Path sourceThumbPath = PathUtil.getInstalledThumbnailPath(m);
		Path sourcePhotoPath = PathUtil.getInstalledPhotoPath(m);
		Path destPath = PathUtil.getProductionFilePath(m);
		Path destThumbPath = PathUtil.getProductionThumbnailPath(m);
		Path destPhotoPath = PathUtil.getProductionPhotoPath(m);
		try {
			logger.info("素材["+m.getMaterialId()+"]にはSTAGEDタグが存在するため、Production環境にコピーします");
			Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceThumbPath, destThumbPath, StandardCopyOption.REPLACE_EXISTING);
			if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
				Files.copy(sourcePhotoPath, destPhotoPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}

	}
	
	/**
	 * 素材をステージングエリアからProduction環境に配置(移動)する。
	 * @param m
	 * @throws MWException
	 */
	private void moveMaterial(Material m) {
		Path sourcePath = PathUtil.getInstalledFilePath(m);
		// 素材がステージングエリアに存在しなければreturn.
		if(!Files.exists(sourcePath, LinkOption.NOFOLLOW_LINKS)){
			return;
		}
		Path sourceThumbPath = PathUtil.getInstalledThumbnailPath(m);
		Path sourcePhotoPath = PathUtil.getInstalledPhotoPath(m);
		Path destPath = PathUtil.getProductionFilePath(m);
		Path destThumbPath = PathUtil.getProductionThumbnailPath(m);
		Path destPhotoPath = PathUtil.getProductionPhotoPath(m);
		try {
			logger.info("素材["+m.getMaterialId()+"]をProduction環境に移動します");
			Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
			Files.move(sourceThumbPath, destThumbPath, StandardCopyOption.REPLACE_EXISTING);
			if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
				Files.move(sourcePhotoPath, destPhotoPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
	}


	/**
	 * 対象の素材にSTAGED状態のタグが付与されているか否かを返却する
	 * @param m
	 * @return
	 */
	private boolean isExistStagedTagFor(Material m) {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.tagState = :tagState AND tm.id.materialId = :materialId",TaggedMaterial.class);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);
		query.setParameter("materialId", m.getMaterialId());
		List<TaggedMaterial> result = query.getResultList();
		if(result.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
}
