package nobugs.nolife.mw.processing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.derivatizer.Derivatizer;
import nobugs.nolife.mw.derivatizer.DerivatizerFactory;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.exceptions.MWConfigurationError;
import nobugs.nolife.mw.exceptions.MWInvalidUserInputException;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class InstallProcessor {

	private File sourceDirectory;
	private File targetDirectory;
	
	public void installProcess(String srcPath, String destPath) throws MWInvalidUserInputException{
		sourceDirectory = new File(srcPath);
		targetDirectory = new File(destPath);
		
		EntityManager em = PersistenceUtil.getMWEntityManager();

		// �f�ރ\�[�X�A�X�e�[�W���O�G���A�̃p�X���`�F�b�N
		isValidPathSet();

		// �t�@�C�����X�g�̎擾�B.jpeg, .jpg, .mov ��ΏۂƂ���(�啶���������͖���)
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dirname, String filename) {
				filename = filename.toLowerCase();
				if ( filename.endsWith(".jpeg") || filename.endsWith(".jpg") || filename.endsWith(".mov") ) {
					return true;
				} 
				return false;
			}
		};
		
		// �f�ރ\�[�X���̃t�@�C�����̌J��Ԃ�
		File[] materialList = sourceDirectory.listFiles(filter);
		for (File material:materialList) {
			
			// MaterialEntity�̐���
			Material materialEntity = new Material();

			// �t�@�C���^�C�v�̎擾�Ɛݒ�
			materialEntity.setMaterialType(MaterialUtil.getMaterialType(material.toPath()));
			String suffix = MaterialUtil.getNormalizedSuffix(material);

			// �t�@�C���^�C���X�^���v�̎擾�Ɛݒ�
			Date lastModifiedDate = new Date(material.lastModified());
			
			String destBaseFilename = formatDate(lastModifiedDate, "yyyyMMdd'_'hhmmss");
			materialEntity.setMaterialId(formatDate(lastModifiedDate,"yyyyMMddhhmmss"));
			materialEntity.setCreatedYear(Integer.parseInt(formatDate(lastModifiedDate,"yyyy")));
			materialEntity.setCreatedMonth(Integer.parseInt(formatDate(lastModifiedDate,"MM")));
			
			// �X�e�[�W���O�G���A�Ƀt�@�C������ύX���ăR�s�[
			Path dest = new File(targetDirectory,destBaseFilename+"."+suffix).toPath();
			try {
				Files.copy(material.toPath(), dest, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e1) {
				throw new MWResourceIOError("�X�e�[�W���O�G���A�ւ̃t�@�C���R�s�[�ŗ�O���������܂���", e1.getCause());
			}
			// DerivationManager(Derivatizer)�ɔh���t�@�C���̍쐬��v��
			Derivatizer derivatizer = DerivatizerFactory.getDerivatizer(dest);
			derivatizer.derivate();
			
			//�@PersistenceManager�ɃC���X�g�[���󋵂̓o�^��v��
			materialEntity.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
			em.getTransaction().begin();
			em.persist(materialEntity);
			em.getTransaction().commit();
		}
		em.close();


		
	}
	private boolean isValidPathSet() throws MWInvalidUserInputException {

		// pathInput/�X�e�[�W���O�G���A����Ⴕ���͑��݂��Ȃ��p�X�Ȃ�Η�O
		if (!sourceDirectory.isDirectory()) {
			throw new MWInvalidUserInputException(sourceDirectory.toString() + " �̓f�B���N�g���ł͂���܂���.");
		}
		if (!targetDirectory.isDirectory()) {
			throw new MWConfigurationError(targetDirectory.toString() + " �̓f�B���N�g���ł͂���܂���.");
		}
		
		return  true;
	}


	private String formatDate(Date date, String pattern) {
		DateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}
}
