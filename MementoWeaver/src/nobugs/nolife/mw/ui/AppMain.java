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

	// お約束
	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		stage.setTitle("Memento Weaver");  // 画面のタイトルを設定
		fwdMainMenu();
		stage.show(); // 画面表示
	}

	/**
	 * 画面遷移メソッド(MainMenuへの遷移)
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
	 * 画面遷移メソッド(InstallMaterialへの遷移)
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
	 * @return fxmlで指定されたシーンに対応したコントローラをInitializableとして返却
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
