package nobugs.nolife.mw.util;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWImplementationError;

/**
 * Materialユーティリティ
 * @author kazyury
 *
 */
public class MaterialUtil {
	private static Logger logger = Logger.getGlobal();

	/**
	 * 素材の撮影年月日を返却する
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
	 * materialPathで指定された素材のタイプを返却する
	 * @param materialPath
	 * @return {@value Constants#MATERIAL_TYPE_JPG} or {@value Constants#MATERIAL_TYPE_MOV}
	 * @throws MWException 
	 * @see Constants#MATERIAL_TYPE_JPG
	 * @see Constants#MATERIAL_TYPE_MOV
	 */
	public static String getMaterialType(Path materialPath) {
		String strpath = materialPath.toString();
		int idx = strpath.lastIndexOf(".");
		String suffix = strpath.substring(idx+1);
		if(suffix.equalsIgnoreCase("jpg")) {
			return Constants.MATERIAL_TYPE_JPG;
		} else if (suffix.equalsIgnoreCase("mov")) {
			return Constants.MATERIAL_TYPE_MOV;
		} else {
			throw new MWImplementationError("path["+materialPath.toString()+"]が不正です.Suffixは["+suffix+"]です.");
		}
	}

	/**
	 * materialPathで指定された素材の拡張子に応じて、正規化された拡張子文字列を返却する
	 * @param materialPath
	 * @return
	 * @throws MWException
	 */
	public static String getNormalizedSuffix(File materialPath) {
		int pos = materialPath.getPath().lastIndexOf(".");
		String suffix = materialPath.getPath().toLowerCase().substring(pos+1);
		
		if (suffix.equals("jpg")||suffix.equals("jpeg")){
			suffix = "jpg";
		}
		if (!suffix.equals("mov") && !suffix.equals("jpg")){
			throw new MWImplementationError("Unsupported file type");
		}
		return suffix;
	}

	/**
	 * 関連するTaggedMaterialの状態に基づいてMaterialの状態を更新する。
	 * @param m
	 */
	public static void updateMaterialState(Material m) {
		// 1件もTaggedMaterialが存在しなければInstalledに設定して終了
		if(m.getTaggedMaterials().isEmpty()) {
			m.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
			logger.info("MaterialはMATERIAL_STATE_INSTALLEDに設定されました(TaggedMaterial:0件)");
			return;
		}

		// 1件でもTaggedMaterialにStagedが居たらStagedに設定して終了
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			if(tm.getTagState().equals(Constants.TAG_STATE_STAGED)){
				m.setMaterialState(Constants.MATERIAL_STATE_STAGED);
				logger.info("MaterialはMATERIAL_STATE_STAGEDに設定されました(TAG_STATE_STAGED:在り)");
				return;
			}
		}

		// 1件でもTaggedMaterialにNotInUseが居たらInstalledに設定して終了(Stagedタグは存在しないので)
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			if(tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
				m.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
				logger.info("MaterialはMATERIAL_STATE_INSTALLEDに設定されました(TAG_STATE_NOT_IN_USE:在り)");
				return;
			}
		}
		// 上記の何れにもあたらない場合は全てPublishedなのでIN-USEに設定
		m.setMaterialState(Constants.MATERIAL_STATE_IN_USE);
		logger.info("MaterialはMATERIAL_STATE_IN_USEに設定されました");
		return;
	}
}
