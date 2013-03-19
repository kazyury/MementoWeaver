package nobugs.nolife.mw.derivatizer;

import java.io.File;
import java.nio.file.Path;

import nobugs.nolife.mw.MWException;

public class JpegDerivatizer extends Derivatizer {
	private Path path;
	
	public JpegDerivatizer(File path){ this.path = path.toPath();}
	public JpegDerivatizer(Path path){ this.path = path; }

	@Override
	public void derivate() throws MWException {
		// Ã~‰æ‘fŞ‚ÍƒTƒ€ƒlƒCƒ‹‚Ìì¬‚Ì‚İ
		createThumbnail(this.path);
	}

}
