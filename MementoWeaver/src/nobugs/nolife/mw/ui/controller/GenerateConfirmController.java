package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.processing.MementoGenerateProcessor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class GenerateConfirmController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private AppMain appl;
	private MementoGenerateProcessor processor = new MementoGenerateProcessor();

	/** 生成対象メメントListView */
	@FXML private ListView<String> generateListView;

	// ListViewと連動するObservableList
	private ObservableList<String> listRecords = FXCollections.observableArrayList();
	
	// イベントハンドラ
	@FXML protected void exit(ActionEvent e) {Platform.exit();}
	@FXML protected void generate(ActionEvent e) {
		// TODO 次の画面への遷移
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		logger.fine("生成されるメメント名をObservableListに登録します");
		for(String mementoName:processor.listAffectedMemento()){
			listRecords.add(mementoName);
		}

		logger.fine("ListView#setItemsでObservableListとListViewを関連付けます");
		generateListView.setItems(listRecords);
	}

	@Override
	public void setApplication(AppMain appMain, Object bulk) {
		appl = appMain;
	}
}
