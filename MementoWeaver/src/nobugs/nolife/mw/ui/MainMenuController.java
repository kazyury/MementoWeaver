package nobugs.nolife.mw.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainMenuController {
	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v

	// �C�x���g�n���h��
	@FXML	protected void install(ActionEvent e) {
		System.out.println(e);
	}
	@FXML	protected void generate(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void modify(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet
}
