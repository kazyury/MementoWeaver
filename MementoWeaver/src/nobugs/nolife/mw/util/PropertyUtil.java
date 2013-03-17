package nobugs.nolife.mw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyUtil {
	private Properties dirProperties = new Properties();
	private Properties bodProperties = new Properties();
	
	public PropertyUtil() {
		InputStream dirstream = this.getClass().getResourceAsStream("/dir.properties");
		InputStream bodstream = this.getClass().getResourceAsStream("/bod.properties");
		try {
			dirProperties.load(dirstream);
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
		int age = 0;
		// TODO 年齢計算の実装
		System.out.println("年齢計算のつもり");
		return age;
	}

	public void storeMaterialSourceCache(String newPath) {
		dirProperties.setProperty("dir.materialSource", newPath);
		try {
			// クラスパスに書き込むための小賢しい方法
			File cache = new File(this.getClass().getResource("/dir.properties").toURI().getPath());
			OutputStream out = new FileOutputStream(cache);
			dirProperties.store(out, "cached material source path");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
