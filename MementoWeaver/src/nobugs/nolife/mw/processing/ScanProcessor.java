package nobugs.nolife.mw.processing;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;

public class ScanProcessor {
	private static Logger logger = Logger.getGlobal();
	private String scanmode;

	public void setScanMode(String scanmode) {
		this.scanmode = scanmode;
	}
	
	public List<String> scan() throws MWException {
		// TODO List<ScannedMementos>を返却するようにする予定
		// TODO *.html以外にも対応させる
		// MWROOT以下の*.htmlを全走査
		MementoFinder finder = new MementoFinder("*.html", scanmode);
		File start = new File(PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_ROOT));
		try {
			Files.walkFileTree(start.toPath(), finder);
		} catch (IOException e) {
			logger.severe("走査中にIOExceptionが発生しました.");
			throw new MWException(e);
		}
		return finder.getResult();
	}
	
	/**
	 * 指定されたパターンにマッチするファイルを走査する.
	 * @see <a href="http://docs.oracle.com/javase/tutorial/essential/io/walk.html">Walking the File Tree</a>
	 * @author kazyury
	 *
	 */
	public static class MementoFinder extends SimpleFileVisitor<Path> {
		private final PathMatcher matcher;
		private Logger logger = Logger.getGlobal();
		private String scanmode;
		private List<String> resultList = new ArrayList<String>();
		
		public MementoFinder(String pattern, String scanmode) {
			matcher=FileSystems.getDefault().getPathMatcher("glob:"+pattern);
			this.scanmode = scanmode;
			logger.info("MementoFinderがパターン["+pattern+"],スキャンモード["+scanmode+"]で作成されました");
		}
		
		public List<String> getResult() {
			return resultList;
		}

		// Invoke the pattern matching method on each file.
		// 今回はFileに対する走査だけなのでpreVisitDirectory等はoverride不要
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes bfa) throws IOException {
			// TODO scanmodeによる挙動変更
			Path name = path.getFileName();
			logger.fine("["+name.toString()+"]がマッチするかチェック中...");
			if(name != null && matcher.matches(name)){
				resultList.add(path.toString());
				logger.fine("Found ["+path.toString()+"]");
			}
			return FileVisitResult.CONTINUE;
		}
		
	}
}
