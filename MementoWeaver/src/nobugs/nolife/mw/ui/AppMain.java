package nobugs.nolife.mw.ui;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class AppMain extends Application {
	private Stage stage;

	// ����
	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		stage.setTitle("Memento Weaver");  // ��ʂ̃^�C�g����ݒ�
		fwdMainMenu();
		stage.show(); // ��ʕ\��
	}

	/**
	 * ��ʑJ�ڃ��\�b�h(MainMenu�ւ̑J��)
	 */
	void fwdMainMenu() {
		MainMenuController next;
		try {
			next = (MainMenuController) replaceSceneContent("MainMenu.fxml");
			next.setApplication(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʑJ�ڃ��\�b�h(InstallMaterial�ւ̑J��)
	 */
	void fwdInstallMaterial() {
		try {
			InstallMaterialController next = (InstallMaterialController) replaceSceneContent("InstallMaterial.fxml");
			next.setApplication(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param fxml
	 * @return fxml�Ŏw�肳�ꂽ�V�[���ɑΉ������R���g���[����Initializable�Ƃ��ĕԋp
	 * @throws IOException 
	 */
	private Initializable replaceSceneContent(String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = AppMain.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(AppMain.class.getResource(fxml));
		AnchorPane page;

		try {
			page = (AnchorPane) loader.load(in);
		} finally {
			in.close();
		}
		Scene scene = new Scene(page, 800, 600);
		stage.setScene(scene);
		stage.sizeToScene();
		return (Initializable) loader.getController();
	}
}
