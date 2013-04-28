package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.dao.MementoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SelectMementoTypeController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private static MementoDao mementoDao = new MementoDao();

	private AppMain appl;

	// 画面項目
	@FXML private ListView<String> listView;
	private ObservableList<String> listRecords = FXCollections.observableArrayList();

	// イベントハンドラ
	@FXML protected void cancel(ActionEvent e){	appl.fwdMainMenu(); }
	@FXML protected void clicked(MouseEvent e){
		// 選択行のTypeを取得し、次の画面に渡す。
		String record = listView.getSelectionModel().getSelectedItem();
		appl.fwdPublishedMementoList(record);
	}

	
	/**
	 * ListViewにメメントタイプ一覧を設定する
	 */
	private void fillTypeListView(){
		for(String category:mementoDao.findCategory()){
			logger.info("カテゴリ["+category+"]を追加します。");
			listRecords.add(category);
		}
		listView.setItems(listRecords);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {/* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk){
		this.appl = appMain;
		fillTypeListView();
	}
	
}
