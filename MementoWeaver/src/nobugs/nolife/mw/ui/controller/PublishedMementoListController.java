package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.processing.FindMementoProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class PublishedMementoListController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private FindMementoProcessor processor = new FindMementoProcessor();

	private AppMain appl;
	private String category;

	// ��ʍ���
	@FXML private Label typeLabel;
	@FXML private ListView<Memento> listView;
	private ObservableList<Memento> listRecords = FXCollections.observableArrayList();

	// �C�x���g�n���h��
	@FXML protected void cancel(ActionEvent e) throws MWException {	appl.fwdMainMenu(); }
	@FXML protected void clicked(MouseEvent e) throws MWException {
		// �I���s��Memento���擾���A���̉�ʂɓn���B
		Memento memento = listView.getSelectionModel().getSelectedItem();
		appl.fwdModifyMemento(memento);
	}


	/**
	 * ListView�Ƀ������g�ꗗ��ݒ肷��
	 */
	private void fillMementoListView(String category){
		for(Memento me:processor.findMementoProcess(category)){
			logger.info("�������g["+me+"]��ǉ����܂��B");
			listRecords.add(me);
		}
		listView.setItems(listRecords);
		listView.setCellFactory(new Callback<ListView<Memento>, ListCell<Memento>>() {
			@Override
			public ListCell<Memento> call(ListView<Memento> arg0) {
				return new MementoCell();
			}
		});
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk) throws MWException {
		this.appl = appMain;
		category = (String)bulk;
		typeLabel.setText(typeLabel.getText()+category); // ���x��������ɃJ�e�S����ǋL
		fillMementoListView(this.category);
	}

	/**
	 * ListView<Memento>�\���p�̃Z��
	 * @author kazyury
	 */
	private static class MementoCell extends ListCell<Memento> {
		@Override
		protected void updateItem(Memento me, boolean empty) {
			super.updateItem(me, empty);
			if(!empty) {
				setText(me.getProductionPath());
			}
		}
	}

}
