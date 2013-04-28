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
 * �t�@�C���p�X�E���[�e�B���e�B
 * @author kazyury
 *
 */
public class PathUtil {
	private static Logger logger = Logger.getGlobal();
	private static Properties dirProperties = new Properties();
	private static int ARCHIVE_MODE_COPY = 0;
	private static int ARCHIVE_MODE_MOVE = 1;

	static {
		logger.info("�v���p�e�B�t�@�C���̓ǂݍ��݂��J�n���܂�");
		InputStream dirstream = PathUtil.class.getResourceAsStream("/dir.properties");
		try {
			logger.info("�f�B���N�g���v���p�e�B�̓ǂݍ��ݒ�");
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
	 * �f�ނ̃x�[�X��(yyyymmdd_hhmmss)��ԋp����B�g���q�͕t�^���Ȃ��B
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
	 * �f�ނ̃t�@�C����(yyyymmdd_hhmmss.jpg��)��ԋp����
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
			throw new MWImplementationError("�f�ރ^�C�v���s���ł�");
		}
		return materialName.toString();
	}

	/**
	 * Material�̐Î~�摜�̃t�@�C�����𕶎���Ƃ��ĕԋp����B
	 * (�Î~��f�ނ̏ꍇ��getFileName�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t�@�C����)
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
	 * �X�e�[�W���O�G���A�ɑ��݂���Material�̃t���p�X��ԋp����B
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
	 * Production���ɑ��݂���Material�̃t���p�X��ԋp����B
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
	 * �X�e�[�W���O�G���A�ɑ��݂���Material�̐Î~�摜�̃t���p�X��ԋp����B
	 * (�Î~��f�ނ̏ꍇ��getInstalledFilePath�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t���p�X)
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
	 * Production�ɑ��݂���Material�̐Î~�摜�̃t���p�X��ԋp����B
	 * (�Î~��f�ނ̏ꍇ��getProductionFilePath�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t���p�X)
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
	 * �X�e�[�W���O�G���A�ɑ��݂���Material�̃T���l�C���̃t���p�X��ԋp����B
	 * ����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃T���l�C���̃p�X��ԋp����B
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
	 * Production���ɑ��݂���Material�̃T���l�C���̃t���p�X��ԋp����B
	 * ����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃T���l�C���̃p�X��ԋp����B
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
	 * �^����ꂽPath���f�ނ�����킷�ꍇ�A���̑f�ނ�MaterialID��ԋp����B
	 * @param path
	 * @return
	 * @throws MWException 
	 */
	public static String toMaterialId(Path path) {

		// �f�ރG���A�ɑ��݂���f�ރt�@�C�����ۂ��̊m�F
		String filename = path.getFileName().toString();
		String parent = getDirectoryProperty(Constants.DIRPROP_KEY_MW_MATERIAL);
		FileSystem fs = FileSystems.getDefault();
		Path materialPath = fs.getPath(parent, filename);
		logger.info("materialPath �� "+materialPath.toString()+"�ł�");
		
		// �f�ރG���A�ɑ��݂��Ȃ��@���́@(���݂��Ă�)ff����n�܂�ꍇ�ɂ͗�O
		if(!Files.exists(materialPath, LinkOption.NOFOLLOW_LINKS) || filename.startsWith("ff")) {
			logger.warning("Not a material ["+materialPath.toString()+"]");
			throw new MWImplementationError("Not a material ["+materialPath.toString()+"]");
		}
		
		// �}�e���A��ID������ԋp
		StringBuffer buf = new StringBuffer();
		buf.append(filename.substring(0,8));
		buf.append(filename.substring(9,15));
		logger.info("materialId is ["+buf.toString()+"]");
		return buf.toString();
	}

	
	/**
	 * �^����ꂽPath���������g������킷�ꍇ�A���̑f�ނ�MementoID��ԋp����B
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
