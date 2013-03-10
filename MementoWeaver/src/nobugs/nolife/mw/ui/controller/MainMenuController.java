package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MainMenuController extends AnchorPane implements MWSceneController {
	private AppMain appl;
	
	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要

	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {appl.fwdInstallMaterial();}
	@FXML	protected void generate(ActionEvent e) {appl.fwdStagingMaterial();}
	@FXML	protected void modify(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {Platform.exit();}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// do nothing.
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
	}
}
