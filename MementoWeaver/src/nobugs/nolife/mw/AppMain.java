package nobugs.nolife.mw;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.ui.controller.MWSceneController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

// TODO 例外設計
// TODO LoggerのFormatter作成
// TODO javadoc
// TODO 単体テスト

public class AppMain extends Application {
	private Stage stage;
	private static Logger logger = Logger.getGlobal();

	// お約束
	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws MWException {
		logger.setLevel(Level.INFO);
		stage = primaryStage;
		stage.setTitle("Memento Weaver");  // 画面のタイトルを設定
		fwdMainMenu();
		stage.show(); // 画面表示
	}

	// 画面遷移
	public void fwdMainMenu() throws MWException {	forward("ui/fxml/MainMenu.fxml"); }
	
	// Install Materials
	public void fwdInstallMaterial() throws MWException { forward("ui/fxml/InstallMaterial.fxml"); }
	
	// Generate Mementos
	public void fwdInstalledMaterialList() throws MWException { forward("ui/fxml/InstalledMaterialList.fxml"); }
	public void fwdMaterialEditor(Material material) throws MWException {
		forward("ui/fxml/EditMaterial.fxml",(Object)material);
	}
	public void fwdGenerateConfirm() throws MWException { forward("ui/fxml/GenerateConfirm.fxml"); }
	public void fwdGenerateResult() throws MWException { forward("ui/fxml/GeneratedResult.fxml"); }

	// Modify Mementos
	public void fwdSelectMementoType() throws MWException { forward("ui/fxml/SelectMementoType.fxml"); }
	public void fwdPublishedMementoList(String type) throws MWException {
		forward("ui/fxml/PublishedMementoList.fxml",(Object)type);
	}
	public void fwdModifyMemento(Memento memento) throws MWException {
		forward("ui/fxml/ModifyMemento.fxml",(Object)memento);
	}

	// Scan Material Usage
	public void fwdScannedMementos() throws MWException  { forward("ui/fxml/ScannedMementos.fxml"); }

	/**
	 * 画面遷移の実装
	 * @param fxml
	 * @throws MWException 
	 */
	private void forward(String fxml) throws MWException {
		forward(fxml, "");
	}

	private void forward(String fxml, Object bulk) throws MWException {
		logger.info(fxml+"へforwardします。伝播オブジェクトは["+bulk.toString()+"]です。");
		MWSceneController next;
		try {
			next = (MWSceneController) replaceSceneContent(fxml);
			next.setApplication(this, bulk);
		} catch (MWException e) {
			e.printStackTrace();
			throw new MWException("画面遷移で例外が発生しました",e.getCause());
		}
	}
	/**
	 * 
	 * @param fxml
	 * @return fxmlで指定されたシーンに対応したコントローラをInitializableとして返却
	 * @throws MWException 
	 * @throws IOException 
	 */
	private Initializable replaceSceneContent(String fxml) throws MWException{
		FXMLLoader loader = new FXMLLoader();
		InputStream in = AppMain.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(AppMain.class.getResource(fxml));
		AnchorPane page = null;

		try {
			page = (AnchorPane) loader.load(in);
		} catch (IOException e) {
			throw new MWException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new MWException(e);
			}
		}
		Scene scene = new Scene(page, 800, 600);
		stage.setScene(scene);
		stage.sizeToScene();
		Object controller = loader.getController();
		if(controller==null){
			logger.severe("controllerがNullです");
			return null;
		}
		logger.info("次画面のControllerは["+loader.getController().toString()+"]です。");
		return (Initializable) loader.getController();
	}


}
