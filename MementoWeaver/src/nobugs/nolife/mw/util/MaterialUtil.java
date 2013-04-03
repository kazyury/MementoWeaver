package nobugs.nolife.mw.util;

import java.util.logging.Logger;

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
		logger.info("Material��MATERIAL_STATE_INSTALLED�ɐݒ肳��܂���(TAG_STATE_NOT_IN_USE:�݂�)");
		return;
	}

}
