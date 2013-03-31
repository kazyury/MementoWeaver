package nobugs.nolife.mw.ui.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.processing.UpdateTagProcessor;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.StringUtil;

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
	/** 描画する素材 */
	private Material material;
	/** 素材内のメモが全て同一か? */
	private boolean isSameMemoOnly = true;

	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField tagTextField;
	@FXML private TextArea memoTextArea;
	@FXML private ImageView imageView;
	@FXML private Button rotateLeft;
	@FXML private Button rotateRight;

	// イベントハンドラ
	@FXML	protected void rotateLeft(ActionEvent e) throws MWException {
		MaterialUtil.rotatePhoto(material,270);
		setImageView();
	}
	@FXML	protected void rotateRight(ActionEvent e) throws MWException {
		MaterialUtil.rotatePhoto(material,90);
		setImageView();
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
	@FXML	protected void apply(ActionEvent e) throws MWException {
		// タグ文字列の分離
		String[] tagnames = StringUtil.splitTagString(tagTextField.getText());
		
		// TaggedMaterialの更新(メモ更新を制限している場合には、デフォルトメモを使用する)
		if (isSameMemoOnly) {
			String memo = memoTextArea.getText();
			MaterialUtil.updateTagInfo(material, tagnames, memo);
		} else {
			MaterialUtil.updateTagInfo(material, tagnames);
		}
		
		// タグ情報を更新して画面を閉じる
		UpdateTagProcessor processor = new UpdateTagProcessor();
		processor.updateTagProcess(material);
		appl.fwdInstalledMaterialList();
		
	}
	@FXML	protected void cancel(ActionEvent e) throws MWException {
		appl.fwdInstalledMaterialList();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// nothing to do.
	}

	@Override
	public void setApplication(AppMain appMain, Object o) throws MWException {
		appl = appMain;
		material = (Material)o;
		// imageViewへの表示
		setImageView();

		// もし、動画素材ならば回転操作はDisable
		if (material.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			rotateLeft.setDisable(true);
			rotateRight.setDisable(true);
		}

		List<TaggedMaterial> taggedMaterialList = material.getTaggedMaterials();
		// タグが存在するならばtagTextFieldに設定
		// TODO tagTextFieldとの突合せではなく、実際のDBにあるか否かでチェックしなければならないか、後で検証（Publishedなタグをもう一度追加されたり)
		String tags = StringUtil.joinTagString(taggedMaterialList);
		tagTextField.appendText(tags);

		// メモの状態に応じて活性/非活性を制御
		boolean isFirsttime = true;// ループ初回判定用
		String previousMemo = "";
		for (TaggedMaterial tm:taggedMaterialList) {
			// Stagedだけを対象に
			if(tm.getTagState().equals(Constants.TAG_STATE_STAGED)){
				if (isFirsttime == true) {
					previousMemo = tm.getMemo();
					isFirsttime = false;
				} else {
					if(!previousMemo.equals(tm.getMemo())){
						isSameMemoOnly=false;
					}
				}
			}
		}
		// メモが存在するならばmemoTextAreaに設定
		// 異なるメモが格納されている場合には、[複数の異なるメモがあります。メメントの修正でコメントを修正してください。]ってことにする。
		if(isSameMemoOnly) {
			memoTextArea.setText(previousMemo);
		} else {
			memoTextArea.setText("タグ毎に異なるメモが登録されています。メメントの修正で個別のメモを修正してください。");
			memoTextArea.setDisable(true);
		}
	}
	
	private void setImageView() throws MWException {
		String fullpath = PathUtil.getInstalledPhotoPath(material).toString();
		FileInputStream is = null;
		try {
			is = new FileInputStream(fullpath);
			imageView.setImage(new Image(is));
			is.close();
		} catch (IOException e) {
			throw new MWException("ImageViewへのイメージ描画で例外が発生しました", e.getCause());
		}
	}
}
