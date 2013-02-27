package nobugs.nolife.mw.processor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialProcessor extends AnchorPane implements Initializable{
	private AppMain appl;
	
	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField pathInput;

	
	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {
		System.out.println(e);
	}

	@FXML	protected void browse(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose digital camera photo path.");
		File file = directoryChooser.showDialog(null);
		if (file!=null) {
			pathInput.setText(file.getPath());
			// TODO 前回選択したパスを記憶しておいて、初期状態でラベルに表示したい。
		}
	}
	
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// do nothing.
	}

	public void setApplication(AppMain appMain) {
		appl = appMain;
	}

}
