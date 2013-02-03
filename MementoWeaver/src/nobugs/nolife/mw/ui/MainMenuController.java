package nobugs.nolife.mw.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class MainMenuController extends AnchorPane implements Initializable {
	private AppMain appl;
	
	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要

	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {
		appl.fwdInstallMaterial();
	}
	@FXML	protected void generate(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void modify(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// do nothing.
	}

	public void setApplication(AppMain appMain) {
		appl = appMain;
	}
}
