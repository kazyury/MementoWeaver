package nobugs.nolife.mw.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class InstallMaterialController extends AnchorPane implements Initializable{
	private AppMain appl;
	
	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField pathInput;

	
	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {
		System.out.println(e);
	}
	@FXML	protected void browse(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

	public void setApplication(AppMain appMain) {
		appl = appMain;
	}

}
