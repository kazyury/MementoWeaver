package nobugs.nolife.mw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;

public class CacheManager {
	private static Logger logger = Logger.getGlobal();
	private static Properties dirProperties = new Properties();

	private CacheManager() {}
	public static void storeMaterialSourceCache(String newPath) throws MWException {
		logger.info("�v���p�e�B�t�@�C���̓ǂݍ��݂��J�n���܂�");
		InputStream dirstream = CacheManager.class.getResourceAsStream("/dir.properties");
		try {
			logger.info("�f�B���N�g���v���p�e�B�̓ǂݍ��ݒ�");
			dirProperties.load(dirstream);
			dirstream.close();
			dirProperties.setProperty("dir.materialSource", newPath);
			// �N���X�p�X�ɏ������ނ��߂̏����������@
			File cache = new File(CacheManager.class.getResource("/dir.properties").toURI().getPath());
			OutputStream out = new FileOutputStream(cache);
			dirProperties.store(out, "cached material source path");
		} catch (Exception e) {
			throw new MWException("��O���������܂���",e.getCause());
		}
	}
}
