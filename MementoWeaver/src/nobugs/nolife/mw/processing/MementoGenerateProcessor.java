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
	 * �������g�𐶐�����
	 * @param generatorList
	 * @return
	 * @throws MWException
	 */
	public List<String> generateProcess() throws MWException {
		List<String> generatedMemento = new ArrayList<>();

		while(true) {
			// �X�e�[�W���O����Ă���^�O�����݂��Ȃ��Ȃ�����break
			TypedQuery<TaggedMaterial> query = createStagedTagQuery();
			List<TaggedMaterial> result = query.getResultList();
			if(result.isEmpty()){
				break;
			}

			// �ŏ���1�����擾
			TaggedMaterial tm = result.get(0);
			Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());

			// �W�F�l���[�^�ɂ�鐶�� �� ���̌��ʂƂ��Ă�TaggedMaterial�̏�ԍX�V
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]���g�p���ă������g�𐶐����܂�");
			List<TaggedMaterial> updateTargetList = generator.generate(tm.getMaterial());
			for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
				inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
				em.getTransaction().begin();
				em.merge(inUseTaggedMaterial);
				em.getTransaction().commit();
			}
			generatedMemento.addAll(generator.getGeneratedMemento());
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]�����������������g��["+generator.getGeneratedMemento().toString()+"]�ł�");
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
		logger.info("generatorList��"+generatorList.toString()+"�ł�");
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
