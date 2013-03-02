package nobugs.nolife.mw.derivatizer;

import java.io.File;

public class JpegDerivatizer extends Derivatizer {
	private File path;
	
	public JpegDerivatizer(File path){
		this.path = path;
	}

	@Override
	public void derivate() {
		createThumbnail(this.path);
	}

}
