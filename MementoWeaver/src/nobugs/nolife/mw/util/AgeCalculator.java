package nobugs.nolife.mw.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Logger;


public class AgeCalculator {
	private static Logger logger = Logger.getGlobal();
	private static Properties bodProperties = new Properties();

	static {
		logger.info("�v���p�e�B�t�@�C���̓ǂݍ��݂��J�n���܂�");
		InputStream bodstream = PathUtil.class.getResourceAsStream("/bod.properties");
		try {
			logger.info("���N�����v���p�e�B�̓ǂݍ��ݒ�");
			bodProperties.load(bodstream);
			bodstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �N����ȒP�ȕ��@�Ōv�Z����.
	 * <p>
	 * �o�T : http://itpro.nikkeibp.co.jp/article/Watcher/20070822/280097/
	 * <cite> ���N��������N����v�Z����ȒP�Ȍv�Z���Ƃ����̂����邻���ł�<br />
	 * (�����̓��t-�a����)/10000�̏����_�ȉ��؎̂āB</cite>
	 * </p>
	 * @param key
	 * @param yyyymmdd
	 * @return
	 */
	public static int calcAge(String key, String yyyymmdd){
		logger.info("�N��v�Z���J�n���܂� key="+key+" �f�ޔN����="+yyyymmdd);
		int dob = getDateOfBirth(key);
		int base = Integer.parseInt(yyyymmdd);
		return (base - dob)/10000;
	}

	/**
	 * �N����ȒP�ȕ��@�Ōv�Z���A2���̕�����Ƃ��ĕԋp����B
	 * @param key
	 * @param yyyymmdd
	 * @return 2���Ńt�H�[�}�b�g���ꂽ�N��("08"��)
	 */
	public static String calcAgeAsFormattedString(String key, String yyyymmdd){
		int age = calcAge(key, yyyymmdd);
		DecimalFormat formatter = new DecimalFormat("00");
		return formatter.format(age);
	}

	
	public static int getDateOfBirth(String key) {
		return Integer.parseInt(bodProperties.getProperty(key));
	}
}
