package nobugs.nolife.mw.util;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.persistence.Material;

/**
 * ファイルパス・ユーティリティ
 * @author kazyury
 *
 */
public class PathUtil {

	public static String getBaseFileName(Material m) {
		String materialId = m.getMaterialId();
		StringBuffer materialName = new StringBuffer();
		materialName.append(materialId.substring(0, 8));
		materialName.append("_");
		materialName.append(materialId.substring(8));
		return materialName.toString();
	}

	public static String getFileName(Material m) throws MWException {
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			materialName.append(".jpg");
		} else if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.append(".mov");
		} else {
			throw new MWException("素材タイプが不正です");
		}
		return materialName.toString();
	}

	/**
	 * Materialの静止画像のファイル名を文字列として返却する。
	 * (静止画素材の場合はgetFileNameと同値.動画素材の場合はスナップショットのファイル名)
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static String getPhotoFileName(Material m) throws MWException {
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			return getFileName(m);
		}
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		materialName.insert(0, "ff");
		materialName.append(".jpg");
		return materialName.toString();
	}

	/**
	 * Materialのフルパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledFilePath(Material m) throws MWException {
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(), getFileName(m));
	}

	/**
	 * Materialの静止画像のフルパスを返却する。
	 * (静止画素材の場合はgetInstalledFilePathと同値.動画素材の場合はスナップショットのフルパス)
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledPhotoPath(Material m) throws MWException {
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			return getInstalledFilePath(m);
		}
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(),getPhotoFileName(m));
	}

	/**
	 * Materialのサムネイルのフルパスを返却する。
	 * 動画素材の場合はスナップショットのサムネイルのパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledThumbnailPath(Material m) throws MWException {
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(),"thumbnail",getPhotoFileName(m));
	}

}
