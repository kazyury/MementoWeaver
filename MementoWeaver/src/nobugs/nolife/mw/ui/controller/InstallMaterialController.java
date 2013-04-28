package nobugs.nolife.mw.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import name.antonsmirnov.javafx.dialog.Dialog;
import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.exceptions.MWInvalidUserInputException;
import nobugs.nolife.mw.processing.InstallProcessor;
import nobugs.nolife.mw.util.CacheManager;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialController extends AnchorPane implements MWSceneController{
	private static String materialSourcePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MATERIAL_SOURCE);
	private static String stagingAreaPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_STAGING_AREA);
	private AppMain appl;

	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField pathInput;

	// �C�x���g�n���h��
	@FXML	protected void install(ActionEvent e){
		InstallProcessor processor = new InstallProcessor();
		try {
			processor.installProcess(pathInput.getText(), stagingAreaPath);
		} catch (MWInvalidUserInputException ex) {
			Dialog.showError(getId(), ex.getMessage());
			return;
		}

		// �����pathInput���v���p�e�B�ɃZ�b�g���ĕۊ�
		CacheManager.storeMaterialSourceCache(pathInput.getText());

		appl.fwdInstalledMaterialList();
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
		// �v���p�e�B�Ɏw�肳�ꂽ�f�ރ\�[�X�������ݒ�
		pathInput.setText(materialSourcePath);
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
	}
}
