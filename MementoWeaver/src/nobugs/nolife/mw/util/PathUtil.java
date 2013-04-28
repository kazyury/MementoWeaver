package nobugs.nolife.mw.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Logger;


import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWImplementationError;
import nobugs.nolife.mw.exceptions.MWResourceIOError;

/**
 * ファイルパス・ユーティリティ
 * @author kazyury
 *
 */
public class PathUtil {
	private static Logger logger = Logger.getGlobal();
	private static Properties dirProperties = new Properties();
	private static int ARCHIVE_MODE_COPY = 0;
	private static int ARCHIVE_MODE_MOVE = 1;

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
	public static String getFileName(Material m) {
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			materialName.append(".jpg");
		} else if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.append(".mov");
		} else {
			throw new MWImplementationError("素材タイプが不正です");
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
	 * ステージングエリアに存在するMaterialのフルパスを返却する。
	 * @param m
	 * @return
	 * @throws MWException 
	 */
	public static Path getInstalledFilePath(Material m) {
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
	public static Path getProductionFilePath(Material m) {
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
	public static Path getInstalledPhotoPath(Material m) {
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
	public static Path getProductionPhotoPath(Material m) {
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
	public static Path getInstalledThumbnailPath(Material m) {
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
	public static Path getProductionThumbnailPath(Material m) {
		FileSystem fs = FileSystems.getDefault();
		String productionPath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL);
		return fs.getPath(productionPath,"thumbnail",getPhotoFileName(m));
	}
	
	/**
	 * 与えられたPathが素材をあらわす場合、その素材のMaterialIDを返却する。
	 * @param path
	 * @return
	 * @throws MWException 
	 */
	public static String toMaterialId(Path path) {

		// 素材エリアに存在する素材ファイルか否かの確認
		String filename = path.getFileName().toString();
		String parent = getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL);
		FileSystem fs = FileSystems.getDefault();
		Path materialPath = fs.getPath(parent, filename);
		logger.info("materialPath は "+materialPath.toString()+"です");
		
		// 素材エリアに存在しない　又は　(存在しても)ffから始まる場合には例外
		if(!Files.exists(materialPath, LinkOption.NOFOLLOW_LINKS) || filename.startsWith("ff")) {
			logger.warning("Not a material ["+materialPath.toString()+"]");
			throw new MWImplementationError("Not a material ["+materialPath.toString()+"]");
		}
		
		// マテリアルID相当を返却
		StringBuffer buf = new StringBuffer();
		buf.append(filename.substring(0,8));
		buf.append(filename.substring(9,15));
		logger.info("materialId is ["+buf.toString()+"]");
		return buf.toString();
	}

	
	/**
	 * 与えられたPathがメメントをあらわす場合、その素材のMementoIDを返却する。
	 * @param path
	 * @return
	 */
	public static String toMementoId(Path path){
		logger.fine("argument path is "+path);
		String filename = path.getFileName().toString();
		String mementoId = filename.substring(0, filename.indexOf("."));

		logger.fine("return mementoId is "+mementoId);
		return mementoId;
	}

	/**
	 * @see toMementoId(Path path)
	 * @param strpath
	 * @return
	 */
	public static String toMementoId(String strpath) {
		File file = new File(strpath);
		return toMementoId(file.toPath());
	}
	
	public static void moveToArchive(String source) { archiveFile(ARCHIVE_MODE_MOVE, source); }
	public static void moveToArchive(Path source) { archiveFile(ARCHIVE_MODE_MOVE, source.toString()); }
	public static void copyToArchive(String source) { archiveFile(ARCHIVE_MODE_COPY, source); }
	public static void copyToArchive(Path source) { archiveFile(ARCHIVE_MODE_COPY, source.toString()); }
	
	private static void archiveFile(int mode, String source) {
		URI mwroot = new File(getDirectoryProperty(Constants.DIRPROP_KEY_MW_ROOT)).toURI();
		File sourceFile = new File(source);
		URI  sourceURI  = sourceFile.toURI();
		String relativePath = mwroot.relativize(sourceURI).toString();

		FileSystem fs = FileSystems.getDefault();
		Path sourcePath = sourceFile.toPath();
		Path targetPath = fs.getPath(getDirectoryProperty(Constants.DIRPROP_KEY_ARCHIVE_AREA), relativePath);
		try {
			if(mode == ARCHIVE_MODE_COPY) {
				Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} else if (mode == ARCHIVE_MODE_MOVE) {
				Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} else {
				throw new MWImplementationError("[BUG] invalid mode.");
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
	}
}
