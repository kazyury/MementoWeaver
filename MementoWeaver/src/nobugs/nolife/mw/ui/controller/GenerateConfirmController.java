package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
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
	private MementoGenerateProcessor processor = new MementoGenerateProcessor();

	/** �����Ώۃ������gListView */
	@FXML private ListView<String> generateListView;

	// ListView�ƘA������ObservableList
	private ObservableList<String> listRecords = FXCollections.observableArrayList();
	
	// �C�x���g�n���h��
	@FXML protected void cancel(ActionEvent e) throws MWException {appl.fwdListInstalledMaterial();}
	@FXML protected void generate(ActionEvent e) {
		// TODO ���̉�ʂւ̑J��
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		logger.fine("��������郁�����g����ObservableList�ɓo�^���܂�");
		try {
			for(String mementoName:processor.listAffectedMemento()){
				listRecords.add(mementoName);
			}
		} catch (MWException e) {
			e.printStackTrace();
		}

		logger.fine("ListView#setItems��ObservableList��ListView���֘A�t���܂�");
		generateListView.setItems(listRecords);
	}

	@Override
	public void setApplication(AppMain appMain, Object bulk) {
		appl = appMain;
	}
}
