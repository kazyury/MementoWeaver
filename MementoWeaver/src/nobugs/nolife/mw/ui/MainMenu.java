package nobugs.nolife.mw.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class MainMenu extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// ��ʂ̃^�C�g����ݒ�
		stage.setTitle("Memento Weaver ���C�����j���[");
		// FXML�t�@�C�������[�h
		Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		// �\�����e��ݒ�
		Scene scene = new Scene(root);
		stage.setScene(scene);

		// ��ʕ\��
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
