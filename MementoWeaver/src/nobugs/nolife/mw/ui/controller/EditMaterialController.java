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
	/** �`�悷��f�� */
	private Material material;
	/** �f�ޓ��̃������S�ē��ꂩ? */
	private boolean isSameMemoOnly = true;

	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField tagTextField;
	@FXML private TextArea memoTextArea;
	@FXML private ImageView imageView;
	@FXML private Button rotateLeft;
	@FXML private Button rotateRight;

	// �C�x���g�n���h��
	@FXML	protected void rotateLeft(ActionEvent e) throws MWException {
		MaterialUtil.rotatePhoto(material,270);
		setImageView();
	}
	@FXML	protected void rotateRight(ActionEvent e) throws MWException {
		MaterialUtil.rotatePhoto(material,90);
		setImageView();
	}
	@FXML	protected void appendTag(ActionEvent e) {
		// �����ꂽ�{�^����ID���^�O�Ƃ��Ďg�p����
		// �����ꂽ�{�^����ID���擾
		Button btn = (Button) e.getSource();
		String tagname = btn.getId();

		// tagTextField�Ɋ��ɓ����^�O����������ǋL���Ȃ��B
		if(!tagTextField.getText().matches(".*"+tagname+".*")){
			tagTextField.setText(tagTextField.getText()+"["+tagname+"]");
		}
	}
	@FXML	protected void apply(ActionEvent e) throws MWException {
		// �^�O������̕���
		String[] tagnames = StringUtil.splitTagString(tagTextField.getText());
		
		// TaggedMaterial�̍X�V(�����X�V�𐧌����Ă���ꍇ�ɂ́A�f�t�H���g�������g�p����)
		if (isSameMemoOnly) {
			String memo = memoTextArea.getText();
			MaterialUtil.updateTagInfo(material, tagnames, memo);
		} else {
			MaterialUtil.updateTagInfo(material, tagnames);
		}
		
		// �^�O�����X�V���ĉ�ʂ����
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
		// imageView�ւ̕\��
		setImageView();

		// �����A����f�ނȂ�Ή�]�����Disable
		if (material.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			rotateLeft.setDisable(true);
			rotateRight.setDisable(true);
		}

		List<TaggedMaterial> taggedMaterialList = material.getTaggedMaterials();
		// �^�O�����݂���Ȃ��tagTextField�ɐݒ�
		// TODO tagTextField�Ƃ̓ˍ����ł͂Ȃ��A���ۂ�DB�ɂ��邩�ۂ��Ń`�F�b�N���Ȃ���΂Ȃ�Ȃ����A��Ō��؁iPublished�ȃ^�O��������x�ǉ����ꂽ��)
		String tags = StringUtil.joinTagString(taggedMaterialList);
		tagTextField.appendText(tags);

		// �����̏�Ԃɉ����Ċ���/�񊈐��𐧌�
		boolean isFirsttime = true;// ���[�v���񔻒�p
		String previousMemo = "";
		for (TaggedMaterial tm:taggedMaterialList) {
			// Staged������Ώۂ�
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
}
