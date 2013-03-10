package nobugs.nolife.mw.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.processing.InstallProcessor;
import nobugs.nolife.mw.util.PropertyUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialController extends AnchorPane implements MWSceneController{
	private AppMain appl;
	private String materialSourcePath = null; // �v���p�e�B�t�@�C�����
	private String stagingAreaPath = null;    // �v���p�e�B�t�@�C�����
	private PropertyUtil propertyUtil = new PropertyUtil();

	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField pathInput;

	// �C�x���g�n���h��
	@FXML	protected void install(ActionEvent e) {
		InstallProcessor processor = new InstallProcessor();
		processor.installProcess(pathInput.getText(), stagingAreaPath);

		// �����pathInput���v���p�e�B�ɃZ�b�g���ĕۊ�
		propertyUtil.storeMaterialSourceCache(pathInput.getText());

		appl.fwdStagingMaterial();
	}

	@FXML	protected void browse(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose digital camera photo path.");
		File file = directoryChooser.showDialog(null);
		if (file!=null) {
			pathInput.setText(file.getPath());
		}
	}

	@FXML	protected void exit(ActionEvent e) {
		Platform.exit();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// �v���p�e�B�̓ǂݍ���
		materialSourcePath = propertyUtil.getMaterialSourceName();
		stagingAreaPath = propertyUtil.getStagingAreaName();
		// �v���p�e�B�Ɏw�肳�ꂽ�f�ރ\�[�X�������ݒ�
		pathInput.setText(materialSourcePath);
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
	}
}
