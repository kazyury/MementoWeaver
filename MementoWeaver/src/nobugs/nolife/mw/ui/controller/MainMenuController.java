package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MainMenuController extends AnchorPane implements MWSceneController {
	private AppMain appl;
	
	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v

	// �C�x���g�n���h��
	@FXML	protected void installMaterial(ActionEvent e) throws MWException {appl.fwdInstallMaterial();}
	@FXML	protected void generateMemento(ActionEvent e) throws MWException {appl.fwdInstalledMaterialList();}
	@FXML	protected void modifyMemento(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void manageTag(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void archive(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void scanMaterial(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {Platform.exit();}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// do nothing.
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
	}
}
