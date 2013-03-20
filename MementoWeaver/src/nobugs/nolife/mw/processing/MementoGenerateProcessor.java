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

	/**
	 * メメントを生成する
	 * @param generatorList
	 * @return
	 * @throws MWException
	 */
	public List<String> generateProcess() throws MWException {
		List<String> generatedMemento = new ArrayList<>();

		while(true) {
			// ステージングされているタグが存在しなくなったらbreak
			TypedQuery<TaggedMaterial> query = createStagedTagQuery();
			List<TaggedMaterial> result = query.getResultList();
			if(result.isEmpty()){
				break;
			}

			// 最初の1件を取得
			TaggedMaterial tm = result.get(0);
			Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());

			// ジェネレータによる生成 と その結果としてのTaggedMaterialの状態更新
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]を使用してメメントを生成します");
			List<TaggedMaterial> updateTargetList = generator.generate(tm.getMaterial());
			for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
				inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
				em.getTransaction().begin();
				em.merge(inUseTaggedMaterial);
				em.getTransaction().commit();
			}
			generatedMemento.addAll(generator.getGeneratedMemento());
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]が生成したメメントは["+generator.getGeneratedMemento().toString()+"]です");
		}
		return generatedMemento;
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
		logger.info("generatorListは"+generatorList.toString()+"です");
		return generatorList;
	}

	//---------------------------------------------------------------- closure
	interface TargetTagHandler {
		void process(Generator generator, Material m, String tag);
	}

	private void whileOnTargetTag(TargetTagHandler handler) throws MWException{
		TypedQuery<TaggedMaterial> query = createStagedTagQuery();
		for(TaggedMaterial tm:query.getResultList()){
			Material m = tm.getMaterial();
			String tag = tm.getId().getTag();

			Generator generator = GeneratorFactory.getGenerator(tag);
			handler.process(generator, m, tag);
		}
//		em.close();
	}

	private TypedQuery<TaggedMaterial> createStagedTagQuery(){
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE m.materialState = :materialState AND tm.tagState = :tagState",TaggedMaterial.class);
		query.setParameter("materialState", Constants.MATERIAL_STATE_STAGED);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);
		return query;
	}
}
