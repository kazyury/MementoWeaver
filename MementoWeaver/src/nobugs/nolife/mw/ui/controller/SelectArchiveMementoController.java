package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import name.antonsmirnov.javafx.dialog.Dialog;
import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.dao.MementoDao;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.processing.ArchiveProcessor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class SelectArchiveMementoController extends AnchorPane implements MWSceneController {
	private static MementoDao mementoDao = new MementoDao();
	private AppMain appl;

	// 画面コントロール
	@FXML private Button archive;
	@FXML private Button cancel;
	@FXML private TableView<TableRecord> tableView;
	@FXML private TableColumn<TableRecord,Boolean> archiveChkCol;
	@FXML private TableColumn<TableRecord,String> mementoCol;
	@FXML private TableColumn<TableRecord,String> pathCol;
	private ObservableList<TableRecord> tableRecord = FXCollections.observableArrayList();

	// イベントハンドラ
	@FXML protected void archive(ActionEvent e){
		// tableRecord から 対象List<Memento>への詰め替え
		List<Memento> mementoList = new ArrayList<>();
		
		Iterator<TableRecord> iterator = tableRecord.iterator();
		while(iterator.hasNext()) {
			TableRecord record = iterator.next();
			// チェックされたrecordのmementoのみを対象に
			if(record.checkedProperty().get()) {
				mementoList.add(record.getMemento());
			}
		}
		ArchiveProcessor processor = new ArchiveProcessor();
		processor.archive(mementoList);
		Dialog.showInfo(getId(), "指定されたメメントはアーカイブされました");
		appl.fwdMainMenu();
	}

	@FXML protected void cancel(ActionEvent e){appl.fwdMainMenu();}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {/* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk){
		this.appl = appMain;
		
		archiveChkCol.setCellValueFactory(new PropertyValueFactory<TableRecord, Boolean>("checked"));
		archiveChkCol.setCellFactory(new Callback<TableColumn<TableRecord,Boolean>, TableCell<TableRecord,Boolean>>() {
			@Override
			public TableCell<TableRecord, Boolean> call(TableColumn<TableRecord, Boolean> arg0) {
				return new CheckBoxTableCell<TableRecord, Boolean>();
			}
		});
		mementoCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("mementoId"));
		pathCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("path"));
		tableView.setItems(tableRecord);

		// tableRecord 追加
		for(Memento memento:mementoDao.findAll()){
			tableRecord.add(new TableRecord(memento));
		}
	}

	public static class TableRecord {
		private BooleanProperty checked;
		private StringProperty mementoId;
		private StringProperty path;
		private Memento memento;

		private TableRecord(Memento memento){
			this.memento = memento;
			this.checked = new SimpleBooleanProperty(false);
			this.mementoId = new SimpleStringProperty(memento.getMementoId());
			this.path = new SimpleStringProperty(memento.getProductionPath());
		}

		public StringProperty pathProperty() { return path; }
		public StringProperty mementoIdProperty() { return mementoId; }
		public BooleanProperty checkedProperty() { return checked; }
		
		public Memento getMemento() { return memento; }
	}
}
