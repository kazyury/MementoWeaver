package nobugs.nolife.mw.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MementoGenerateProcessor {
	private static Logger logger = Logger.getGlobal();
	private EntityManager em = PersistenceUtil.getMWEntityManager();


	public void generateProcess(List<Generator> generatorList) {
		// TODO Auto-generated method stub
		
	}


	public List<Generator> getGeneratorList() throws MWException{
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
		return generatorList;
	}

//	public List<String> listAffectedMemento() throws MWException{
//		final List<String> affectedMementoList = new ArrayList<>();
//		whileOnTargetTag(new TargetTagHandler() {
//			@Override
//			public void process(Generator generator, Material m, String tag) {
//				String affected = generator.affectedMemento(m, tag);
//				if (!affectedMementoList.contains(affected) && affected!=null){
//					affectedMementoList.add(affected);
//				}
//			}
//		});
//		return affectedMementoList;

		//		List<String> affectedMementoList = new ArrayList<>();
		//
		//		logger.fine("TaggedMaterial から生成対象タグ抽出します");
		//		TypedQuery<TaggedMaterial> query = em.createQuery(
		//				"SELECT tm FROM Material m , m.taggedMaterials tm " +
		//				"WHERE m.materialState = :materialState AND tm.tagState = :tagState",TaggedMaterial.class);
		//		query.setParameter("materialState", Constants.MATERIAL_STATE_STAGED);
		//		query.setParameter("tagState", Constants.TAG_STATE_STAGED);
		//
		//		
		//		logger.fine("影響のあるメメントリストを作成します");
		//		for(TaggedMaterial tm:query.getResultList()){
		//			Material m = tm.getMaterial();
		//			String tag = tm.getId().getTag();
		//
		//			Generator generator = GeneratorFactory.getGenerator(tag);
		//			String affected = generator.affectedMemento(m, tag);
		//
		//			if (!affectedMementoList.contains(affected) && affected!=null){
		//				affectedMementoList.add(affected);
		//			}
		//		}
		//		em.close();
		//		
		//		logger.fine("影響のあるメメントは["+affectedMementoList+"]です");
		//		return affectedMementoList;
//	}

	//---------------------------------------------------------------- closure
	interface TargetTagHandler {
		void process(Generator generator, Material m, String tag);
	}

	private void whileOnTargetTag(TargetTagHandler handler) throws MWException{
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE m.materialState = :materialState AND tm.tagState = :tagState",TaggedMaterial.class);
		query.setParameter("materialState", Constants.MATERIAL_STATE_STAGED);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);

		for(TaggedMaterial tm:query.getResultList()){
			Material m = tm.getMaterial();
			String tag = tm.getId().getTag();

			Generator generator = GeneratorFactory.getGenerator(tag);
			handler.process(generator, m, tag);
		}
		em.close();
	}
}
