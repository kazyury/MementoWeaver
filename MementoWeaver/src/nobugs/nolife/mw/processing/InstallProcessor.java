package nobugs.nolife.mw.processing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.derivatizer.Derivatizer;
import nobugs.nolife.mw.derivatizer.DerivatizerFactory;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PersistenceUtil;

public class InstallProcessor {
	private File sourceDirectory;
	private File targetDirectory;
	
	public void installProcess(String srcPath, String destPath){
		sourceDirectory = new File(srcPath);
		targetDirectory = new File(destPath);
		
		EntityManager em = PersistenceUtil.getMWEntityManager();

		// �f�ރ\�[�X�A�X�e�[�W���O�G���A�̃p�X���`�F�b�N
		if (!isValidPathSet()) {
			System.out.println("checkFilePath() fails.");
			return; // TODO ��O�X���[
		}

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
			String suffix = fileTypeOf(material);
			if (suffix.equals("jpg")){
				materialEntity.setMaterialType(Constants.MATERIAL_TYPE_JPG);
			} else if (suffix.equals("mov")){
				materialEntity.setMaterialType(Constants.MATERIAL_TYPE_MOV);
			} else {
				System.out.println("Unsupported file type");
				return; // TODO ��O�𓊂���悤�ɂ���ׂ��B
			}

			// �t�@�C���^�C���X�^���v�̎擾�Ɛݒ�
			Date lastModifiedDate = new Date(material.lastModified());
			
			String destBaseFilename = formatDate(lastModifiedDate, "yyyyMMdd'_'hhmmss");
			materialEntity.setMaterialId(formatDate(lastModifiedDate,"yyyyMMddhhmmss"));
			materialEntity.setCreatedYear(Integer.parseInt(formatDate(lastModifiedDate,"yyyy")));
			materialEntity.setCreatedMonth(Integer.parseInt(formatDate(lastModifiedDate,"MM")));
			
			// �X�e�[�W���O�G���A�Ƀt�@�C������ύX���ăR�s�[
			java.nio.file.Path dest = new File(targetDirectory,destBaseFilename+"."+suffix).toPath();
			try {
				Files.copy(material.toPath(), dest, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e1) {
				//TODO �����͈���Ԃ��Ȃ��ق����������ȁB
				e1.printStackTrace();
				return;
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
	private boolean isValidPathSet() {
		// �f�ރ\�[�X(pathInput)���擾

		// pathInput����Ⴕ���͑��݂��Ȃ��p�X�Ȃ�΃G���[���b�Z�[�W��\������return
		if (!sourceDirectory.isDirectory()) {
			//TODO �G���[���b�Z�[�W�̕\�� javaFX1.3 �ł� javafx.stage.Alert���L������2.2�ł͂Ȃ��Ȃ��Ă���B3�ŕ����̗\��炵�����B
			System.out.println(sourceDirectory.toString() + " is not Directory.");
			return false;
		}

		// �X�e�[�W���O�G���A����Ⴕ���͑��݂��Ȃ��p�X�Ȃ�΃G���[���b�Z�[�W��\������return
		if (!targetDirectory.isDirectory()) {
			//TODO �G���[���b�Z�[�W�̕\�� javaFX1.3 �ł� javafx.stage.Alert���L������2.2�ł͂Ȃ��Ȃ��Ă���B3�ŕ����̗\��炵�����B
			System.out.println(targetDirectory.toString() + " is not Directory.");
			return false;
		}
		return  true;
	}

	/**
	 * @param material
	 * @return
	 */
	private String fileTypeOf(File material) {
		int pos = material.getPath().lastIndexOf(".");
		String suffix = material.getPath().toLowerCase().substring(pos+1);
		
		if (suffix.equals("jpg")||suffix.equals("jpeg")){
			suffix = "jpg";
		}
		if (!suffix.equals("mov") && !suffix.equals("jpg")){
			System.out.println("Unsupported file type");
		}
		return suffix;
	}

	private String formatDate(Date date, String pattern) {
		DateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}
}
