package nobugs.nolife.mw.processor;

import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MainMenuProcessor extends AnchorPane implements MWProcessor {
	private AppMain appl;
	
	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v

	// �C�x���g�n���h��
	@FXML	protected void install(ActionEvent e) {
		appl.fwdInstallMaterial();
	}
	@FXML	protected void generate(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void modify(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// do nothing.
	}

	@Override
	public void setApplication(AppMain appMain) {
		appl = appMain;
	}
}
