package nobugs.nolife.mw.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import name.antonsmirnov.javafx.dialog.Dialog;
import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.exceptions.MWInvalidUserInputException;
import nobugs.nolife.mw.processing.InstallProcessor;
import nobugs.nolife.mw.util.CacheManager;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialController extends AnchorPane implements MWSceneController{
	private static String materialSourcePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MATERIAL_SOURCE);
	private static String stagingAreaPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_STAGING_AREA);
	private AppMain appl;

	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField pathInput;

	// イベントハンドラ
	@FXML	protected void install(ActionEvent e){
		InstallProcessor processor = new InstallProcessor();
		try {
			processor.installProcess(pathInput.getText(), stagingAreaPath);
		} catch (MWInvalidUserInputException ex) {
			Dialog.showError(getId(), ex.getMessage());
			return;
		}

		// 今回のpathInputをプロパティにセットして保管
		CacheManager.storeMaterialSourceCache(pathInput.getText());

		appl.fwdInstalledMaterialList();
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
		// プロパティに指定された素材ソースを初期設定
		pathInput.setText(materialSourcePath);
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
	}
}
