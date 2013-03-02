package nobugs.nolife.mw.processor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.derivatizer.Derivatizer;
import nobugs.nolife.mw.derivatizer.DerivatizerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialProcessor extends AnchorPane implements Initializable{
	private AppMain appl;
	private Properties dirProperties = new Properties();
	private String materialSourcePath = null; // �v���p�e�B�t�@�C�����
	private String stagingAreaPath = null;    // �v���p�e�B�t�@�C�����
	private File srcdir = null; // �f�ރ\�[�X
	private File todir = null;  // �X�e�[�W���O�G���A

	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField pathInput;

	// �C�x���g�n���h��
	@FXML	protected void install(ActionEvent e) {
		// �f�ރ\�[�X�A�X�e�[�W���O�G���A�̃p�X���`�F�b�N
		if (!isValidPathSet()) {
			System.out.println("checkFilePath() fails.");
			return;
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
		File[] materialList = srcdir.listFiles(filter);

		// �f�ރ\�[�X���̃t�@�C�����Ɉȉ��̏������s���B
		for (File material:materialList) {
			// �t�@�C���^�C�v�̎擾
			int pos = material.getPath().lastIndexOf(".");
			String suffix = material.getPath().toLowerCase().substring(pos+1);

			// �t�@�C���^�C���X�^���v�̎擾
			Date lastModifiedDate = new Date(material.lastModified());
			DateFormat df = new SimpleDateFormat("yyyyMMdd'_'hhmmss");
			String timestamp = df.format(lastModifiedDate);

			// �X�e�[�W���O�G���A�Ƀt�@�C������ύX���ăR�s�[
			java.nio.file.Path dest = new File(todir,timestamp+"."+suffix).toPath();
			try {
				Files.copy(material.toPath(), dest, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e1) {
				//TODO �����͈���Ԃ��Ȃ��ق����������ȁB
				e1.printStackTrace();
				return;
			}
			// DerivationManager(Derivatizer)�ɔh���t�@�C���̍쐬��v��
			Derivatizer derivatizer = DerivatizerFactory.getDerivatizer(material);
			derivatizer.derivate();
			
			//�@TODO PersistenceManager�ɃC���X�g�[���󋵂̓o�^��v��
			
		}
		//TODO MaterialSourceManager�ɃL���b�V����v��
		//TODO ���̉�ʂւ̑J��
	}

	@FXML	protected void browse(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose digital camera photo path.");
		File file = directoryChooser.showDialog(null);
		if (file!=null) {
			pathInput.setText(file.getPath());
		}
	}

	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// �v���p�e�B�̓ǂݍ���
		InputStream is = this.getClass().getResourceAsStream("dir.properties");
		try {
			dirProperties.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		materialSourcePath = dirProperties.getProperty("dir.materialSource");
		stagingAreaPath = dirProperties.getProperty("dir.stagingArea");

		// �v���p�e�B�Ɏw�肳�ꂽ�f�ރ\�[�X�������ݒ�
		pathInput.setText(materialSourcePath);
	}

	public void setApplication(AppMain appMain) {
		appl = appMain;
	}

	private boolean isValidPathSet() {
		// �f�ރ\�[�X(pathInput)���擾
		String src = pathInput.getText();

		// pathInput����Ⴕ���͑��݂��Ȃ��p�X�Ȃ�΃G���[���b�Z�[�W��\������return
		srcdir = new File(src);
		if (!srcdir.isDirectory()) {
			//TODO �G���[���b�Z�[�W�̕\�� javaFX1.3 �ł� javafx.stage.Alert���L������2.2�ł͂Ȃ��Ȃ��Ă���B3�ŕ����̗\��炵�����B
			System.out.println(src + " is not Directory.");
			return false;
		}

		// �X�e�[�W���O�G���A����Ⴕ���͑��݂��Ȃ��p�X�Ȃ�΃G���[���b�Z�[�W��\������return
		todir = new File(stagingAreaPath);
		if (!todir.isDirectory()) {
			//TODO �G���[���b�Z�[�W�̕\�� javaFX1.3 �ł� javafx.stage.Alert���L������2.2�ł͂Ȃ��Ȃ��Ă���B3�ŕ����̗\��炵�����B
			System.out.println(stagingAreaPath + " is not Directory.");
			return false;
		}
		return  true;
	}
}