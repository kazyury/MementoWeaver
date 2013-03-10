package nobugs.nolife.mw.util;

import nobugs.nolife.mw.persistence.Material;

public class MaterialUtil {

	public static String getFileBasename(Material m) {
		String materialId = m.getMaterialId();
		StringBuffer materialName = new StringBuffer();
		materialName.append(materialId.substring(0, 8));
		materialName.append("_");
		materialName.append(materialId.substring(8));
		return materialName.toString();
	}

	public static String getFileName(Material m) {
		StringBuffer materialName = new StringBuffer(getFileBasename(m));
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			materialName.append(".jpg");
		} else if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.append(".mov");
		} else {
			System.out.println("素材タイプ不正");
		}
		return materialName.toString();
	}

	public static String getPhotoFileName(Material m) {
		StringBuffer materialName = new StringBuffer(getFileBasename(m));
		if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.insert(0, "ff");
		}
		materialName.append(".jpg");
		return materialName.toString();
	}
	
}
