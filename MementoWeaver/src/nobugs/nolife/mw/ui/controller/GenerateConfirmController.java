package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.processing.MementoGenerateProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class GenerateConfirmController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private AppMain appl;
	private List<Generator> generatorList;


	/** 生成対象メメントListView */
	@FXML private ListView<String> generateListView;

	// ListViewと連動するObservableList
	private ObservableList<String> listRecords = FXCollections.observableArrayList();

	// イベントハンドラ
	@FXML protected void cancel(ActionEvent e) throws MWException {appl.fwdListInstalledMaterial();}
	@FXML protected void generate(ActionEvent e) throws MWException {appl.fwdGenerateResult();}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		MementoGenerateProcessor processor = new MementoGenerateProcessor();
		try {
			generatorList=processor.getGeneratorList();
		} catch (MWException e1) {
			e1.printStackTrace();
		}

		logger.fine("生成されるメメント名をObservableListに登録します");
		for(Generator generator:generatorList){
			for(String memento:generator.getPreparedMemento()){
				listRecords.add(memento);
			}
		}

		logger.fine("ListView#setItemsでObservableListとListViewを関連付けます");
		generateListView.setItems(listRecords);
	}

	@Override
	public void setApplication(AppMain appMain, Object bulk) {
		appl = appMain;
	}
}
