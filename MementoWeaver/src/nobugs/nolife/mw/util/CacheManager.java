package nobugs.nolife.mw.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

import nobugs.nolife.mw.exceptions.MWResourceIOError;

public class CacheManager {
	private static Logger logger = Logger.getGlobal();
	private static Properties dirProperties = new Properties();

	private CacheManager() {}
	public static void storeMaterialSourceCache(String newPath) {
		logger.fine("プロパティファイルの読み込みを開始します");
		logger.fine("ディレクトリプロパティの読み込み中");
		try(InputStream dirstream = CacheManager.class.getResourceAsStream("/dir.properties")) {
			dirProperties.load(dirstream);
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
		dirProperties.setProperty("dir.materialSource", newPath);

		// クラスパスに書き込むための小賢しい方法
		File cache=null;
		logger.fine("ディレクトリプロパティの書き込み中");
		try {
			cache = new File(CacheManager.class.getResource("/dir.properties").toURI().getPath());
			OutputStream out = new FileOutputStream(cache);
			dirProperties.store(out, "cached material source path");
		} catch (URISyntaxException|IOException e) {
			throw new MWResourceIOError(e);
		}
	}
}
