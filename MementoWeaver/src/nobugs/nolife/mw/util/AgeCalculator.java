package nobugs.nolife.mw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


public class AgeCalculator {
	private static Logger logger = Logger.getGlobal();
	private static Properties bodProperties = new Properties();

	static {
		logger.info("プロパティファイルの読み込みを開始します");
		InputStream bodstream = PathUtil.class.getResourceAsStream("/bod.properties");
		try {
			logger.info("生年月日プロパティの読み込み中");
			bodProperties.load(bodstream);
			bodstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 年齢を簡単な方法で計算する.
	 * <p>
	 * 出典 : http://itpro.nikkeibp.co.jp/article/Watcher/20070822/280097/
	 * <cite> 生年月日から年齢を計算する簡単な計算式というのがあるそうです<br />
	 * (今日の日付-誕生日)/10000の小数点以下切捨て。</cite>
	 * </p>
	 * @param key
	 * @param yyyymmdd
	 * @return
	 */
	public static int calcAge(String key, String yyyymmdd){
		logger.info("年齢計算を開始します key="+key+" 素材年月日="+yyyymmdd);
		int dob = Integer.parseInt(bodProperties.getProperty(key));
		int base = Integer.parseInt(yyyymmdd);
		return (base - dob)/10000;
	}
}
