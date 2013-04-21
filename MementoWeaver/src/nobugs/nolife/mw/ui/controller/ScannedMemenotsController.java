package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.processing.ScanProcessor;
import nobugs.nolife.mw.util.Constants;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class ScannedMemenotsController extends AnchorPane implements MWSceneController  {
	private static Logger logger = Logger.getGlobal();
	private static ScanProcessor processor = new ScanProcessor();
	private AppMain appl;

	// ��ʃR���g���[��
	@FXML private Button scan;
	@FXML private Button cancel;
	@FXML private ComboBox<String> targetType;
	private ObservableList<String> targetTypeList = FXCollections.observableArrayList(Constants.SCANTYPE_ALL,Constants.SCANTYPE_NOTSCANNED,Constants.SCANTYPE_SCANNED);

	@FXML private TableView<TableRecord> tableView;
	@FXML private TableColumn<TableRecord,String> pathCol;
	@FXML private TableColumn<TableRecord,String> statusCol;
	@FXML private TableColumn<TableRecord,String> materialNumCol;
	@FXML private TableColumn<TableRecord,String> lastScannedCol;
	private ObservableList<TableRecord> tableRecord = FXCollections.observableArrayList();

	// �C�x���g�n���h��
	// �e�[�u���r���[�̃N���b�N
	@FXML protected void clicked(MouseEvent e) throws MWException {
		// �I���s��TableRecord���擾���A���̉�ʂɓn���B
		TableRecord record = tableView.getSelectionModel().getSelectedItem();
		// TODO not implemented
	}
	
	@FXML protected void scan(ActionEvent e) throws MWException {
		// �X�L�������[�h��ݒ�
		processor.setScanMode(targetType.getValue());
		// �X�L�������Č��ʂ�tableView�ɕ\��
		for(String scanned:processor.scan()) {
			tableRecord.add(new TableRecord(scanned, "", "", ""));
		}

	}

	@FXML protected void cancel(ActionEvent e) throws MWException {appl.fwdMainMenu();}

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk) throws MWException {
		this.appl = appMain;
		// �R���{�{�b�N�X�A�C�e���ݒ�
		targetType.setItems(targetTypeList);
		targetType.setValue(Constants.SCANTYPE_ALL);

		pathCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("path"));
		statusCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("status"));
		materialNumCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("materialNum"));
		lastScannedCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("lastScanned"));
		tableView.setItems(tableRecord);
	}

	/**
	 * �X�L�����ꗗ��1�s��\��TableRecord
	 * @author kazyury
	 *
	 */
	public static class TableRecord {
		private StringProperty path;
		private StringProperty status;
		private StringProperty materialNum;
		private StringProperty lastScanned;

		private TableRecord(String path, String status, String materialNum, String lastScanned){
			this.path = new SimpleStringProperty(path);
			this.status = new SimpleStringProperty(status);
			this.materialNum = new SimpleStringProperty(materialNum);
			this.lastScanned = new SimpleStringProperty(lastScanned);
		}
		
		public StringProperty pathProperty() { return path; }
		public StringProperty statusProperty() { return status; }
		public StringProperty materialNumProperty() { return materialNum; }
		public StringProperty lastScannedProperty() { return lastScanned; }
	}
}
