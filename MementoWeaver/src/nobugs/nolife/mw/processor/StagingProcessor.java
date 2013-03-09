package nobugs.nolife.mw.processor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PersistenceUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class StagingProcessor extends AnchorPane implements MWProcessor {
	private AppMain appl;
	private List<Material> installedMaterials;

	@FXML private TableView<TableRecord> tableView;
	@FXML private TableColumn<TableRecord,String> idCol;
	@FXML private TableColumn<TableRecord,String> typeCol;
	@FXML private TableColumn<TableRecord,String> tagCol;
	private ObservableList<TableRecord> tableRecord = FXCollections.observableArrayList();

	// テーブルビューのクリック
	@FXML protected void clicked(MouseEvent e) {
		// 選択行のTableRecordを取得し、次の画面に渡す。
		TableRecord record = tableView.getSelectionModel().getSelectedItem();
		System.out.println(record.materialIdProperty().toString());
		appl.fwdMaterialEditor(record.getMaterial(), record.getTaggedMaterialList());
		
	}
	@FXML protected void generate(ActionEvent e) {} // TODO not implemented yet
	@FXML protected void exit(ActionEvent e) {Platform.exit();}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		EntityManager em = PersistenceUtil.getMWEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		// Material表を検索し、installedされている素材の一覧を取得する。
		// TODO outer joinするべき
		CriteriaQuery<Material> materialQuery= cb.createQuery(Material.class);
		Root<Material> material = materialQuery.from(Material.class);
		materialQuery.select(material).where(cb.equal(material.get("materialState"), Constants.MATERIAL_STATE_INSTALLED));

		installedMaterials = em.createQuery(materialQuery).getResultList();
		for (Material m:installedMaterials) {

			// TaggedMaterial表を検索し、設定されているタグの一覧を取得する。
			CriteriaQuery<TaggedMaterial> taggedMaterialQuery = cb.createQuery(TaggedMaterial.class);
			Root<TaggedMaterial> taggedMaterialRoot = taggedMaterialQuery.from(TaggedMaterial.class);
			taggedMaterialQuery.select(taggedMaterialRoot).where(cb.equal(taggedMaterialRoot.get("id").get("materialId"), m.getMaterialId()));

			List<TaggedMaterial> taggedMaterialList = em.createQuery(taggedMaterialQuery).getResultList();
			StringBuffer tags = new StringBuffer();
			for(TaggedMaterial tm:taggedMaterialList) {
				tags.append("["+tm.getId().getTag()+"]");
			}

			// observableArrayListにTableRecordを登録
			tableRecord.add(new TableRecord(m.getMaterialId(), m.getMaterialType(), tags.toString(),m,taggedMaterialList));
		}

		// tableViewの設定
		// PropertyValueFactoryで"materialID"を指定すると、TableRecord#materialIDProperty()が呼ばれる。
		idCol.setCellValueFactory(new PropertyValueFactory<TableRecord,String>("materialId"));
		typeCol.setCellValueFactory(new PropertyValueFactory<TableRecord,String>("type"));
		tagCol.setCellValueFactory(new PropertyValueFactory<TableRecord,String>("concatenatedTag"));
		tableView.setItems(tableRecord);
	}

	@Override
	public void setApplication(AppMain appMain) {
		this.appl = appMain;
	}


	/**
	 * tableViewの1行をあらわすクラス
	 * @author kazyury
	 */
	public static class TableRecord {
		private StringProperty materialId;
		private StringProperty type;
		private StringProperty concatenatedTag;
		private Material material;
		private List<TaggedMaterial> taggedMaterialList;

		private TableRecord(String id, String type, String tags, Material material, List<TaggedMaterial> taggedMaterialList) {
			this.materialId = new SimpleStringProperty(id);
			this.type = new SimpleStringProperty(type);
			this.concatenatedTag = new SimpleStringProperty(tags);
			this.material = material;
			this.taggedMaterialList = taggedMaterialList;
		}

		public StringProperty materialIdProperty(){return materialId;}
		public StringProperty typeProperty(){return type;}
		public StringProperty concatenatedTagProperty(){return concatenatedTag;}
		
		public Material getMaterial() { return this.material; }
		public List<TaggedMaterial> getTaggedMaterialList() { return this.taggedMaterialList; }

	}

}
