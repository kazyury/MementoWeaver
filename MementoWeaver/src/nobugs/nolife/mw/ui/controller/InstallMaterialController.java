package nobugs.nolife.mw.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.processing.InstallProcessor;
import nobugs.nolife.mw.util.PropertyUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialController extends AnchorPane implements MWSceneController{
	private AppMain appl;
	private String materialSourcePath = null; // プロパティファイルより
	private String stagingAreaPath = null;    // プロパティファイルより
	private PropertyUtil propertyUtil = new PropertyUtil();

	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField pathInput;

	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {
		InstallProcessor processor = new InstallProcessor();
		processor.installProcess(pathInput.getText(), stagingAreaPath);

		// 今回のpathInputをプロパティにセットして保管
		propertyUtil.storeMaterialSourceCache(pathInput.getText());

		appl.fwdStagingMaterial();
	}

	@FXML	protected void browse(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose digital camera photo path.");
		File file = directoryChooser.showDialog(null);
		if (file!=null) {
			pathInput.setText(file.getPath());
		}
	}

	@FXML	protected void exit(ActionEvent e) {
		Platform.exit();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// プロパティの読み込み
		materialSourcePath = propertyUtil.getMaterialSourceName();
		stagingAreaPath = propertyUtil.getStagingAreaName();
		// プロパティに指定された素材ソースを初期設定
		pathInput.setText(materialSourcePath);
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
	}
}
