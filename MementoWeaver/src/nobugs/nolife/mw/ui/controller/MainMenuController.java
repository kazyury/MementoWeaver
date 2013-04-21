package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MainMenuController extends AnchorPane implements MWSceneController {
	private AppMain appl;
	
	// イベントハンドラ
	@FXML	protected void installMaterial(ActionEvent e) throws MWException {appl.fwdInstallMaterial();}
	@FXML	protected void generateMemento(ActionEvent e) throws MWException {appl.fwdInstalledMaterialList();}
	@FXML	protected void modifyMemento(ActionEvent e) throws MWException {appl.fwdSelectMementoType();}
	@FXML	protected void archive(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void scanMaterial(ActionEvent e) throws MWException {appl.fwdScannedMementos();}
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
