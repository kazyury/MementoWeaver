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
		logger.info("プロパティファイルの読み込みを開始します");
		InputStream dirstream = this.getClass().getResourceAsStream("/dir.properties");
		InputStream bodstream = this.getClass().getResourceAsStream("/bod.properties");
		try {
			logger.info("ディレクトリプロパティの読み込み中");
			dirProperties.load(dirstream);
			logger.info("BoDプロパティの読み込み中");
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
	 * keyで識別されるユーザーのyyyymmdd時点における計算上の年齢を返却する
	 * @param key
	 * @param yyyymmdd
	 * @return
	 */
	public int calcAge(String key, String yyyymmdd){
		logger.info("年齢計算を開始します key="+key+" 素材年月日="+yyyymmdd);
		int age = 0;
		// TODO 年齢計算の実装
		return age;
	}

	public void storeMaterialSourceCache(String newPath) throws MWException {
		dirProperties.setProperty("dir.materialSource", newPath);
		try {
			// クラスパスに書き込むための小賢しい方法
			File cache = new File(this.getClass().getResource("/dir.properties").toURI().getPath());
			OutputStream out = new FileOutputStream(cache);
			dirProperties.store(out, "cached material source path");
		} catch (Exception e1) {
			throw new MWException("例外が発生しました",e1.getCause());
		}
	}
}
