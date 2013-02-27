package nobugs.nolife.mw.processor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialProcessor extends AnchorPane implements Initializable{
	private AppMain appl;
	
	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField pathInput;

	
	// �C�x���g�n���h��
	@FXML	protected void install(ActionEvent e) {
		System.out.println(e);
	}

	@FXML	protected void browse(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose digital camera photo path.");
		File file = directoryChooser.showDialog(null);
		if (file!=null) {
			pathInput.setText(file.getPath());
			// TODO �O��I�������p�X���L�����Ă����āA������ԂŃ��x���ɕ\���������B
		}
	}
	
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// do nothing.
	}

	public void setApplication(AppMain appMain) {
		appl = appMain;
	}

}
