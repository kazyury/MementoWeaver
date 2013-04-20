package nobugs.nolife.mw.ui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import name.antonsmirnov.javafx.dialog.Dialog;
import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.processing.ModifyMementoProcessor;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class ModifyMementoController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private ModifyMementoProcessor processor = new ModifyMementoProcessor();

	private AppMain appl;
	private Memento memento;

	// 画面項目
	@FXML private Label mementoLabel;
	@FXML private TableView<TableRecord> tableView;
	@FXML private TableColumn<TableRecord,String> idCol;
	@FXML private TableColumn<TableRecord,String> imageCol;
	@FXML private TableColumn<TableRecord,String> memoCol;
	private ObservableList<TableRecord> tableRecord = FXCollections.observableArrayList();

	// イベントハンドラ
	@FXML protected void append(ActionEvent e) throws MWException {
		// FileChooserの表示
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Material");
		fileChooser.setInitialDirectory(new File(PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL)));
		File materialFile = fileChooser.showOpenDialog(null);

		// 選択された素材に対応するTaggedMaterialを作成してtableRecordに追加
		if(materialFile!=null){
			logger.info(materialFile.toString()+"が選択されました。");
			String tag = memento.getTaggedMaterials().get(0).getId().getTag();
			try {
				TaggedMaterial tm = processor.appendTaggedMaterialProcess(memento, materialFile, tag);
				if(tm==null){
					Dialog.showError("Append Material Error", "選択された素材は既にメメント内で使用されています。");
				} else {
					tableRecord.add(new TableRecord(tm));
					memento.getTaggedMaterials().add(tm);
				}
			} catch (MWException ex) {
				Dialog.showError("Append Material Error", "選択された素材はDBに登録されていません。Scanを実行してください。");
			}
		}
	}

	@FXML protected void remove(ActionEvent e) throws MWException {
		int idx = tableView.getSelectionModel().getSelectedIndex();
		TableRecord deleted = tableRecord.remove(idx);
		deleted.getTaggedMaterial().setTagState(Constants.TAG_STATE_NOT_IN_USE);
	}
	
	@FXML protected void submit(ActionEvent e) throws MWException {
		processor.mementoSubmitProcess(memento);
		Dialog.showInfo("Memento was Modified", "Memento["+memento.getMementoId()+"]は正常に更新されました。");
		appl.fwdMainMenu();
	}
	
	@FXML protected void cancel(ActionEvent e) throws MWException {	appl.fwdMainMenu(); }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */ }

	@Override
	public void setApplication(AppMain appMain, Object bulk) throws MWException {
		appl = appMain;
		memento = (Memento)bulk;
		fillTable();
		mementoLabel.setText(mementoLabel.getText()+" "+memento.getMementoId()); // label更新
	}

	/**
	 * mementoの関連するTaggedMaterialをTableViewにセットする
	 * FIXME 全てのTaggedMaterialを表示しているが、タグ状態に応じて制御する必要がある
	 * @throws MWException
	 */
	private void fillTable() throws MWException {
		for(TaggedMaterial tm:memento.getTaggedMaterials()){
			logger.info("素材["+tm.getId().getMaterialId()+"]を追加します");
			tableRecord.add(new TableRecord(tm));
		}

		idCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("id"));
		imageCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("image"));
		memoCol.setCellValueFactory(new PropertyValueFactory<TableRecord, String>("memo"));

		// imageCol は画像を表示できるように
		imageCol.setCellFactory(new Callback<TableColumn<TableRecord,String>, TableCell<TableRecord,String>>() {
			@Override
			public TableCell<TableRecord, String> call(TableColumn<TableRecord, String> arg0) {
				return new ImageColCell();
			}
		});

		// memoCol はEditableに
		memoCol.setCellFactory(new Callback<TableColumn<TableRecord,String>, TableCell<TableRecord,String>>() {
			@Override
			public TableCell<TableRecord, String> call(TableColumn<TableRecord, String> arg0) {
				return new TextFieldTableCell<TableRecord, String>(new DefaultStringConverter());
			}
		});

		memoCol.setOnEditCommit(new EventHandler<CellEditEvent<TableRecord, String>>() {           
			@Override
			public void handle(CellEditEvent<TableRecord, String> t) {
				((TableRecord) t.getTableView().getItems().get(t.getTablePosition().getRow())).setMemo(t.getNewValue());
			}

		});
		tableView.setEditable(true);
		tableView.setItems(tableRecord);
	}

	/**
	 * tableViewの1行をあらわすクラス
	 * @author kazyury
	 */
	public static class TableRecord {
		private StringProperty id;
		private StringProperty image;
		private StringProperty memo;
		private TaggedMaterial taggedMaterial;

		private TableRecord(TaggedMaterial tm) throws MWException {
			this.taggedMaterial = tm;
			this.id = new SimpleStringProperty(tm.getId().getMaterialId());
			this.image = new SimpleStringProperty(PathUtil.getProductionThumbnailPath(tm.getMaterial()).toString());
			this.memo = new SimpleStringProperty(tm.getMemo());
		}

		public void setMemo(String newValue) {
			this.memo.set(newValue);
			taggedMaterial.setMemo(newValue);
			logger.info("memo was set to "+newValue);
		}

		public StringProperty idProperty(){return id;}
		public StringProperty imageProperty(){return image;}
		public StringProperty memoProperty(){return memo;}

		public TaggedMaterial getTaggedMaterial(){
			return taggedMaterial;
		}
	}

	/**
	 * ImageCol用のCell
	 * @author kazyury
	 */
	public static class ImageColCell extends TableCell<TableRecord, String> {
		private final ImageView imageView;

		public ImageColCell(){
			imageView = new ImageView();
			setText(null);
			setGraphic(imageView);
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty){
				FileInputStream is = null;
				try {
					is = new FileInputStream(item);
					imageView.setImage(new Image(is));
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				setText(null);
				setGraphic(imageView);
			}
		}
	}
}
