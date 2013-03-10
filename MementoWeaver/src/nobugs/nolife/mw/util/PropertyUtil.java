package nobugs.nolife.mw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyUtil {
	private Properties dirProperties = new Properties();
	
	public PropertyUtil() {
		InputStream is = this.getClass().getResourceAsStream("/dir.properties");
		try {
			dirProperties.load(is);
			is.close();
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
