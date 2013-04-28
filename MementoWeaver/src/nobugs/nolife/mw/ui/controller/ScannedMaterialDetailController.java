package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import name.antonsmirnov.javafx.dialog.Dialog;
import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.dao.MementoDao;
import nobugs.nolife.mw.dao.TagConfigDao;
import nobugs.nolife.mw.dto.ScannedMaterialDTO;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.ScannedResult;
import nobugs.nolife.mw.processing.ScanProcessor;
import nobugs.nolife.mw.processing.ScannedResultProcessor;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class ScannedMaterialDetailController extends AnchorPane implements MWSceneController  {
	private static MementoDao mementoDao = new MementoDao();
	private static TagConfigDao tagConfigDao = new TagConfigDao();
	private AppMain appl;
	private ScannedResult scannedResult;

	// 画面コントロール
	@FXML private Button manage;
	@FXML private Button cancel;
	@FXML private Label pathLabel;
	@FXML private ComboBox<String> categoryCombo;
	@FXML private ComboBox<String> tagCombo;
	private ObservableList<String> categoryComboList = FXCollections.observableArrayList();
	private ObservableList<String> tagComboList = FXCollections.observableArrayList();

	@FXML private TableView<TableRecord> tableView;
	@FXML private TableColumn<TableRecord,Boolean> materialCol;
	@FXML private TableColumn<TableRecord,Boolean> taggedMaterialCol;
	@FXML private TableColumn<TableRecord,String> pathCol;
	private ObservableList<TableRecord> tableRecord = FXCollections.observableArrayList();

	// イベントハンドラ
	@FXML protected void manage(ActionEvent e){
		// Comboの入力チェック
		if(categoryCombo.getValue() == null || categoryCombo.getValue().equals("")) {
			Dialog.showError("Choose Category", "カテゴリを選択又は入力してください");
			return;
		}
		if(tagCombo.getValue() == null || tagCombo.getValue().equals("")) {
			Dialog.showError("Choose tag", "タグを選択してください");
			return;
		}
		
		// tableRecordからの詰め替え
		List<ScannedMaterialDTO> scannedMaterialList = new ArrayList<>();
		Iterator<TableRecord> iterator = tableRecord.iterator();
		while(iterator.hasNext()){
			TableRecord record = iterator.next();
			scannedMaterialList.add(record.getScannedMaterialDTO());
		}
		ScannedResultProcessor processor = new ScannedResultProcessor();
		processor.manageScannedMaterialProcess(scannedResult,scannedMaterialList,categoryCombo.getValue(),tagCombo.getValue());
		Dialog.showInfo(getId(), "素材スキャン結果を保管しました");
		appl.fwdScannedMementos();
	}

	@FXML protected void cancel(ActionEvent e){appl.fwdScannedMementos();}

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk){
		this.appl = appMain;
		this.scannedResult = (ScannedResult)bulk;
		Memento memento = scannedResult.getMemento();

		// ラベル設定
		pathLabel.setText(pathLabel.getText()+scannedResult.getProductionPath());
		
		// memento category コンボ設定
		for(String category:mementoDao.findCategory()){
			categoryComboList.add(category);
		}
		categoryCombo.setItems(categoryComboList);
		categoryCombo.setEditable(true);
		
		// tag コンボ設定
		for(String tag:tagConfigDao.findTagList()){
			tagComboList.add(tag);
		}
		tagCombo.setItems(tagComboList);
		
		// scannedResultにmementoが設定されていたら、そのmementoのcategory, tagを設定してDisableに
		if(memento!=null){
			categoryCombo.setValue(memento.getCategory());
			categoryCombo.setDisable(true);
			tagCombo.setValue(memento.getTaggedMaterials().get(0).getId().getTag());
			tagCombo.setDisable(true);
		}
		
		pathCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("path"));
		materialCol.setCellValueFactory(new PropertyValueFactory<TableRecord, Boolean>("isMaterial"));
		materialCol.setCellFactory(new Callback<TableColumn<TableRecord,Boolean>, TableCell<TableRecord,Boolean>>() {
			@Override
			public TableCell<TableRecord, Boolean> call(TableColumn<TableRecord, Boolean> arg0) {
				return new CheckBoxTableCell<TableRecord, Boolean>();
			}
		});
		taggedMaterialCol.setCellValueFactory(new PropertyValueFactory<TableRecord, Boolean>("isBelongToMemento"));
		taggedMaterialCol.setCellFactory(new Callback<TableColumn<TableRecord,Boolean>, TableCell<TableRecord,Boolean>>() {
			@Override
			public TableCell<TableRecord, Boolean> call(TableColumn<TableRecord, Boolean> arg0) {
				return new CheckBoxTableCell<TableRecord, Boolean>();
			}
		});
		tableView.setItems(tableRecord);
		
		ScanProcessor processor = new ScanProcessor();
		for(ScannedMaterialDTO scannedMaterial:processor.scanMaterialsIn(scannedResult)) {
			tableRecord.add(new TableRecord(scannedMaterial));
		}
	}

	/**
	 * スキャンされた素材一覧の1行を表すTableRecord
	 * @author kazyury
	 *
	 */
	public static class TableRecord {
		private StringProperty path;
		private BooleanProperty isMaterial;
		private BooleanProperty isBelongToMemento;
		private ScannedMaterialDTO scannedMaterialDto;

		private TableRecord(ScannedMaterialDTO scannedMaterial){
			this.scannedMaterialDto = scannedMaterial;
			this.path = new SimpleStringProperty(scannedMaterial.getPath().toString());
			this.isMaterial = new SimpleBooleanProperty(scannedMaterial.isRegisteredMaterial());
			this.isBelongToMemento = new SimpleBooleanProperty(scannedMaterial.isBelongToMemento());
		}
		
		public StringProperty pathProperty() { return path; }
		public BooleanProperty isMaterialProperty() { return isMaterial; }
		public BooleanProperty isBelongToMementoProperty() { return isBelongToMemento; }
		public ScannedMaterialDTO getScannedMaterialDTO() { return scannedMaterialDto; }
	}
}
