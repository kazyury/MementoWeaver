package nobugs.nolife.mw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;

public class PropertyUtil {
	private static Logger logger = Logger.getGlobal();
	private Properties dirProperties = new Properties();
	private Properties bodProperties = new Properties();
	
	public PropertyUtil() {
		logger.info("�v���p�e�B�t�@�C���̓ǂݍ��݂��J�n���܂�");
		InputStream dirstream = this.getClass().getResourceAsStream("/dir.properties");
		InputStream bodstream = this.getClass().getResourceAsStream("/bod.properties");
		try {
			logger.info("�f�B���N�g���v���p�e�B�̓ǂݍ��ݒ�");
			dirProperties.load(dirstream);
			logger.info("BoD�v���p�e�B�̓ǂݍ��ݒ�");
			bodProperties.load(bodstream);
			dirstream.close();
			bodstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getMaterialSourceName(){
		return dirProperties.getProperty("dir.materialSource");
	}

	public String getStagingAreaName(){
		return dirProperties.getProperty("dir.stagingArea");
	}
	
	/**
	 * key�Ŏ��ʂ���郆�[�U�[��yyyymmdd���_�ɂ�����v�Z��̔N���ԋp����
	 * @param key
	 * @param yyyymmdd
	 * @return
	 */
	public int calcAge(String key, String yyyymmdd){
		logger.info("�N��v�Z���J�n���܂� key="+key+" �f�ޔN����="+yyyymmdd);
		int age = 0;
		// TODO �N��v�Z�̎���
		return age;
	}

	public void storeMaterialSourceCache(String newPath) throws MWException {
		dirProperties.setProperty("dir.materialSource", newPath);
		try {
			// �N���X�p�X�ɏ������ނ��߂̏����������@
			File cache = new File(this.getClass().getResource("/dir.properties").toURI().getPath());
			OutputStream out = new FileOutputStream(cache);
			dirProperties.store(out, "cached material source path");
		} catch (Exception e1) {
			throw new MWException("��O���������܂���",e1.getCause());
		}
	}
}
