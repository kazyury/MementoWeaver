package nobugs.nolife.mw.processing;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.dao.MaterialDao;
import nobugs.nolife.mw.dao.MementoDao;
import nobugs.nolife.mw.dao.TagConfigDao;
import nobugs.nolife.mw.dao.TaggedMaterialDao;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.generator.GeneratorFactory;
import nobugs.nolife.mw.generator.SubGenerator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class ArchiveProcessor {
	private static Logger logger = Logger.getGlobal();
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	private static MaterialDao materialDao = new MaterialDao();
	private static TaggedMaterialDao taggedMaterialDao = new TaggedMaterialDao();
	private static MementoDao mementoDao = new MementoDao();
	private static TagConfigDao tagConfigDao = new TagConfigDao();


	public void archive(List<Memento> mementoList) {
		// �������g�ɑ����Ă���f�ނ̃}�e���A��ID�ꗗ�Ƃ��̑f�ނ����̃������g�ɂ������Ă��邩��Map���擾(���ƂŃ`�F�b�N�Ŏg�p)
		Map<String, Boolean> materialUsageMap = getMaterialUsageMap(mementoList);
		logger.info("materialUsageMap ���擾���܂���.");

		// �Ώ�Memento�ɑ�����TaggedMaterial���폜(TaggedMaterial��1���_�ōő��1��Memento�ɂ����֘A���Ȃ�=>ER���Ԉ���Ă���...)
		for(Memento memento:mementoList) {
			for(TaggedMaterial tm:memento.getTaggedMaterials()){
				logger.info("�}�e���A��ID["+tm.getId().getMaterialId()+"] �^�O["+tm.getId().getTag()+"]��TaggedMaterial���폜���܂�.");
				taggedMaterialDao.remove(tm);
			}
		}

		// (���̃������g�Ŏg�p���Ă��Ȃ��ꍇ)Material����폜
		for(Map.Entry<String, Boolean> used:materialUsageMap.entrySet()){
			Material m = em.find(Material.class, used.getKey());
			Path materialSource = PathUtil.getProductionFilePath(m);
			Path thumbnailSoruce = PathUtil.getProductionThumbnailPath(m);
			Path photoSource = PathUtil.getProductionPhotoPath(m);

			if(used.getValue().booleanValue()) {
				// material�����̃������g�Ŏg�p����Ă���ꍇ�́A�A�[�J�C�u�G���A�ɕ���
				logger.info("�}�e���A��ID["+m.getMaterialId()+"] ��Material���A�[�J�C�u�G���A�ɕ������܂�.");
				PathUtil.copyToArchive(materialSource);
				PathUtil.copyToArchive(thumbnailSoruce);
				if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
					PathUtil.copyToArchive(photoSource);
				}
				
			} else {
				// material�����̃������g�Ŏg�p����Ă��Ȃ��ꍇ�́ADB����폜���ăA�[�J�C�u�G���A�Ɉړ�
				logger.info("�}�e���A��ID["+m.getMaterialId()+"] ��Material��DB���폜���A�A�[�J�C�u�G���A�Ɉړ����܂�.");
				materialDao.remove(m);

				PathUtil.moveToArchive(materialSource);
				PathUtil.moveToArchive(thumbnailSoruce);
				if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)){
					PathUtil.moveToArchive(photoSource);
				}
			}
		}

		// Memento�̏���DB����폜��,�t�@�C�����̂��̂̓A�[�J�C�u�G���A�Ɉړ�. ���̌チ�����g�̃J�e�S���ɉ������T�u�W�F�l���[�^��Index�����č쐬
		// TODO �A�[�J�C�u�G���A���ɂ����j���[�₻�̑��e��t�@�C�������Ȃ���΂Ȃ�Ȃ�
		for(Memento memento:mementoList) {
			logger.info("�������gID["+memento.getMementoId()+"] ��Memento��DB���폜���A�A�[�J�C�u�G���A�Ɉړ����܂�.");
			mementoDao.remove(memento);
			PathUtil.moveToArchive(memento.getProductionPath());
			
			String category = memento.getCategory();
			String generatorFQCN = tagConfigDao.findGeneratorNameByCategory(category);
			if(generatorFQCN==null) {
				// ����̓������g��(�J�e�S���ɊY���Ȃ�)
				logger.info("�J�e�S��["+category+"] �͐����������g�ł͂Ȃ�����Index�쐬�͍s���܂���.");
			} else {
				logger.info("�J�e�S��["+category+"] ��index���Đ������܂�.");
				SubGenerator subGenerator = GeneratorFactory.getSubGenerator(generatorFQCN);
				subGenerator.generate();
			}
		}

		// TODO news.vm�̏���
	}

	private Map<String, Boolean> getMaterialUsageMap(List<Memento> mementoList) {

		Map<String, Boolean> materialUsageMap = new HashMap<>();

		for(Memento memento:mementoList){
			for(TaggedMaterial tm:memento.getTaggedMaterials()) {
				String materialId = tm.getId().getMaterialId();
				List<TaggedMaterial> list = taggedMaterialDao.findByMaterialId(materialId);
				if(list.size() > 1){
					materialUsageMap.put(materialId, new Boolean(true));	// ���ł��g�p����Ă���
				} else {
					materialUsageMap.put(materialId, new Boolean(false)); // ���g�p
				}
			}
		}
		return materialUsageMap;
	}
}