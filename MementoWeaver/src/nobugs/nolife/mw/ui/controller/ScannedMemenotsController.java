package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import name.antonsmirnov.javafx.dialog.Dialog;
import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.entities.ScannedResult;
import nobugs.nolife.mw.processing.ScanProcessor;
import nobugs.nolife.mw.processing.ScannedResultProcessor;
import nobugs.nolife.mw.util.Constants;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class ScannedMemenotsController extends AnchorPane implements MWSceneController  {
	private static Logger logger = Logger.getGlobal();
	private AppMain appl;

	// 画面コントロール
	@FXML private Button scan;
	@FXML private Button cancel;
	@FXML private ComboBox<String> targetType;
	private ObservableList<String> targetTypeList = FXCollections.observableArrayList(Constants.SCANTYPE_ALL,Constants.SCANTYPE_NOT_IGNORED,Constants.SCANTYPE_IGNORED);

	@FXML private TableView<TableRecord> tableView;
	@FXML private TableColumn<TableRecord,String> pathCol;
	@FXML private TableColumn<TableRecord,Boolean> ignoreCol;
	@FXML private TableColumn<TableRecord,String> lastScannedCol;
	@FXML private TableColumn<TableRecord,String> statusCol;
	private ObservableList<TableRecord> tableRecord = FXCollections.observableArrayList();

	// イベントハンドラ
	// テーブルビューのクリック
	@FXML protected void clicked(MouseEvent e){
		// 選択行のTableRecordを取得し、次の画面に渡す。
		TableRecord record = tableView.getSelectionModel().getSelectedItem();
		if(record == null){
			// 列ヘッダをクリックしてもclickedがfireされるのでNullPointerが発生してしまうのを回避
			return ;
		}
		ScannedResult sr = record.getScannedResult();
		if(record.ignoreProperty().get()) {
			sr.setIgnored(Constants.SCAN_IGNORED);
		} else {
			sr.setIgnored(Constants.SCAN_NOT_IGNORED);
		}
		logger.fine("Selected:path["+sr.getProductionPath()+"],ignored["+sr.getIgnored()+"],memento["+sr.getMemento()+"],lastScanned["+sr.getLastScanned()+"]");
		appl.fwdScanneMaterialDetail(sr);
	}
	
	@FXML protected void scan(ActionEvent e){
		ScanProcessor processor = new ScanProcessor();
		
		// スキャンモードを設定
		processor.setScanMode(targetType.getValue());
		// スキャンして結果をtableViewに再表示
		tableRecord.clear();
		for(ScannedResult scanned:processor.scanMementoFiles()) {
			tableRecord.add(new TableRecord(scanned));
		}
	}

	@FXML protected void update(ActionEvent e){
		
		// ObservableList<TableRecord>からList<ScannedResult>への詰め替え
		Iterator<TableRecord> iterator = tableRecord.iterator();
		List<ScannedResult> scannedResultList = new ArrayList<ScannedResult>();
		while(iterator.hasNext()){
			TableRecord record = iterator.next();
			ScannedResult sr = record.getScannedResult();
			if(record.ignoreProperty().get()) {
				sr.setIgnored(Constants.SCAN_IGNORED);
			} else {
				sr.setIgnored(Constants.SCAN_NOT_IGNORED);
			}
			scannedResultList.add(sr);
		}
		
		ScannedResultProcessor processor = new ScannedResultProcessor();
		processor.updateIgnored(scannedResultList);
		Dialog.showInfo("udpateIgnored success", "Ignored の更新を完了しました.");
	}
	
	@FXML protected void cancel(ActionEvent e){appl.fwdMainMenu();}

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk){
		this.appl = appMain;
		// コンボボックスアイテム設定
		targetType.setItems(targetTypeList);
		targetType.setValue(Constants.SCANTYPE_ALL);

		pathCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("path"));
		ignoreCol.setCellValueFactory(new PropertyValueFactory<TableRecord, Boolean>("ignore"));
		ignoreCol.setCellFactory(new Callback<TableColumn<TableRecord,Boolean>, TableCell<TableRecord,Boolean>>() {
			@Override
			public TableCell<TableRecord, Boolean> call(TableColumn<TableRecord, Boolean> arg0) {
				return new CheckBoxTableCell<TableRecord, Boolean>();
			}
		});
		statusCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("status"));
		lastScannedCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("lastScanned"));
		tableView.setItems(tableRecord);
		tableView.setEditable(true);
	}

	/**
	 * スキャン一覧の1行を表すTableRecord
	 * @author kazyury
	 *
	 */
	public static class TableRecord {
		private StringProperty path;
		private StringProperty status;
		private BooleanProperty ignore;
		private StringProperty lastScanned;
		private ScannedResult scannedResult;

		private TableRecord(ScannedResult result){
			this.scannedResult = result;
			this.path = new SimpleStringProperty(result.getProductionPath());
			this.status = new SimpleStringProperty(""); // TODO not implemented
			if(result.getIgnored().equals(Constants.SCAN_IGNORED)){
				this.ignore = new SimpleBooleanProperty(true);
			} else {
				this.ignore = new SimpleBooleanProperty(false);
			}
			Date lastScanned = result.getLastScanned();
			if(lastScanned!=null){
				this.lastScanned = new SimpleStringProperty(lastScanned.toString());
			}else{
				this.lastScanned = new SimpleStringProperty("");
			}
		}
		
		public StringProperty pathProperty() { return path; }
		public StringProperty statusProperty() { return status; }
		public BooleanProperty ignoreProperty() { return ignore; }
		public StringProperty lastScannedProperty() { return lastScanned; }
		
		public ScannedResult getScannedResult() {
			return scannedResult;
		}
	}
}
