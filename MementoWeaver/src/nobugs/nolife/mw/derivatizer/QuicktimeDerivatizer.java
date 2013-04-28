package nobugs.nolife.mw.derivatizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Logger;

import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.util.Constants;

public class QuicktimeDerivatizer extends Derivatizer {
	private static Logger logger = Logger.getGlobal();
	private Path path;

	public QuicktimeDerivatizer(File path){	this.path = path.toPath(); }
	public QuicktimeDerivatizer(Path path){	this.path = path; }

	@Override
	public void derivate() {
		// 動画スナップショットの作成
		Path snapshotPath = createSnapshot();
		// 動画スナップショットのサムネイル(動画サムネイル)の作成
		createThumbnail(snapshotPath);
	}

	/** スナップショットを作成する.
	 * 
	 * @return 作成したスナップショットのPathオブジェクト
	 * @throws MWException 
	 */
	private Path createSnapshot() {
		String filename = path.getFileName().toString();
		int pos = filename.lastIndexOf(".");
		String basename = filename.substring(0,pos);
		FileSystem fs = FileSystems.getDefault();
		Path snapshotFilePath = fs.getPath(path.getParent().toString(), "ff"+basename+".jpg");

		Runtime rt = Runtime.getRuntime();
		// ffmpeg は 最初に -i オプションを指定しないとCodecを認識できないらしい。
		String command = Constants.FFMPEG_PATH+" -i " + path.toString()+Constants.FFMPEG_OPTS+snapshotFilePath.toString();
		logger.info(command+"を実行します");

		Process process = null;
		try {
			process = rt.exec(command);
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		// rt.execだけだとffmpegが終わる前にサムネイル作成が走り始めてしまうっぽい。
		//			rt.exec(command);
		String result;
		try {
			while ((result = br.readLine()) != null) {
				logger.fine(result);
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}

		return snapshotFilePath;
	}
}
