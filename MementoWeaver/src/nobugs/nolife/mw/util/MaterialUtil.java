package nobugs.nolife.mw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nobugs.nolife.mw.image.ImageManipulator;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.persistence.TaggedMaterialPK;

/**
 * Material���[�e�B���e�B
 * @author kazyury
 *
 */
public class MaterialUtil {

	/** 
	 * �摜����]���ĕۊǂ���B�Î~��f�ޖ{�̂ƃT���l�C�����ΏۂƂȂ�B
	 * @param m
	 * @param degree:��]�p
	 */
	public static void rotatePhoto(Material m,int degree){
		if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			System.out.println("����f�ނ͉�]�ɑΉ����Ă��܂���"); // TODO ��O���o
			return;
		}

		try {
			// �f�ޖ{�̂���]����B
			ImageManipulator.rotate(PathUtil.getInstalledPhotoPath(m), degree);
			// �T���l�C������]����B
			ImageManipulator.rotate(PathUtil.getInstalledThumbnailPath(m), degree);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * yyyymmdd�`���őf�ނ̎B�e����ԋp����
	 * @param m
	 * @return
	 */
	public static String getMaterialDate(Material m){
		return m.getMaterialId().substring(0, 8);
	}

	/**
	 * �^�O�t�f�ނ̏����X�V����B
	 * �ǉ��^�O�̃����ɂ��ẮAchronicle�����ȊO�̐擪1����p����B
	 * @param m
	 * @param tags
	 */
	public static void updateTagInfo(Material m, String[] tags){
		// chronicle�ł͂Ȃ��擪1���̃������擾
		String defaultMemo="default memo";
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			if(!tm.getMemo().isEmpty() && !tag.equals("kazunori") && !tag.equals("hiroko") && !tag.equals("taito") ){
				defaultMemo=tm.getMemo();
				break;
			}
		}
		final String memo=defaultMemo;

		// closure�I�Ȏg�������Ă���ł����Ă�񂾂낤��...�B
		MaterialUtil.updateTagInfo(new UpdateMemoHandler() {
			@Override
			public void updateAllMemo(TaggedMaterial existingTm) {/* do nothing */}
			
			@Override
			public void createNewMemo(TaggedMaterial newTm) {
				MaterialUtil.updateMemo(newTm, memo);
			}
		}, m, tags);
	}

	/**
	 * �^�O�t�f�ނ̏����X�V����B
	 * �ǉ��^�O�̃����ɂ��ẮA�n���ꂽmemo��p����B
	 * @param m
	 * @param tags
	 * @param memo
	 */
	public static void updateTagInfo(Material m, String[] tags, final String memo){
		MaterialUtil.updateTagInfo(new UpdateMemoHandler() {
			
			@Override
			public void updateAllMemo(TaggedMaterial existingTm) {
				MaterialUtil.updateMemo(existingTm, memo);
			}
			
			@Override
			public void createNewMemo(TaggedMaterial newTm) {
				MaterialUtil.updateMemo(newTm, memo);
			}
		}, m, tags);
	}

	//----------------------------------------------------------------------------

	/**
	 * �����X�V�̃N���[�W���C���^�[�t�F�[�X
	 * @author kazyury
	 *
	 */
	interface UpdateMemoHandler {
		void updateAllMemo(TaggedMaterial existingTm);
		void createNewMemo(TaggedMaterial newTm);
	}
	private static void updateTagInfo(UpdateMemoHandler handler, Material m, String[] tags){
		// String�z���List�ɋl�ߑւ�
		List<String> tagList = new ArrayList<String>();
		for(String tag:tags){
			tagList.add(tag);
		}
		System.out.println("tagList is "+tagList.toString());
		/*
		 * taggedMaterial |___|___|___|___|___|
		 * tagList            |___|___|___|___|___|
		 *                  v   v   v   v   v   v
		 *                 upd upd upd upd upd add
		 */
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			/* tagList�Ɋ܂܂��ꍇ�́Aupdate/�܂܂�Ȃ��ꍇ�ɂ�NOT_IN_USE�ɏ�Ԃ�ύX
			 * ��ԑJ�ڃ}�g���b�N�X�́�
			 *            |        TagList
			 * -----------+-------------------------
			 * TAG_STATE  | �܂܂�Ă���	�܂܂�ċ��Ȃ�
			 * -----------+-------------------------
			 * Staged     | (NoChange)	NotInUse
			 * Published  | (NoChange)	NotInUse
			 * NotInUse   | Staged		(NoChange)
			 * -----------+-------------------------
			 */
			if(tagList.contains(tag)){
				handler.updateAllMemo(tm); // �u���b�N�Ăяo��1
				tagList.remove(tag);
				if(tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					tm.setTagState(Constants.TAG_STATE_STAGED);
				}
			} else {
				tm.setTagState(Constants.TAG_STATE_NOT_IN_USE);
			}
		}

		// tagList�ɂ̂ݑ��݂���tag��ǉ�
		for(String tag:tagList){
			TaggedMaterialPK pk = new TaggedMaterialPK();
			pk.setMaterialId(m.getMaterialId());
			pk.setTag(tag);

			TaggedMaterial tm = new TaggedMaterial();
			tm.setId(pk);
			tm.setMaterial(m);
			handler.createNewMemo(tm); // �u���b�N�Ăяo��2
			tm.setTagState(Constants.TAG_STATE_STAGED);

			m.getTaggedMaterials().add(tm);
		}
	}



	/**
	 * �����ݒ��
	 * @param tm
	 * @param memo
	 * @return
	 */
	private static TaggedMaterial updateMemo(TaggedMaterial tm, String memo) {
		PropertyUtil prop = new PropertyUtil();
		Material m = tm.getMaterial();
		String tag = tm.getId().getTag();

		if(tag.equals("kazunori")||tag.equals("hiroko")||tag.equals("taito")){
			System.out.println("chronicle rule applied");
			tm.setMemo(prop.calcAge(tag, MaterialUtil.getMaterialDate(m))+"�΂̏ё�");
		} else {
			tm.setMemo(memo);
		}
		return tm;
	}
}
