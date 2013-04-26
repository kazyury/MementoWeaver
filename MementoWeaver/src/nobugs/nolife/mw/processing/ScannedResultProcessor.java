package nobugs.nolife.mw.processing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.dto.ScannedMaterialDTO;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.ScannedResult;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.entities.TaggedMaterialPK;
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
			String category, String tag) throws MWException {
		Memento memento = scannedResult.getMemento();
		String path = scannedResult.getProductionPath();
		List<ScannedResult> scannedResults = new ArrayList<>();
		scannedResults.add(scannedResult);

		// memento �� null �Ȃ�΁A�V�K��Memento���쐬����.
		// �escannedMaterial���w���Tag������TaggedMaterial�Ƃ��ĊǗ�����.
		if(memento == null) {
			memento = new Memento();
			memento.setMementoId(PathUtil.toMementoId(path));
			memento.setCategory(category);
			memento.setProductionPath(path);
			memento.setTaggedMaterials(toTaggedMaterials(memento, tag, scannedMaterialList));
			em.persist(memento);
		} else {
			// ����memento�����݂��Ă���̂ł���Γo�^�ς݂�TaggedMaterial���g�p����
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
	 * scannedMaterial�̃��X�g����TaggedMaterial�̃��X�g�𐶐�����.
	 * ���̍ۂ�Material�����o�^�Ȃ��Material���쐬����.
	 * @param memento
	 * @param tag
	 * @param scannedMaterialList
	 * @return
	 * @throws MWException
	 */
	private List<TaggedMaterial> toTaggedMaterials(Memento memento, String tag, List<ScannedMaterialDTO> scannedMaterialList) throws MWException {
		List<TaggedMaterial> taggedMaterials = new ArrayList<>();
		List<Memento> mementos = new ArrayList<>();
		mementos.add(memento);
		
		// �l�ߑւ�
		for(ScannedMaterialDTO dto:scannedMaterialList) {
			// TaggedMaterialPK �̍쐬
			Path materialPath = dto.getPath();
			String materialId = PathUtil.toMaterialId(materialPath);

			TaggedMaterialPK pk = new TaggedMaterialPK();
			pk.setMaterialId(materialId);
			pk.setTag(tag);
			TaggedMaterial tm = null;

			if(dto.isBelongToMemento()){
				// dto���������g�ɑ����Ă���ꍇ�ɂ͊���TaggedMaterial�����݂���(=Material������)�͂��Ȃ̂œo�^�ς݂�tm���g�p����
				tm = em.find(TaggedMaterial.class, pk);
				logger.info("dto["+dto.getPath()+"]��Memento�ɑ����Ă���Atm["+tm.getId().getMaterialId()+"]��EntityManager���擾���܂���.");

			} else {
				// dto���������g�ɑ����Ă��Ȃ��ꍇ�ɂ͐V�K��TaggedMaterial���쐬����
				tm = new TaggedMaterial();
				tm.setId(pk);
				tm.setMemo("");
				tm.setTagState(Constants.TAG_STATE_PUBLISHED);
				logger.info("dto["+dto.getPath()+"]��Memento�ɑ����Ă��Ȃ����߁Atm��V�K�ɍ쐬���܂���.");

				Material m = null;
				if(dto.isRegisteredMaterial()) {
					// scannedMaterial������Material�Ƃ��ēo�^����Ă���Ȃ�A����Material���g�p����
					m = em.find(Material.class, materialId);
					m.addTaggedMaterial(tm);
					logger.info("dto["+dto.getPath()+"]�͓o�^�ς�Material�̂��߁Am["+m.getMaterialId()+"]��EntityManager���擾���܂���.");
				} else {
					// scannedMaterial�����o�^Material�Ȃ�A�V�KMaterial���쐬����
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
					logger.info("dto["+dto.getPath()+"]�͖��o�^Material�̂��߁Am��V�K�ɍ쐬���܂���.");
				}
				tm.setMaterial(m);
			}
			tm.setMementos(mementos);
			taggedMaterials.add(tm);
		}
		
		return taggedMaterials;
	}
}
