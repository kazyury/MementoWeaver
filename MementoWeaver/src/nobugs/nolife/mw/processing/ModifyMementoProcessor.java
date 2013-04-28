package nobugs.nolife.mw.processing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.entities.TaggedMaterialPK;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWInvalidUserInputException;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.generator.SubGenerator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.MementoUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class ModifyMementoProcessor {
	private static Logger logger = Logger.getGlobal();
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	private Set<String> generatorSet = new TreeSet<>(); // インデックス等を1度だけ作成するために必要

	/**
	 * 
	 * @param memento
	 * @param materialFile
	 * @param tag
	 * @return
	 * @throws MWInvalidUserInputException 
	 * @throws MWException
	 */
	public TaggedMaterial appendTaggedMaterialProcess(Memento memento, File materialFile, String tag) throws MWInvalidUserInputException {
		/* 追加されたTaggedMaterialのチェック処理
		 * 1 memento内に同一のMaterialIDを持つTaggedMaterialが存在する場合=>null返却
		 * 2 mementoに含まれるべきではないTaggedMaterialの場合(ex. 2012年1月のalbumPageに2012年2月の素材)=>null返却
		 * 3  対応するMaterialがDBに登録されていない=>throw MWException
		 */
		String materialId = PathUtil.toMaterialId(materialFile.toPath());

		// 1 memento内に同一のMaterialIDを持つTaggedMaterialが存在する場合はnullを返却
		for(TaggedMaterial check:memento.getTaggedMaterials()) {
			if(check.getId().getMaterialId().equals(materialId)){
				logger.warning("memento内に同一のMaterialIDを持つTaggedMaterialが存在します. materialID="+materialId);
				return null;
			}
		}

		// 2 mementoに含まれるべきではないTaggedMaterialの場合(ex. 2012年1月のalbumPageに2012年2月の素材)=>null返却
		if(!MementoUtil.isAppendable(memento, materialId)){
			logger.warning("mementoに追加することの出来ない素材です.materialID="+materialId);
			return null;
		}
		
		
		// 3 対応するMaterialが存在しない場合には例外throw
		Material m = em.find(Material.class, materialId);
		if(m==null){
			throw new MWInvalidUserInputException("materialId["+materialId+"]はDBに登録されていません.scanを実行してください.");
		}

		logger.info("素材["+materialFile+"]　タグ["+tag+"]でTaggedMaterialを作成します。");
		TaggedMaterialPK pk = new TaggedMaterialPK();
		pk.setMaterialId(m.getMaterialId());
		pk.setTag(tag);

		List<Memento> mementos = new ArrayList<Memento>();
		mementos.add(memento);
		
		TaggedMaterial tm = new TaggedMaterial();
		tm.setId(pk);
		tm.setTagState(Constants.TAG_STATE_STAGED);
		tm.setMemo(""); // TextFieldTableCellに反映させるためにはNullでは問題あり
		tm.setMementos(mementos);
		tm.setMaterial(m);

		m.addTaggedMaterial(tm);
		memento.getTaggedMaterials().add(tm);
		
		MaterialUtil.updateMaterialState(m);

		return tm;
	}


	public void mementoSubmitProcess(Memento memento) {
		// generatorによる生成前にmementoを永続化する
		logger.info("commit current state");
		em.getTransaction().begin();
		em.getTransaction().commit();
		

		TaggedMaterial tm = memento.getTaggedMaterials().get(0);

		// ジェネレータを生成
		Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());
		String generatorName = generator.getClass().getSimpleName();
		generatorSet.add(generatorName);
		logger.info("Generator:["+generatorName+"]を使用してメメントを生成します");

		// ジェネレータによる生成 と生成されたメメントに含まれるタグ付素材のリスト(updateTargetList)を取得
		generator.generate(tm);
		List<TaggedMaterial> updateTargetList = memento.getTaggedMaterials();

		// 生成されたメメントに含まれるタグ付素材の状態を更新(PUBLISHED)
		for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
			inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
			logger.info("マテリアルID["+inUseTaggedMaterial.getId().getMaterialId()+"]タグ状態は["+inUseTaggedMaterial.getTagState()+"]です。");

			// Materialの状態を更新
			Material m = inUseTaggedMaterial.getMaterial();
			MaterialUtil.updateMaterialState(m);
		}

		em.getTransaction().begin();
		em.getTransaction().commit();

		// その他の関連メメント(albumIndex等)の生成
		for(String g:generatorSet){
			SubGenerator subGenerator = GeneratorFactory.getSubGenerator(g);
			subGenerator.generate();
		}

		// TODO news.vmの処理

	}
}
