package nobugs.nolife.mw.processing;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.dao.MaterialDao;
import nobugs.nolife.mw.dao.MementoDao;
import nobugs.nolife.mw.dao.TagConfigDao;
import nobugs.nolife.mw.dao.TaggedMaterialDao;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.generator.SubGenerator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class ArchiveProcessor {
	private static Logger logger = Logger.getGlobal();
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	private static MaterialDao materialDao = new MaterialDao();
	private static TaggedMaterialDao taggedMaterialDao = new TaggedMaterialDao();
	private static MementoDao mementoDao = new MementoDao();
	private static TagConfigDao tagConfigDao = new TagConfigDao();


	public void archive(List<Memento> mementoList) {
		// メメントに属している素材のマテリアルID一覧とその素材が他のメメントにも属しているかのMapを取得(あとでチェックで使用)
		Map<String, Boolean> materialUsageMap = getMaterialUsageMap(mementoList);
		logger.info("materialUsageMap を取得しました.");

		// 対象Mementoに属するTaggedMaterialを削除(TaggedMaterialは1時点で最大で1つのMementoにしか関連しない=>ERが間違ってたな...)
		for(Memento memento:mementoList) {
			for(TaggedMaterial tm:memento.getTaggedMaterials()){
				logger.info("マテリアルID["+tm.getId().getMaterialId()+"] タグ["+tm.getId().getTag()+"]のTaggedMaterialを削除します.");
				taggedMaterialDao.remove(tm);
			}
		}

		// (他のメメントで使用していない場合)Materialから削除
		for(Map.Entry<String, Boolean> used:materialUsageMap.entrySet()){
			Material m = em.find(Material.class, used.getKey());
			Path materialSource = PathUtil.getProductionFilePath(m);
			Path thumbnailSoruce = PathUtil.getProductionThumbnailPath(m);
			Path photoSource = PathUtil.getProductionPhotoPath(m);

			if(used.getValue().booleanValue()) {
				// materialが他のメメントで使用されている場合は、アーカイブエリアに複製
				logger.info("マテリアルID["+m.getMaterialId()+"] のMaterialをアーカイブエリアに複製します.");
				PathUtil.copyToArchive(materialSource);
				PathUtil.copyToArchive(thumbnailSoruce);
				if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
					PathUtil.copyToArchive(photoSource);
				}
				
			} else {
				// materialが他のメメントで使用されていない場合は、DBから削除してアーカイブエリアに移動
				logger.info("マテリアルID["+m.getMaterialId()+"] のMaterialをDBより削除し、アーカイブエリアに移動します.");
				materialDao.remove(m);

				PathUtil.moveToArchive(materialSource);
				PathUtil.moveToArchive(thumbnailSoruce);
				if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
					PathUtil.moveToArchive(photoSource);
				}
			}
		}

		// Mementoの情報をDBから削除し,ファイルそのものはアーカイブエリアに移動. その後メメントのカテゴリに応じたサブジェネレータでIndex等を再作成
		// TODO アーカイブエリア側にもメニューやその他各種ファイルを作らなければならない
		for(Memento memento:mementoList) {
			logger.info("メメントID["+memento.getMementoId()+"] のMementoをDBより削除し、アーカイブエリアに移動します.");
			mementoDao.remove(memento);
			PathUtil.moveToArchive(memento.getProductionPath());
			
			String category = memento.getCategory();
			String generatorFQCN = tagConfigDao.findGeneratorNameByCategory(category);
			if(generatorFQCN==null) {
				// 手入力メメント等(カテゴリに該当なし)
				logger.info("カテゴリ["+category+"] は生成メメントではないためIndex作成は行いません.");
			} else {
				logger.info("カテゴリ["+category+"] のindexを再生成します.");
				SubGenerator subGenerator = GeneratorFactory.getSubGenerator(generatorFQCN);
				subGenerator.generate();
			}
		}

		// TODO news.vmの処理
	}

	private Map<String, Boolean> getMaterialUsageMap(List<Memento> mementoList) {

		Map<String, Boolean> materialUsageMap = new HashMap<>();

		for(Memento memento:mementoList){
			for(TaggedMaterial tm:memento.getTaggedMaterials()) {
				String materialId = tm.getId().getMaterialId();
				List<TaggedMaterial> list = taggedMaterialDao.findByMaterialId(materialId);
				if(list.size() > 1){
					materialUsageMap.put(materialId, new Boolean(true));	// 他でも使用されている
				} else {
					materialUsageMap.put(materialId, new Boolean(false)); // 未使用
				}
			}
		}
		return materialUsageMap;
	}
}