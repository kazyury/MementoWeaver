package nobugs.nolife.mw.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class MainMenu extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// 画面のタイトルを設定
		stage.setTitle("Memento Weaver メインメニュー");
		// FXMLファイルをロード
		Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		// 表示内容を設定
		Scene scene = new Scene(root);
		stage.setScene(scene);

		// 画面表示
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
