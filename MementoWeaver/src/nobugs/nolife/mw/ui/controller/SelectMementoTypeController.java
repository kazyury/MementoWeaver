package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.processing.ModifyMementoProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SelectMementoTypeController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private ModifyMementoProcessor processor = new ModifyMementoProcessor();

	private AppMain appl;

	// ��ʍ���
	@FXML private ListView<String> listView;
	private ObservableList<String> listRecords = FXCollections.observableArrayList();

	// �C�x���g�n���h��
	@FXML protected void cancel(ActionEvent e) throws MWException {	appl.fwdMainMenu(); }
	@FXML protected void clicked(MouseEvent e) throws MWException {
		// �I���s��Type���擾���A���̉�ʂɓn���B
		String record = listView.getSelectionModel().getSelectedItem();
		appl.fwdPublishedMementoList(record);
	}

	
	/**
	 * ListView�Ƀ������g�^�C�v�ꗗ��ݒ肷��
	 */
	private void fillTypeListView(){
		for(String category:processor.findMementoCategoryProcess()){
			logger.info("�J�e�S��["+category+"]��ǉ����܂��B");
			listRecords.add(category);
		}
		listView.setItems(listRecords);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {/* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk) throws MWException {
		this.appl = appMain;
		fillTypeListView();
	}
	
}
