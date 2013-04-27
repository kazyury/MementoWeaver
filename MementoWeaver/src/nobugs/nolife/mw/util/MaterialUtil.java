package nobugs.nolife.mw.util;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;

/**
 * Material���[�e�B���e�B
 * @author kazyury
 *
 */
public class MaterialUtil {
	private static Logger logger = Logger.getGlobal();

	/**
	 * �f�ނ̎B�e�N������ԋp����
	 * @param m
	 * @return
	 */
	public static String getMaterialYearMonthDate(Material m){ return m.getMaterialId().substring(0, 8); }
	public static String getMaterialYearMonth(Material m){ return m.getMaterialId().substring(0, 6); }
	public static String getMaterialYear(Material m) { return m.getMaterialId().substring(0, 4); }
	public static String getMaterialMonth(Material m){ return m.getMaterialId().substring(4, 6); }
	public static String getMaterialDate(Material m){ return m.getMaterialId().substring(6, 8); }
	public static String getMaterialHour(Material m){ return m.getMaterialId().substring(8, 10); }
	public static String getMaterialMinute(Material m){ return m.getMaterialId().substring(10, 12); }
	public static String getMaterialSecond(Material m){ return m.getMaterialId().substring(12, 14); }

	/**
	 * materialPath�Ŏw�肳�ꂽ�f�ނ̃^�C�v��ԋp����
	 * @param materialPath
	 * @return {@value Constants#MATERIAL_TYPE_JPG} or {@value Constants#MATERIAL_TYPE_MOV}
	 * @throws MWException 
	 * @see Constants#MATERIAL_TYPE_JPG
	 * @see Constants#MATERIAL_TYPE_MOV
	 */
	public static String getMaterialType(Path materialPath) throws MWException {
		String strpath = materialPath.toString();
		int idx = strpath.lastIndexOf(".");
		String suffix = strpath.substring(idx+1);
		if(suffix.equalsIgnoreCase("jpg")) {
			return Constants.MATERIAL_TYPE_JPG;
		} else if (suffix.equalsIgnoreCase("mov")) {
			return Constants.MATERIAL_TYPE_MOV;
		} else {
			logger.severe("path["+materialPath.toString()+"]���s���ł�.Suffix��["+suffix+"]�ł�.");
			throw new MWException("path["+materialPath.toString()+"]���s���ł�.");
		}
	}

	/**
	 * materialPath�Ŏw�肳�ꂽ�f�ނ̊g���q�ɉ����āA���K�����ꂽ�g���q�������ԋp����
	 * @param materialPath
	 * @return
	 * @throws MWException
	 */
	public static String getNormalizedSuffix(File materialPath) throws MWException {
		int pos = materialPath.getPath().lastIndexOf(".");
		String suffix = materialPath.getPath().toLowerCase().substring(pos+1);
		
		if (suffix.equals("jpg")||suffix.equals("jpeg")){
			suffix = "jpg";
		}
		if (!suffix.equals("mov") && !suffix.equals("jpg")){
			throw new MWException("Unsupported file type");
		}
		return suffix;
	}

	/**
	 * �֘A����TaggedMaterial�̏�ԂɊ�Â���Material�̏�Ԃ��X�V����B
	 * @param m
	 */
	public static void updateMaterialState(Material m) {
		// 1����TaggedMaterial�����݂��Ȃ����Installed�ɐݒ肵�ďI��
		if(m.getTaggedMaterials().isEmpty()) {
			m.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
			logger.info("Material��MATERIAL_STATE_INSTALLED�ɐݒ肳��܂���(TaggedMaterial:0��)");
			return;
		}

		// 1���ł�TaggedMaterial��Staged��������Staged�ɐݒ肵�ďI��
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			if(tm.getTagState().equals(Constants.TAG_STATE_STAGED)){
				m.setMaterialState(Constants.MATERIAL_STATE_STAGED);
				logger.info("Material��MATERIAL_STATE_STAGED�ɐݒ肳��܂���(TAG_STATE_STAGED:�݂�)");
				return;
			}
		}

		// 1���ł�TaggedMaterial��NotInUse��������Installed�ɐݒ肵�ďI��(Staged�^�O�͑��݂��Ȃ��̂�)
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			if(tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
				m.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
				logger.info("Material��MATERIAL_STATE_INSTALLED�ɐݒ肳��܂���(TAG_STATE_NOT_IN_USE:�݂�)");
				return;
			}
		}
		// ��L�̉���ɂ�������Ȃ��ꍇ�͑S��Published�Ȃ̂�IN-USE�ɐݒ�
		m.setMaterialState(Constants.MATERIAL_STATE_IN_USE);
		logger.info("Material��MATERIAL_STATE_IN_USE�ɐݒ肳��܂���");
		return;
	}
}
