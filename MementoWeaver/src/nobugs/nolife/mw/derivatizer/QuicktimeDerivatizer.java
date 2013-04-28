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
		// ����X�i�b�v�V���b�g�̍쐬
		Path snapshotPath = createSnapshot();
		// ����X�i�b�v�V���b�g�̃T���l�C��(����T���l�C��)�̍쐬
		createThumbnail(snapshotPath);
	}

	/** �X�i�b�v�V���b�g���쐬����.
	 * 
	 * @return �쐬�����X�i�b�v�V���b�g��Path�I�u�W�F�N�g
	 * @throws MWException 
	 */
	private Path createSnapshot() {
		String filename = path.getFileName().toString();
		int pos = filename.lastIndexOf(".");
		String basename = filename.substring(0,pos);
		FileSystem fs = FileSystems.getDefault();
		Path snapshotFilePath = fs.getPath(path.getParent().toString(), "ff"+basename+".jpg");

		Runtime rt = Runtime.getRuntime();
		// ffmpeg �� �ŏ��� -i �I�v�V�������w�肵�Ȃ���Codec��F���ł��Ȃ��炵���B
		String command = Constants.FFMPEG_PATH+" -i " + path.toString()+Constants.FFMPEG_OPTS+snapshotFilePath.toString();
		logger.info(command+"�����s���܂�");

		Process process = null;
		try {
			process = rt.exec(command);
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		// rt.exec��������ffmpeg���I���O�ɃT���l�C���쐬������n�߂Ă��܂����ۂ��B
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
