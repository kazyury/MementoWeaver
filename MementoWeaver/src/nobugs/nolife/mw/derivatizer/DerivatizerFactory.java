package nobugs.nolife.mw.derivatizer;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class DerivatizerFactory {
	private static Logger logger = Logger.getGlobal();

	public static Derivatizer getDerivatizer(Path path) {
		int pos = path.toString().lastIndexOf(".");
		String suffix = path.toString().substring(pos+1);
		
		if (suffix.equals("jpg")) {
			return new JpegDerivatizer(path);
		
		} else if (suffix.equals("mov")){
			return new QuicktimeDerivatizer(path);
		
		} else {
			logger.warning("Not supported :["+suffix+"]");
			// TODO ó·äOÉXÉçÅ[
			return null;
		}

	}
	
	public static Derivatizer getDerivatizer(File path) {
		return getDerivatizer(path.toPath());
	}
	
}
