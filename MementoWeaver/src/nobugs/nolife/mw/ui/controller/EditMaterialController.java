package nobugs.nolife.mw.ui.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PropertyUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class EditMaterialController extends AnchorPane implements MWSceneController{
	private AppMain appl;
	private Material material;
	private PropertyUtil propertyUtil = new PropertyUtil();

	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField tagTextField;
	@FXML private TextArea memoTextArea;
	@FXML private ImageView imageView;
	@FXML private Button rotateLeft;
	@FXML private Button rotateRight;

	// イベントハンドラ
	@FXML	protected void rotateLeft(ActionEvent e) {
		// TODO not implemented.
	}
	@FXML	protected void rotateRight(ActionEvent e) {
		// TODO not implemented.
	}
	@FXML	protected void appendTag(ActionEvent e) {
		// 押されたボタンのIDをタグとして使用する
		// 押されたボタンのIDを取得
		Button btn = (Button) e.getSource();
		String tagname = btn.getId();

		// tagTextFieldに既に同じタグがあったら追記しない。
		if(!tagTextField.getText().matches(".*"+tagname+".*")){
			tagTextField.setText(tagTextField.getText()+"["+tagname+"]");
		}
	}
	@FXML	protected void apply(ActionEvent e) {
		// TODO not implemented.	
	}
	@FXML	protected void cancel(ActionEvent e) {
		appl.fwdStagingMaterial();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// nothing to do.
	}

	@Override
	public void setApplication(AppMain appMain, Object o) {
		appl = appMain;
		material = (Material)o;
		// imageViewへの表示
		String stagingArea = propertyUtil.getStagingAreaName();
		String imageFileName = MaterialUtil.getPhotoFileName(material);

		FileSystem fs = FileSystems.getDefault();
		String fullpath = fs.getPath(stagingArea, imageFileName).toString();
		try {
			imageView.setImage(new Image(new FileInputStream(fullpath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// もし、動画素材ならば回転操作はDisable
		if (material.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			rotateLeft.setDisable(true);
			rotateRight.setDisable(true);
		}

		
		List<TaggedMaterial> taggedMaterialList = material.getTaggedMaterials();
		boolean isSameMemoOnly = true; // 複数のメモが全て同一か否かのFL
		boolean isFirsttime = true;// ループ初回判定用
		String previousMemo = "";
		for (TaggedMaterial tm:taggedMaterialList) {
			// タグが存在するならばtagTextFieldに設定
			tagTextField.appendText("["+tm.getId().getTag()+"]");
			
			if (isFirsttime == true) {
				previousMemo = tm.getMemo();
				isFirsttime = false;
			} else {
				if(!previousMemo.equals(tm.getMemo())){
					isSameMemoOnly=false;
				}
			}
		}
		// メモが存在するならばmemoTextAreaに設定
		// 異なるメモが格納されている場合には、[複数の異なるメモがあります。メメントの修正でコメントを修正してください。]ってことにする。
		if(isSameMemoOnly==true) {
			memoTextArea.setText(previousMemo);
		} else {
			memoTextArea.setText("タグ毎に異なるメモが登録されています。メメントの修正で個別のメモを修正してください。");
			memoTextArea.setDisable(true);
		}
	}
}
