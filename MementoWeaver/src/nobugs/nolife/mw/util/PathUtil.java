package nobugs.nolife.mw.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;

/**
 * ファイルパス・ユーティリティ
 * @author kazyury
 *
 */
public class PathUtil {
	private static Logger logger = Logger.getGlobal();
	private static Properties dirProperties = new Properties();

	static {
		logger.info("プロパティファイルの読み込みを開始します");
		InputStream dirstream = PathUtil.class.getResourceAsStream("/dir.properties");
		try {
			logger.info("ディレクトリプロパティの読み込み中");
			dirProperties.load(dirstream);
			dirstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getDirectoryProperty(String key){
		return dirProperties.getProperty(key);
	}
	
	/**
	 * 素材のベース名(yyyymmdd_hhmmss)を返却する。拡張子は付与しない。
	 * @param m
	 * @return
	 */
	public static String getBaseFileName(Material m) {
		String materialId = m.getMaterialId();
		StringBuffer materialName = new StringBuffer();
		materialName.append(materialId.substring(0, 8));
		materialName.append("_");
		materialName.append(materialId.substring(8));
		return materialName.toString();
	}

	/**
	 * 素材のファイル名(yyyymmdd_hhmmss.jpg等)を返却する
	 * @param m
	 * @return
	 * @throws MWException
	 */
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
	 * ステージングエリアに存在するMaterialのフルパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledFilePath(Material m) throws MWException {
		FileSystem fs = FileSystems.getDefault();
		String stagingAreaPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_STAGING_AREA);
		return fs.getPath(stagingAreaPath, getFileName(m));
	}

	/**
	 * Production環境に存在するMaterialのフルパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getProductionFilePath(Material m) throws MWException {
		FileSystem fs = FileSystems.getDefault();
		String productionPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL);
		return fs.getPath(productionPath, getFileName(m));
	}

	/**
	 * ステージングエリアに存在するMaterialの静止画像のフルパスを返却する。
	 * (静止画素材の場合はgetInstalledFilePathと同値.動画素材の場合はスナップショットのフルパス)
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledPhotoPath(Material m) throws MWException {
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			return getInstalledFilePath(m);
		}
		FileSystem fs = FileSystems.getDefault();
		String stagingAreaPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_STAGING_AREA);
		return fs.getPath(stagingAreaPath,getPhotoFileName(m));
	}

	/**
	 * Productionに存在するMaterialの静止画像のフルパスを返却する。
	 * (静止画素材の場合はgetProductionFilePathと同値.動画素材の場合はスナップショットのフルパス)
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getProductionPhotoPath(Material m) throws MWException {
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			return getProductionFilePath(m);
		}
		FileSystem fs = FileSystems.getDefault();
		String productionPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL);
		return fs.getPath(productionPath,getPhotoFileName(m));
	}

	/**
	 * ステージングエリアに存在するMaterialのサムネイルのフルパスを返却する。
	 * 動画素材の場合はスナップショットのサムネイルのパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledThumbnailPath(Material m) throws MWException {
		FileSystem fs = FileSystems.getDefault();
		String stagingAreaPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_STAGING_AREA);
		return fs.getPath(stagingAreaPath,"thumbnail",getPhotoFileName(m));
	}

	/**
	 * Production環境に存在するMaterialのサムネイルのフルパスを返却する。
	 * 動画素材の場合はスナップショットのサムネイルのパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getProductionThumbnailPath(Material m) throws MWException {
		FileSystem fs = FileSystems.getDefault();
		String productionPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL);
		return fs.getPath(productionPath,"thumbnail",getPhotoFileName(m));
	}

}
