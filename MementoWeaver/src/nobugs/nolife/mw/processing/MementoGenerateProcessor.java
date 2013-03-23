package nobugs.nolife.mw.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
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
			TypedQuery<TaggedMaterial> stagedTagQuery = stagedTagQuery();
			List<TaggedMaterial> result = stagedTagQuery.getResultList();
			if(result.isEmpty()){
				break;
			}

			// �ŏ���1�����擾
			TaggedMaterial tm = result.get(0);
			Material m = tm.getMaterial();
			Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());

			logger.info("Generator:["+generator.getClass().getSimpleName()+"]���g�p���ă������g�𐶐����܂�");

			// �W�F�l���[�^�ɂ�鐶�� �Ɛ������ꂽ�������g�Ɋ܂܂��^�O�t�f�ނ̃��X�g(updateTargetList)���擾
			Memento memento = generator.generate(m);
			List<TaggedMaterial> updateTargetList = memento.getTaggedMaterials();

			// �������ꂽ�������g�Ɋ܂܂��^�O�t�f�ނ̏�Ԃ��X�V(PUBLISHED)
			em.getTransaction().begin();
			for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
				inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
				em.merge(inUseTaggedMaterial);
				
				// ���񏈗������f�ނ�STAGED�̃^�O�����݂���ꍇ�A�f�ނ��X�e�[�W���O�G���A����R�s�[. ���݂��Ȃ���Έړ�
				deployMaterial(inUseTaggedMaterial.getMaterial());
			}

			// Memento�e�[�u���̍쐬
			em.merge(memento);
			em.getTransaction().commit();

			generatedMemento.addAll(generator.getGeneratedMemento());
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]�����������������g��["+generator.getGeneratedMemento().toString()+"]�ł�");

		}

		// TODO ���̑��̊֘A�������g�̐���
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
		TypedQuery<TaggedMaterial> query = stagedTagQuery();
		for(TaggedMaterial tm:query.getResultList()){
			Material m = tm.getMaterial();
			String tag = tm.getId().getTag();

			Generator generator = GeneratorFactory.getGenerator(tag);
			handler.process(generator, m, tag);
		}
		//		em.close();
	}

	private TypedQuery<TaggedMaterial> stagedTagQuery(){
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE m.materialState = :materialState AND tm.tagState = :tagState",TaggedMaterial.class);
		query.setParameter("materialState", Constants.MATERIAL_STATE_STAGED);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);
		return query;
	}

	
	/**
	 * �f�ނ��X�e�[�W���O�G���A����Production���ɔz�u����B
	 * �Ώۂ̑f�ނ�STAGED�̃^�O�����݂���ꍇ�A�f�ނ��X�e�[�W���O�G���A����R�s�[. ���݂��Ȃ���Έړ�����B
	 * @param m
	 * @throws MWException
	 */
	private void deployMaterial(Material m) throws MWException {
		Path sourcePath = PathUtil.getInstalledFilePath(m);
		// �f�ނ��X�e�[�W���O�G���A�ɑ��݂��Ȃ����return.
		if(!Files.exists(sourcePath, LinkOption.NOFOLLOW_LINKS)){
			return;
		}
		Path sourceThumbPath = PathUtil.getInstalledThumbnailPath(m);
		Path sourcePhotoPath = PathUtil.getInstalledPhotoPath(m);
		Path destPath = PathUtil.getProductionFilePath(m);
		Path destThumbPath = PathUtil.getProductionThumbnailPath(m);
		Path destPhotoPath = PathUtil.getProductionPhotoPath(m);
		try {
			if(isExistStagedTagFor(m)){
				logger.info("�f��["+m.getMaterialId()+"]�ɂ�STAGED�^�O�����݂��邽�߁AProduction���ɃR�s�[���܂�");
				Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(sourceThumbPath, destThumbPath, StandardCopyOption.REPLACE_EXISTING);
				if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
					Files.copy(sourcePhotoPath, destPhotoPath, StandardCopyOption.REPLACE_EXISTING);
				}
			} else {
				logger.info("�f��["+m.getMaterialId()+"]��Production���Ɉړ����܂�");
				Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
				Files.move(sourceThumbPath, destThumbPath, StandardCopyOption.REPLACE_EXISTING);
				if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
					Files.move(sourcePhotoPath, destPhotoPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		} catch (IOException e) {
			throw new MWException(e);
		}

	}

	/**
	 * �Ώۂ̑f�ނ�STAGED��Ԃ̃^�O���t�^����Ă��邩�ۂ���ԋp����
	 * @param m
	 * @return
	 */
	private boolean isExistStagedTagFor(Material m) {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.tagState = :tagState AND tm.id.materialId = :materialId",TaggedMaterial.class);
		query.setParameter("tagState", Constants.TAG_STATE_STAGED);
		query.setParameter("materialId", m.getMaterialId());
		List<TaggedMaterial> result = query.getResultList();
		if(result.isEmpty()){
			return false;
		} else {
			return true;
		}
	}

}
