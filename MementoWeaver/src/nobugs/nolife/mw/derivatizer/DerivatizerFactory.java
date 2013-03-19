package nobugs.nolife.mw.derivatizer;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;

public class DerivatizerFactory {
	private static Logger logger = Logger.getGlobal();

	public static Derivatizer getDerivatizer(Path path) throws MWException {
		int pos = path.toString().lastIndexOf(".");
		String suffix = path.toString().substring(pos+1);
		
		if (suffix.equals("jpg")) {
			return new JpegDerivatizer(path);
		
		} else if (suffix.equals("mov")){
			return new QuicktimeDerivatizer(path);
		
		} else {
			throw new MWException("Not supported :["+suffix+"]");
		}

	}
	
	public static Derivatizer getDerivatizer(File path) throws MWException {
		return getDerivatizer(path.toPath());
	}
	
}
