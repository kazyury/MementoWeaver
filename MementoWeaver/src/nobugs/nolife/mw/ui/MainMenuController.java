package nobugs.nolife.mw.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainMenuController {
	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要

	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {
		System.out.println(e);
	}
	@FXML	protected void generate(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void modify(ActionEvent e) {} // TODO not implemented yet
	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet
}
