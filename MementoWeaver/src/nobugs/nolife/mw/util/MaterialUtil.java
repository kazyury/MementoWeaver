package nobugs.nolife.mw.util;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import nobugs.nolife.mw.image.ImageManipulator;
import nobugs.nolife.mw.persistence.Material;

public class MaterialUtil {

	public static String getBaseFileName(Material m) {
		String materialId = m.getMaterialId();
		StringBuffer materialName = new StringBuffer();
		materialName.append(materialId.substring(0, 8));
		materialName.append("_");
		materialName.append(materialId.substring(8));
		return materialName.toString();
	}

	public static String getFileName(Material m) {
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			materialName.append(".jpg");
		} else if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.append(".mov");
		} else {
			System.out.println("素材タイプ不正");
		}
		return materialName.toString();
	}

	/**
	 * Materialの静止画像のファイル名を文字列として返却する。
	 * (静止画素材の場合はgetFileNameと同値.動画素材の場合はスナップショットのファイル名)
	 * @param m
	 * @return
	 */
	public static String getPhotoFileName(Material m) {
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
	 */
	public static Path getInstalledFilePath(Material m) {
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(), getFileName(m));
	}

	/**
	 * Materialの静止画像のフルパスを返却する。
	 * (静止画素材の場合はgetInstalledFilePathと同値.動画素材の場合はスナップショットのフルパス)
	 * @param m
	 * @return
	 */
	public static Path getInstalledPhotoPath(Material m) {
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
	 */
	public static Path getInstalledThumbnailPath(Material m) {
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(),"thumbnail",getPhotoFileName(m));
	}

	/** 
	 * 画像を回転して保管する。静止画素材本体とサムネイルが対象となる。
	 * @param m
	 * @param degree:回転角
	 */
	public static void rotatePhoto(Material m,int degree){
		if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			System.out.println("動画素材は回転に対応していません"); // TODO 例外送出
			return;
		}

		try {
			// 素材本体を回転する。
			ImageManipulator.rotate(getInstalledPhotoPath(m), degree);
			// サムネイルを回転する。
			ImageManipulator.rotate(getInstalledThumbnailPath(m), degree);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
