package nobugs.nolife.mw.derivatizer;

import java.io.File;
import java.nio.file.Path;

public class JpegDerivatizer extends Derivatizer {
	private Path path;
	
	public JpegDerivatizer(File path){ this.path = path.toPath();}
	public JpegDerivatizer(Path path){ this.path = path; }

	@Override
	public void derivate(){
		// 静止画素材はサムネイルの作成のみ
		createThumbnail(this.path);
	}

}
