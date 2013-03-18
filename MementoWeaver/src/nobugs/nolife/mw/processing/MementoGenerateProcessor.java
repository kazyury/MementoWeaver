package nobugs.nolife.mw.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MementoGenerateProcessor {
	private static Logger logger = Logger.getGlobal();
	private EntityManager em = PersistenceUtil.getMWEntityManager();


	/**
	 * 
	 * @return
	 */
	public List<String> listAffectedMemento(){
		List<String> affectedMementoList = new ArrayList<>();

		logger.fine("TaggedMaterial から生成対象タグ抽出します");
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
				"WHERE m.materialState = :materialState AND tm.tagState = :tagState",TaggedMaterial.class);
		query.setParameter("materialState", Constants.MATERIAL_STATE_STAGED);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);

		
		logger.fine("影響のあるメメントリストを作成します");
		for(TaggedMaterial tm:query.getResultList()){
			Material m = tm.getMaterial();
			String tag = tm.getId().getTag();

			Generator generator = GeneratorFactory.getGenerator(tag);
			String affected = generator.affectedMemento(m, tag);

			if (!affectedMementoList.contains(affected) && affected!=null){
				affectedMementoList.add(affected);
			}
		}
		em.close();
		
		logger.fine("影響のあるメメントは["+affectedMementoList+"]です");
		return affectedMementoList;
	}
}
