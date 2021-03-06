package nobugs.nolife.mw.processing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.dto.ScannedMaterialDTO;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.ScannedResult;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.entities.TaggedMaterialPK;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class ScannedResultProcessor {
	private static Logger logger = Logger.getGlobal();
	private static EntityManager em = PersistenceUtil.getMWEntityManager();

	public void updateIgnored(List<ScannedResult> scannedResultList) {
		for(ScannedResult sr:scannedResultList) {
			em.getTransaction().begin();
			em.merge(sr);
			em.getTransaction().commit();
		}
	}

	public void manageScannedMaterialProcess(ScannedResult scannedResult, List<ScannedMaterialDTO> scannedMaterialList, 
			String category, String tag) {
		Memento memento = scannedResult.getMemento();
		String path = scannedResult.getProductionPath();
		List<ScannedResult> scannedResults = new ArrayList<>();
		scannedResults.add(scannedResult);

		// memento が null ならば、新規にMementoを作成する.
		// 各scannedMaterialを指定のTagを持つTaggedMaterialとして管理する.
		if(memento == null) {
			memento = new Memento();
			memento.setMementoId(PathUtil.toMementoId(path));
			memento.setCategory(category);
			memento.setProductionPath(path);
			memento.setTaggedMaterials(toTaggedMaterials(memento, tag, scannedMaterialList));
			em.persist(memento);
		} else {
			// 既にmementoが存在しているのであれば登録済みのTaggedMaterialを使用する
			em.merge(memento);
			String registeredTag = memento.getTaggedMaterials().get(0).getId().getTag();
			memento.setTaggedMaterials(toTaggedMaterials(memento, registeredTag, scannedMaterialList));
		}
		
		memento.setScannedResults(scannedResults);
		
		em.getTransaction().begin();
		em.merge(memento);
		em.getTransaction().commit();

	}

	/**
	 * scannedMaterialのリストからTaggedMaterialのリストを生成する.
	 * その際にMaterialが未登録ならばMaterialを作成する.
	 * @param memento
	 * @param tag
	 * @param scannedMaterialList
	 * @return
	 * @throws MWException
	 */
	private List<TaggedMaterial> toTaggedMaterials(Memento memento, String tag, List<ScannedMaterialDTO> scannedMaterialList) {
		List<TaggedMaterial> taggedMaterials = new ArrayList<>();
		List<Memento> mementos = new ArrayList<>();
		mementos.add(memento);
		
		// 詰め替え
		for(ScannedMaterialDTO dto:scannedMaterialList) {
			// TaggedMaterialPK の作成
			Path materialPath = dto.getPath();
			String materialId = PathUtil.toMaterialId(materialPath);

			TaggedMaterialPK pk = new TaggedMaterialPK();
			pk.setMaterialId(materialId);
			pk.setTag(tag);
			TaggedMaterial tm = null;

			if(dto.isBelongToMemento()){
				// dtoがメメントに属している場合には既にTaggedMaterialが存在する(=Materialも存在)はずなので登録済みのtmを使用する
				tm = em.find(TaggedMaterial.class, pk);
				logger.info("dto["+dto.getPath()+"]はMementoに属しており、tm["+tm.getId().getMaterialId()+"]をEntityManagerより取得しました.");

			} else {
				// dtoがメメントに属していない場合には新規にTaggedMaterialを作成する
				tm = new TaggedMaterial();
				tm.setId(pk);
				tm.setMemo("");
				tm.setTagState(Constants.TAG_STATE_PUBLISHED);
				logger.info("dto["+dto.getPath()+"]はMementoに属していないため、tmを新規に作成しました.");

				Material m = null;
				if(dto.isRegisteredMaterial()) {
					// scannedMaterialが既にMaterialとして登録されているなら、そのMaterialを使用する
					m = em.find(Material.class, materialId);
					m.addTaggedMaterial(tm);
					logger.info("dto["+dto.getPath()+"]は登録済みMaterialのため、m["+m.getMaterialId()+"]をEntityManagerより取得しました.");
				} else {
					// scannedMaterialが未登録Materialなら、新規Materialを作成する
					m = new Material();
					m.setMaterialId(materialId);
					m.setCreatedYear(Integer.parseInt(MaterialUtil.getMaterialYear(m)));
					m.setCreatedMonth(Integer.parseInt(MaterialUtil.getMaterialMonth(m)));
					m.setMaterialType(MaterialUtil.getMaterialType(materialPath));
					List<TaggedMaterial> list = new ArrayList<>();
					list.add(tm);
					m.setTaggedMaterials(list);
					MaterialUtil.updateMaterialState(m);
					em.persist(m);
					logger.info("dto["+dto.getPath()+"]は未登録Materialのため、mを新規に作成しました.");
				}
				tm.setMaterial(m);
			}
			tm.setMementos(mementos);
			taggedMaterials.add(tm);
		}
		
		return taggedMaterials;
	}
}
