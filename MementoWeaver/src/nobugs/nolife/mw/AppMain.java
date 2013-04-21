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

// TODO ��O�݌v
// TODO Logger��Formatter�쐬
// TODO javadoc
// TODO �P�̃e�X�g

public class AppMain extends Application {
	private Stage stage;
	private static Logger logger = Logger.getGlobal();

	// ����
	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage primaryStage) throws MWException {
		logger.setLevel(Level.INFO);
		stage = primaryStage;
		stage.setTitle("Memento Weaver");  // ��ʂ̃^�C�g����ݒ�
		fwdMainMenu();
		stage.show(); // ��ʕ\��
	}

	// ��ʑJ��
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
	 * ��ʑJ�ڂ̎���
	 * @param fxml
	 * @throws MWException 
	 */
	private void forward(String fxml) throws MWException {
		forward(fxml, "");
	}

	private void forward(String fxml, Object bulk) throws MWException {
		logger.info(fxml+"��forward���܂��B�`�d�I�u�W�F�N�g��["+bulk.toString()+"]�ł��B");
		MWSceneController next;
		try {
			next = (MWSceneController) replaceSceneContent(fxml);
			next.setApplication(this, bulk);
		} catch (MWException e) {
			e.printStackTrace();
			throw new MWException("��ʑJ�ڂŗ�O���������܂���",e.getCause());
		}
	}
	/**
	 * 
	 * @param fxml
	 * @return fxml�Ŏw�肳�ꂽ�V�[���ɑΉ������R���g���[����Initializable�Ƃ��ĕԋp
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
			logger.severe("controller��Null�ł�");
			return null;
		}
		logger.info("����ʂ�Controller��["+loader.getController().toString()+"]�ł��B");
		return (Initializable) loader.getController();
	}


}
