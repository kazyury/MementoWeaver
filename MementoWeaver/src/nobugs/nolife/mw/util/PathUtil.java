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
 * �t�@�C���p�X�E���[�e�B���e�B
 * @author kazyury
 *
 */
public class PathUtil {
	private static Logger logger = Logger.getGlobal();
	private static Properties dirProperties = new Properties();

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
	public static String getFileName(Material m) throws MWException {
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			materialName.append(".jpg");
		} else if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.append(".mov");
		} else {
			throw new MWException("�f�ރ^�C�v���s���ł�");
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
	 * �X�e�[�W���O�G���A�ɑ��݂���Material�̃t���p�X��ԋp����B
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
	 * Production���ɑ��݂���Material�̃t���p�X��ԋp����B
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
	 * �X�e�[�W���O�G���A�ɑ��݂���Material�̐Î~�摜�̃t���p�X��ԋp����B
	 * (�Î~��f�ނ̏ꍇ��getInstalledFilePath�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t���p�X)
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
	 * Production�ɑ��݂���Material�̐Î~�摜�̃t���p�X��ԋp����B
	 * (�Î~��f�ނ̏ꍇ��getProductionFilePath�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t���p�X)
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
	 * �X�e�[�W���O�G���A�ɑ��݂���Material�̃T���l�C���̃t���p�X��ԋp����B
	 * ����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃T���l�C���̃p�X��ԋp����B
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
	 * Production���ɑ��݂���Material�̃T���l�C���̃t���p�X��ԋp����B
	 * ����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃T���l�C���̃p�X��ԋp����B
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
