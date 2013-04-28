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
	private Set<String> generatorSet = new TreeSet<>(); // �C���f�b�N�X����1�x�����쐬���邽�߂ɕK�v

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
		/* �ǉ����ꂽTaggedMaterial�̃`�F�b�N����
		 * 1 memento���ɓ����MaterialID������TaggedMaterial�����݂���ꍇ=>null�ԋp
		 * 2 memento�Ɋ܂܂��ׂ��ł͂Ȃ�TaggedMaterial�̏ꍇ(ex. 2012�N1����albumPage��2012�N2���̑f��)=>null�ԋp
		 * 3  �Ή�����Material��DB�ɓo�^����Ă��Ȃ�=>throw MWException
		 */
		String materialId = PathUtil.toMaterialId(materialFile.toPath());

		// 1 memento���ɓ����MaterialID������TaggedMaterial�����݂���ꍇ��null��ԋp
		for(TaggedMaterial check:memento.getTaggedMaterials()) {
			if(check.getId().getMaterialId().equals(materialId)){
				logger.warning("memento���ɓ����MaterialID������TaggedMaterial�����݂��܂�. materialID="+materialId);
				return null;
			}
		}

		// 2 memento�Ɋ܂܂��ׂ��ł͂Ȃ�TaggedMaterial�̏ꍇ(ex. 2012�N1����albumPage��2012�N2���̑f��)=>null�ԋp
		if(!MementoUtil.isAppendable(memento, materialId)){
			logger.warning("memento�ɒǉ����邱�Ƃ̏o���Ȃ��f�ނł�.materialID="+materialId);
			return null;
		}
		
		
		// 3 �Ή�����Material�����݂��Ȃ��ꍇ�ɂ͗�Othrow
		Material m = em.find(Material.class, materialId);
		if(m==null){
			throw new MWInvalidUserInputException("materialId["+materialId+"]��DB�ɓo�^����Ă��܂���.scan�����s���Ă�������.");
		}

		logger.info("�f��["+materialFile+"]�@�^�O["+tag+"]��TaggedMaterial���쐬���܂��B");
		TaggedMaterialPK pk = new TaggedMaterialPK();
		pk.setMaterialId(m.getMaterialId());
		pk.setTag(tag);

		List<Memento> mementos = new ArrayList<Memento>();
		mementos.add(memento);
		
		TaggedMaterial tm = new TaggedMaterial();
		tm.setId(pk);
		tm.setTagState(Constants.TAG_STATE_STAGED);
		tm.setMemo(""); // TextFieldTableCell�ɔ��f�����邽�߂ɂ�Null�ł͖�肠��
		tm.setMementos(mementos);
		tm.setMaterial(m);

		m.addTaggedMaterial(tm);
		memento.getTaggedMaterials().add(tm);
		
		MaterialUtil.updateMaterialState(m);

		return tm;
	}


	public void mementoSubmitProcess(Memento memento) {
		// generator�ɂ�鐶���O��memento���i��������
		logger.info("commit current state");
		em.getTransaction().begin();
		em.getTransaction().commit();
		

		TaggedMaterial tm = memento.getTaggedMaterials().get(0);

		// �W�F�l���[�^�𐶐�
		Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());
		String generatorName = generator.getClass().getSimpleName();
		generatorSet.add(generatorName);
		logger.info("Generator:["+generatorName+"]���g�p���ă������g�𐶐����܂�");

		// �W�F�l���[�^�ɂ�鐶�� �Ɛ������ꂽ�������g�Ɋ܂܂��^�O�t�f�ނ̃��X�g(updateTargetList)���擾
		generator.generate(tm);
		List<TaggedMaterial> updateTargetList = memento.getTaggedMaterials();

		// �������ꂽ�������g�Ɋ܂܂��^�O�t�f�ނ̏�Ԃ��X�V(PUBLISHED)
		for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
			inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
			logger.info("�}�e���A��ID["+inUseTaggedMaterial.getId().getMaterialId()+"]�^�O��Ԃ�["+inUseTaggedMaterial.getTagState()+"]�ł��B");

			// Material�̏�Ԃ��X�V
			Material m = inUseTaggedMaterial.getMaterial();
			MaterialUtil.updateMaterialState(m);
		}

		em.getTransaction().begin();
		em.getTransaction().commit();

		// ���̑��̊֘A�������g(albumIndex��)�̐���
		for(String g:generatorSet){
			SubGenerator subGenerator = GeneratorFactory.getSubGenerator(g);
			subGenerator.generate();
		}

		// TODO news.vm�̏���

	}
}
