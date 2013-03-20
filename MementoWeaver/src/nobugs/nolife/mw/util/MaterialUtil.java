package nobugs.nolife.mw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;
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
	private static Logger logger = Logger.getGlobal();

	/** 
	 * �摜����]���ĕۊǂ���B�Î~��f�ޖ{�̂ƃT���l�C�����ΏۂƂȂ�B
	 * @param m
	 * @param degree:��]�p
	 * @throws MWException 
	 */
	public static void rotatePhoto(Material m,int degree) throws MWException{
		if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			throw new MWException("����f�ނ͉�]�ɑΉ����Ă��܂���");
		}

		try {
			// �f�ޖ{�̂���]����B
			ImageManipulator.rotate(PathUtil.getInstalledPhotoPath(m), degree);
			// �T���l�C������]����B
			ImageManipulator.rotate(PathUtil.getInstalledThumbnailPath(m), degree);
		} catch (IOException e) {
			throw new MWException("��O���������܂���",e.getCause());
		}
	}

	/**
	 * yyyymmdd�`���őf�ނ̎B�e�N������ԋp����
	 * @param m
	 * @return
	 */
	public static String getMaterialDate(Material m){
		return m.getMaterialId().substring(0, 8);
	}

	/**
	 * yyyymm�`���őf�ނ̎B�e�N����ԋp����
	 * @param m
	 * @return
	 */
	public static String getMaterialMonth(Material m){
		return m.getMaterialId().substring(0, 6);
	}

	/**
	 * yyyy�`���őf�ނ̎B�e�N��ԋp����
	 * @param m
	 * @return
	 */
	public static String getMaterialYear(Material m) {
		return m.getMaterialId().substring(0, 4);
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
		logger.info("tagList is "+tagList.toString());
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
			 *    tm      |        TagList          |
			 * -----------+-------------------------+-----------------
			 * TAG_STATE  | �܂܂�Ă���	�܂܂�ċ��Ȃ�    |	memo
			 * -----------+-------------------------+-----------------
			 * Staged     | (NoChange)	NotInUse    | �X�V����
			 * Published  | (NoChange)	NotInUse    | �X�V���Ȃ�
			 * NotInUse   | Staged		(NoChange)  | �X�V���Ȃ�
			 * -----------+-------------------------+-----------------
			 */
			if(tagList.contains(tag)){
				logger.info("tagList["+tagList.toString()+"]��tag["+tag+"]���܂܂�Ă��܂��B");
				
				if(tm.getTagState().equals(Constants.TAG_STATE_STAGED)){
					logger.info("���̃^�O��Ԃ�STAGED�̂��߁A�f�ނ̏�Ԃ�STAGED�ɕύX���܂�");
					m.setMaterialState(Constants.MATERIAL_STATE_STAGED); // �f�ނ̏�Ԃ�STAGED�ɕύX
					handler.updateAllMemo(tm); // �u���b�N�Ăяo��1

				} else if(tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					logger.info("���̃^�O��Ԃ�NOT-IN-USE�̂��߁A�f�ނ̏�Ԃ�STAGED�ɕύX���܂�");
					m.setMaterialState(Constants.MATERIAL_STATE_STAGED); // �f�ނ̏�Ԃ�STAGED�ɕύX
					tm.setTagState(Constants.TAG_STATE_STAGED);
				}
				tagList.remove(tag);
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
			m.setMaterialState(Constants.MATERIAL_STATE_STAGED);
		}
	}



	/**
	 * �����ݒ��
	 * @param tm
	 * @param memo
	 * @return
	 */
	private static TaggedMaterial updateMemo(TaggedMaterial tm, String memo) {
		Material m = tm.getMaterial();
		String tag = tm.getId().getTag();

		if(tag.equals("kazunori")||tag.equals("hiroko")||tag.equals("taito")){
			logger.info("tag["+tag+"]�̂��߃N���j�N���E���[�����K�p����܂�");
			PropertyUtil prop = new PropertyUtil();
			tm.setMemo(prop.calcAge(tag, MaterialUtil.getMaterialDate(m))+"�΂̏ё�");
		} else {
			tm.setMemo(memo);
		}
		return tm;
	}
}
