package nobugs.nolife.mw.ui.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.image.ImageManipulator;
import nobugs.nolife.mw.processing.UpdateTagProcessor;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.StringUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class EditMaterialController extends AnchorPane implements MWSceneController{
	private AppMain appl;
	/** 描画する素材 */
	private Material material;
	/** 素材内のメモが全て同一か? */
	private boolean isSameMemoOnly = true;
	
	/** tagname と ToggleButtonのmap */
	private Map<String, ToggleButton> tagMap = new HashMap<String, ToggleButton>();

	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField tagTextField;
	@FXML private TextArea memoTextArea;
	@FXML private ImageView imageView;
	@FXML private Button rotateLeft;	// ButtonのEnable/Disable制御を行うために必要
	@FXML private Button rotateRight;	// ButtonのEnable/Disable制御を行うために必要
	// PredefinedTag用のToggleButton
	@FXML private ToggleButton albumTag;
	@FXML private ToggleButton winnerTag;
	@FXML private ToggleButton treasureTag;
	@FXML private ToggleButton partyTag;
	@FXML private ToggleButton kazunoriTag;
	@FXML private ToggleButton hirokoTag;
	@FXML private ToggleButton taitoTag;

	// イベントハンドラ
	@FXML	protected void rotateLeft(ActionEvent e) throws MWException {
		rotate(270);
		setImageView();
	}
	@FXML	protected void rotateRight(ActionEvent e) throws MWException {
		rotate(90);
		setImageView();
	}
	
	@FXML	protected void apply(ActionEvent e) throws MWException {
		// 画面で選択・入力されたタグ情報
		Map<String, Boolean> toggleButtonState = gatherInputedTag();

		// タグ情報を更新して画面を閉じる
		UpdateTagProcessor processor = new UpdateTagProcessor();
		// TaggedMaterialの更新(メモ更新を制限している場合には、デフォルトメモを使用する)
		if (isSameMemoOnly) {
			String memo = memoTextArea.getText();
			processor.updateTagProess(material, toggleButtonState, memo);
		} else {
			processor.updateTagProess(material, toggleButtonState);
		}
		appl.fwdInstalledMaterialList();

	}

	@FXML	protected void cancel(ActionEvent e) throws MWException {
		appl.fwdInstalledMaterialList();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 事前定義タグ名とToggleButtonの対応付け
		tagMap.put("album", albumTag);
		tagMap.put("winner", winnerTag);
		tagMap.put("treasure", treasureTag);
		tagMap.put("party", partyTag);
		tagMap.put("kazunori", kazunoriTag);
		tagMap.put("hiroko", hirokoTag);
		tagMap.put("taito", taitoTag);
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

		// Toggleボタンの制御
		List<TaggedMaterial> taggedMaterialList = material.getTaggedMaterials();
		for(TaggedMaterial tm:taggedMaterialList){
			String tag = tm.getId().getTag();
			if(tagMap.containsKey(tag)){
				ToggleButton btn = tagMap.get(tag);
				// タグがStaged又はPublishedとして登録されているならToggleButtonをON(PredefinedTagの場合)
				if(!tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					btn.setSelected(true);
				}
				// ただし、Published状態ならばDisableにしてToggleを変更不可に。
				if(tm.getTagState().equals(Constants.TAG_STATE_PUBLISHED)){
					btn.setDisable(true);
				}
			}else{
				// タグがStaged又はPublishedとして登録されているならtagTextFieldに追記(PredefinedTag以外の場合)
				if(!tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					tagTextField.appendText("["+tag+"]");
				}
				// 事前定義タグ以外の場合には、Publish状態を制御できないが、Publishedになることが無いので当面は放置
			}
		}

		// メモの状態に応じて活性/非活性を制御
		boolean isFirsttime = true;// ループ初回判定用
		String previousMemo = "";
		for (TaggedMaterial tm:taggedMaterialList) {
			// Stagedだけを対象にする。apply時にPublishedのメモを潰さないようにする必要がある。
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
	
	private void rotate(int degree) throws MWException {
		try {
			// 素材本体を回転する。
			ImageManipulator.rotate(PathUtil.getInstalledPhotoPath(material), degree);
			// サムネイルを回転する。
			ImageManipulator.rotate(PathUtil.getInstalledThumbnailPath(material), degree);
		} catch (IOException ioe) {
			throw new MWException("例外が発生しました",ioe.getCause());
		}
	}
	
	/**
	 * 画面のトグルボタン・tagTextFieldで入力されたタグの状態をMap<タグ名,Boolean>で返却する。
	 * タグが選択又は入力されているときにはtrueとなる。PredefinedTagが未選択の場合にはfalse.
	 * @return
	 * @throws MWException
	 */
	private Map<String, Boolean> gatherInputedTag() throws MWException {
		// 画面上のトグルボタン状態の取得
		Map<String, Boolean> toggleButtonState = new HashMap<>();
		for(Map.Entry<String,ToggleButton> entry:tagMap.entrySet()){
			if(entry.getValue().isSelected()){
				toggleButtonState.put(entry.getKey(), new Boolean(true));
			} else {
				toggleButtonState.put(entry.getKey(), new Boolean(false));
			}
		}
		// tagTextFieldに入力されたタグ
		for(String tagname:StringUtil.splitTagString(tagTextField.getText())){
			toggleButtonState.put(tagname, new Boolean(true));
		}
		return toggleButtonState;
	}
}
