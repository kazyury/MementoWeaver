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
	/** �`�悷��f�� */
	private Material material;
	/** �f�ޓ��̃������S�ē��ꂩ? */
	private boolean isSameMemoOnly = true;
	
	/** tagname �� ToggleButton��map */
	private Map<String, ToggleButton> tagMap = new HashMap<String, ToggleButton>();

	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField tagTextField;
	@FXML private TextArea memoTextArea;
	@FXML private ImageView imageView;
	@FXML private Button rotateLeft;	// Button��Enable/Disable������s�����߂ɕK�v
	@FXML private Button rotateRight;	// Button��Enable/Disable������s�����߂ɕK�v
	// PredefinedTag�p��ToggleButton
	@FXML private ToggleButton albumTag;
	@FXML private ToggleButton winnerTag;
	@FXML private ToggleButton treasureTag;
	@FXML private ToggleButton partyTag;
	@FXML private ToggleButton kazunoriTag;
	@FXML private ToggleButton hirokoTag;
	@FXML private ToggleButton taitoTag;

	// �C�x���g�n���h��
	@FXML	protected void rotateLeft(ActionEvent e) throws MWException {
		rotate(270);
		setImageView();
	}
	@FXML	protected void rotateRight(ActionEvent e) throws MWException {
		rotate(90);
		setImageView();
	}
	
	@FXML	protected void apply(ActionEvent e) throws MWException {
		// ��ʂőI���E���͂��ꂽ�^�O���
		Map<String, Boolean> toggleButtonState = gatherInputedTag();

		// �^�O�����X�V���ĉ�ʂ����
		UpdateTagProcessor processor = new UpdateTagProcessor();
		// TaggedMaterial�̍X�V(�����X�V�𐧌����Ă���ꍇ�ɂ́A�f�t�H���g�������g�p����)
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
		// ���O��`�^�O����ToggleButton�̑Ή��t��
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
		// imageView�ւ̕\��
		setImageView();

		// �����A����f�ނȂ�Ή�]�����Disable
		if (material.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			rotateLeft.setDisable(true);
			rotateRight.setDisable(true);
		}

		// Toggle�{�^���̐���
		List<TaggedMaterial> taggedMaterialList = material.getTaggedMaterials();
		for(TaggedMaterial tm:taggedMaterialList){
			String tag = tm.getId().getTag();
			if(tagMap.containsKey(tag)){
				ToggleButton btn = tagMap.get(tag);
				// �^�O��Staged����Published�Ƃ��ēo�^����Ă���Ȃ�ToggleButton��ON(PredefinedTag�̏ꍇ)
				if(!tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					btn.setSelected(true);
				}
				// �������APublished��ԂȂ��Disable�ɂ���Toggle��ύX�s�ɁB
				if(tm.getTagState().equals(Constants.TAG_STATE_PUBLISHED)){
					btn.setDisable(true);
				}
			}else{
				// �^�O��Staged����Published�Ƃ��ēo�^����Ă���Ȃ�tagTextField�ɒǋL(PredefinedTag�ȊO�̏ꍇ)
				if(!tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					tagTextField.appendText("["+tag+"]");
				}
				// ���O��`�^�O�ȊO�̏ꍇ�ɂ́APublish��Ԃ𐧌�ł��Ȃ����APublished�ɂȂ邱�Ƃ������̂œ��ʂ͕��u
			}
		}

		// �����̏�Ԃɉ����Ċ���/�񊈐��𐧌�
		boolean isFirsttime = true;// ���[�v���񔻒�p
		String previousMemo = "";
		for (TaggedMaterial tm:taggedMaterialList) {
			// Staged������Ώۂɂ���Bapply����Published�̃�����ׂ��Ȃ��悤�ɂ���K�v������B
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
		
		// ���������݂���Ȃ��memoTextArea�ɐݒ�
		// �قȂ郁�����i�[����Ă���ꍇ�ɂ́A[�����̈قȂ郁��������܂��B�������g�̏C���ŃR�����g���C�����Ă��������B]���Ă��Ƃɂ���B
		if(isSameMemoOnly) {
			memoTextArea.setText(previousMemo);
		} else {
			memoTextArea.setText("�^�O���ɈقȂ郁�����o�^����Ă��܂��B�������g�̏C���Ōʂ̃������C�����Ă��������B");
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
			throw new MWException("ImageView�ւ̃C���[�W�`��ŗ�O���������܂���", e.getCause());
		}
	}
	
	private void rotate(int degree) throws MWException {
		try {
			// �f�ޖ{�̂���]����B
			ImageManipulator.rotate(PathUtil.getInstalledPhotoPath(material), degree);
			// �T���l�C������]����B
			ImageManipulator.rotate(PathUtil.getInstalledThumbnailPath(material), degree);
		} catch (IOException ioe) {
			throw new MWException("��O���������܂���",ioe.getCause());
		}
	}
	
	/**
	 * ��ʂ̃g�O���{�^���EtagTextField�œ��͂��ꂽ�^�O�̏�Ԃ�Map<�^�O��,Boolean>�ŕԋp����B
	 * �^�O���I�𖔂͓��͂���Ă���Ƃ��ɂ�true�ƂȂ�BPredefinedTag�����I���̏ꍇ�ɂ�false.
	 * @return
	 * @throws MWException
	 */
	private Map<String, Boolean> gatherInputedTag() throws MWException {
		// ��ʏ�̃g�O���{�^����Ԃ̎擾
		Map<String, Boolean> toggleButtonState = new HashMap<>();
		for(Map.Entry<String,ToggleButton> entry:tagMap.entrySet()){
			if(entry.getValue().isSelected()){
				toggleButtonState.put(entry.getKey(), new Boolean(true));
			} else {
				toggleButtonState.put(entry.getKey(), new Boolean(false));
			}
		}
		// tagTextField�ɓ��͂��ꂽ�^�O
		for(String tagname:StringUtil.splitTagString(tagTextField.getText())){
			toggleButtonState.put(tagname, new Boolean(true));
		}
		return toggleButtonState;
	}
}
