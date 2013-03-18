package nobugs.nolife.mw;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.ui.controller.MWSceneController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

// TODO 例外設計
// TODO 画面のお化粧直し

public class AppMain extends Application {
	private Stage stage;
	private static Logger logger = Logger.getGlobal();

	// お約束
	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) {
		logger.setLevel(Level.INFO);
		stage = primaryStage;
		stage.setTitle("Memento Weaver");  // 画面のタイトルを設定
		fwdMainMenu();
		stage.show(); // 画面表示
	}

	// TODO 画面遷移系をforwardに一本化するか?
	public void fwdMainMenu() {	forward("ui/fxml/MainMenu.fxml"); }
	public void fwdInstallMaterial() { forward("ui/fxml/InstallMaterial.fxml"); }
	public void fwdStagingMaterial() { forward("ui/fxml/ListInstalledMaterial.fxml"); }

	public void fwdMaterialEditor(Material material) {
		forward("ui/fxml/EditMaterial.fxml",(Object)material);
	}
	public void fwdGenerateConfirm() { forward("ui/fxml/GenerateConfirm.fxml"); }

	
	/**
	 * 画面遷移の実装
	 * @param fxml
	 */
	private void forward(String fxml) {
		forward(fxml, "");
	}

	private void forward(String fxml, Object bulk) {
		MWSceneController next;
		try {
			next = (MWSceneController) replaceSceneContent(fxml);
			next.setApplication(this, bulk);
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
