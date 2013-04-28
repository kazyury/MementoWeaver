package nobugs.nolife.mw.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.generator.SubGenerator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MementoGenerateProcessor {
	private static Logger logger = Logger.getGlobal();
	private EntityManager em = PersistenceUtil.getMWEntityManager();
	private Set<String> generatorSet = new TreeSet<>(); // �C���f�b�N�X����1�x�����쐬���邽�߂ɕK�v

	/**
	 * �������g�𐶐�����
	 * @param generatorList
	 * @return
	 * @throws MWException
	 */
	public List<String> generateProcess() {
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

			// �W�F�l���[�^�𐶐�
			Generator generator = GeneratorFactory.getGenerator(tm.getId().getTag());
			String generatorName = generator.getClass().getSimpleName();
			generatorSet.add(generatorName);
			logger.info("Generator:["+generatorName+"]���g�p���ă������g�𐶐����܂�");

			// �W�F�l���[�^�ɂ�鐶�� �Ɛ������ꂽ�������g�Ɋ܂܂��^�O�t�f�ނ̃��X�g(updateTargetList)���擾
			Memento memento = generator.generate(tm);
			List<TaggedMaterial> updateTargetList = memento.getTaggedMaterials();

			// �������ꂽ�������g�Ɋ܂܂��^�O�t�f�ނ̏�Ԃ��X�V(PUBLISHED)
			em.getTransaction().begin();
			for(TaggedMaterial inUseTaggedMaterial:updateTargetList){
				inUseTaggedMaterial.setTagState(Constants.TAG_STATE_PUBLISHED);
				em.merge(inUseTaggedMaterial);

				// ���񏈗������f�ނ�STAGED�̃^�O�����݂���ꍇ�A�f�ނ��X�e�[�W���O�G���A����R�s�[. ���݂��Ȃ���Έړ�����Material�̏�Ԃ��X�V
				Material m = inUseTaggedMaterial.getMaterial();
				if(isExistStagedTagFor(m)){
					copyMaterial(m);
				} else {
					moveMaterial(m);
					m.setMaterialState(Constants.MATERIAL_STATE_IN_USE);
					em.merge(m);
				}
			}

			// Memento�e�[�u���̍쐬
			em.merge(memento);
			em.getTransaction().commit();

			generatedMemento.addAll(generator.getGeneratedMemento());
			logger.info("Generator:["+generator.getClass().getSimpleName()+"]�����������������g��["+generator.getGeneratedMemento().toString()+"]�ł�");

		}

		// ���̑��̊֘A�������g(albumIndex��)�̐���
		for(String generatorName:generatorSet){
			SubGenerator subGenerator = GeneratorFactory.getSubGenerator(generatorName);
			subGenerator.generate();
		}

		// TODO news.vm�̏���

		return generatedMemento;
	}



	public List<Generator> getGeneratorList(){
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

	private void whileOnTargetTag(TargetTagHandler handler){
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
	 * �f�ނ��X�e�[�W���O�G���A����Production���ɔz�u(�R�s�[)����B
	 * @param m
	 * @throws MWException
	 */
	private void copyMaterial(Material m) {
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
			logger.info("�f��["+m.getMaterialId()+"]�ɂ�STAGED�^�O�����݂��邽�߁AProduction���ɃR�s�[���܂�");
			Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceThumbPath, destThumbPath, StandardCopyOption.REPLACE_EXISTING);
			if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
				Files.copy(sourcePhotoPath, destPhotoPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}

	}
	
	/**
	 * �f�ނ��X�e�[�W���O�G���A����Production���ɔz�u(�ړ�)����B
	 * @param m
	 * @throws MWException
	 */
	private void moveMaterial(Material m) {
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
			logger.info("�f��["+m.getMaterialId()+"]��Production���Ɉړ����܂�");
			Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
			Files.move(sourceThumbPath, destThumbPath, StandardCopyOption.REPLACE_EXISTING);
			if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
				Files.move(sourcePhotoPath, destPhotoPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
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
