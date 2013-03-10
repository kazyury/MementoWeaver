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

	// ���̓t�B�[���h�ɑΉ�����C���X�^���X��ێ�����ϐ�
	// �Ή��t����FXML�t�@�C���Œ�`����
	// public�t�B�[���h�̏ꍇ��@FXML�̋L�q�͕s�v
	@FXML private TextField tagTextField;
	@FXML private TextArea memoTextArea;
	@FXML private ImageView imageView;
	@FXML private Button rotateLeft;
	@FXML private Button rotateRight;

	// �C�x���g�n���h��
	@FXML	protected void rotateLeft(ActionEvent e) {
		// TODO not implemented.
	}
	@FXML	protected void rotateRight(ActionEvent e) {
		// TODO not implemented.
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
		// imageView�ւ̕\��
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
		// �����A����f�ނȂ�Ή�]�����Disable
		if (material.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			rotateLeft.setDisable(true);
			rotateRight.setDisable(true);
		}

		
		List<TaggedMaterial> taggedMaterialList = material.getTaggedMaterials();
		boolean isSameMemoOnly = true; // �����̃������S�ē��ꂩ�ۂ���FL
		boolean isFirsttime = true;// ���[�v���񔻒�p
		String previousMemo = "";
		for (TaggedMaterial tm:taggedMaterialList) {
			// �^�O�����݂���Ȃ��tagTextField�ɐݒ�
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
		// ���������݂���Ȃ��memoTextArea�ɐݒ�
		// �قȂ郁�����i�[����Ă���ꍇ�ɂ́A[�����̈قȂ郁��������܂��B�������g�̏C���ŃR�����g���C�����Ă��������B]���Ă��Ƃɂ���B
		if(isSameMemoOnly==true) {
			memoTextArea.setText(previousMemo);
		} else {
			memoTextArea.setText("�^�O���ɈقȂ郁�����o�^����Ă��܂��B�������g�̏C���Ōʂ̃������C�����Ă��������B");
			memoTextArea.setDisable(true);
		}
	}
}
