package nobugs.nolife.mw;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.ScannedResult;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWFxmlError;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.ui.controller.MWSceneController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

// TODO LoggerのFormatter作成
// TODO javadoc
// TODO 単体テスト

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

	// 画面遷移
	public void fwdMainMenu() {	forward("ui/fxml/MainMenu.fxml"); }

	// Install Materials
	public void fwdInstallMaterial() { forward("ui/fxml/InstallMaterial.fxml"); }

	// Generate Mementos
	public void fwdInstalledMaterialList() { forward("ui/fxml/InstalledMaterialList.fxml"); }
	public void fwdMaterialEditor(Material material) {
		forward("ui/fxml/EditMaterial.fxml",(Object)material);
	}
	public void fwdGenerateConfirm() { forward("ui/fxml/GenerateConfirm.fxml"); }
	public void fwdGenerateResult() { forward("ui/fxml/GeneratedResult.fxml"); }

	// Modify Mementos
	public void fwdSelectMementoType() { forward("ui/fxml/SelectMementoType.fxml"); }
	public void fwdPublishedMementoList(String type) {
		forward("ui/fxml/PublishedMementoList.fxml",(Object)type);
	}
	public void fwdModifyMemento(Memento memento) {
		forward("ui/fxml/ModifyMemento.fxml",(Object)memento);
	}

	// Scan Material Usage
	public void fwdScannedMementos()  { forward("ui/fxml/ScannedMementos.fxml"); }
	public void fwdScanneMaterialDetail(ScannedResult sr) {
		forward("ui/fxml/ScannedMaterialDetail.fxml",(Object)sr);
	}

	// Archive
	public void fwdSelectArchiveMemento()  { forward("ui/fxml/SelectArchiveMemento.fxml"); }

	/**
	 * 画面遷移の実装
	 * @param fxml
	 * @throws MWException 
	 */
	private void forward(String fxml) {
		forward(fxml, "");
	}

	private void forward(String fxml, Object bulk) {
		logger.info(fxml+"へforwardします。伝播オブジェクトは["+bulk.toString()+"]です。");
		MWSceneController next;
		next = (MWSceneController) replaceSceneContent(fxml);
		next.setApplication(this, bulk);
	}
	/**
	 * 
	 * @param fxml
	 * @return fxmlで指定されたシーンに対応したコントローラをInitializableとして返却
	 * @throws MWException 
	 * @throws IOException 
	 */
	private Initializable replaceSceneContent(String fxml){
		FXMLLoader loader = new FXMLLoader();
		//InputStream in = AppMain.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(AppMain.class.getResource(fxml));
		AnchorPane page = null;

		try(InputStream in = AppMain.class.getResourceAsStream(fxml)) {
			page = (AnchorPane) loader.load(in);
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
		
		Scene scene = new Scene(page, 800, 600);
		stage.setScene(scene);
		stage.sizeToScene();
		Object controller = loader.getController();
		if(controller==null){
			logger.severe("controllerがNullです");
			throw new MWFxmlError("FXML定義["+fxml+"]に含まれるcontrollerがNullです");
		}
		logger.info("次画面のControllerは["+loader.getController().toString()+"]です。");
		return (Initializable) loader.getController();
	}
}
