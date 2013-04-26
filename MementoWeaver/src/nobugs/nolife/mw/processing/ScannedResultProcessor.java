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

		// memento ‚ª null ‚È‚ç‚ÎAV‹K‚ÉMemento‚ğì¬‚·‚é.
		// ŠescannedMaterial‚ğw’è‚ÌTag‚ğ‚ÂTaggedMaterial‚Æ‚µ‚ÄŠÇ—‚·‚é.
		if(memento == null) {
			memento = new Memento();
			memento.setMementoId(PathUtil.toMementoId(path));
			memento.setCategory(category);
			memento.setProductionPath(path);
			memento.setTaggedMaterials(toTaggedMaterials(memento, tag, scannedMaterialList));
			em.persist(memento);
		} else {
			// Šù‚Émemento‚ª‘¶İ‚µ‚Ä‚¢‚é‚Ì‚Å‚ ‚ê‚Î“o˜^Ï‚İ‚ÌTaggedMaterial‚ğg—p‚·‚é
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
	 * scannedMaterial‚ÌƒŠƒXƒg‚©‚çTaggedMaterial‚ÌƒŠƒXƒg‚ğ¶¬‚·‚é.
	 * ‚»‚ÌÛ‚ÉMaterial‚ª–¢“o˜^‚È‚ç‚ÎMaterial‚ğì¬‚·‚é.
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
		
		// ‹l‚ß‘Ö‚¦
		for(ScannedMaterialDTO dto:scannedMaterialList) {
			// TaggedMaterialPK ‚Ìì¬
			Path materialPath = dto.getPath();
			String materialId = PathUtil.toMaterialId(materialPath);

			TaggedMaterialPK pk = new TaggedMaterialPK();
			pk.setMaterialId(materialId);
			pk.setTag(tag);
			TaggedMaterial tm = null;

			if(dto.isBelongToMemento()){
				// dto‚ªƒƒƒ“ƒg‚É‘®‚µ‚Ä‚¢‚éê‡‚É‚ÍŠù‚ÉTaggedMaterial‚ª‘¶İ‚·‚é(=Material‚à‘¶İ)‚Í‚¸‚È‚Ì‚Å“o˜^Ï‚İ‚Ìtm‚ğg—p‚·‚é
				tm = em.find(TaggedMaterial.class, pk);
				logger.info("dto["+dto.getPath()+"]‚ÍMemento‚É‘®‚µ‚Ä‚¨‚èAtm["+tm.getId().getMaterialId()+"]‚ğEntityManager‚æ‚èæ“¾‚µ‚Ü‚µ‚½.");

			} else {
				// dto‚ªƒƒƒ“ƒg‚É‘®‚µ‚Ä‚¢‚È‚¢ê‡‚É‚ÍV‹K‚ÉTaggedMaterial‚ğì¬‚·‚é
				tm = new TaggedMaterial();
				tm.setId(pk);
				tm.setMemo("");
				tm.setTagState(Constants.TAG_STATE_PUBLISHED);
				logger.info("dto["+dto.getPath()+"]‚ÍMemento‚É‘®‚µ‚Ä‚¢‚È‚¢‚½‚ßAtm‚ğV‹K‚Éì¬‚µ‚Ü‚µ‚½.");

				Material m = null;
				if(dto.isRegisteredMaterial()) {
					// scannedMaterial‚ªŠù‚ÉMaterial‚Æ‚µ‚Ä“o˜^‚³‚ê‚Ä‚¢‚é‚È‚çA‚»‚ÌMaterial‚ğg—p‚·‚é
					m = em.find(Material.class, materialId);
					m.addTaggedMaterial(tm);
					logger.info("dto["+dto.getPath()+"]‚Í“o˜^Ï‚İMaterial‚Ì‚½‚ßAm["+m.getMaterialId()+"]‚ğEntityManager‚æ‚èæ“¾‚µ‚Ü‚µ‚½.");
				} else {
					// scannedMaterial‚ª–¢“o˜^Material‚È‚çAV‹KMaterial‚ğì¬‚·‚é
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
					logger.info("dto["+dto.getPath()+"]‚Í–¢“o˜^Material‚Ì‚½‚ßAm‚ğV‹K‚Éì¬‚µ‚Ü‚µ‚½.");
				}
				tm.setMaterial(m);
			}
			tm.setMementos(mementos);
			taggedMaterials.add(tm);
		}
		
		return taggedMaterials;
	}
}
