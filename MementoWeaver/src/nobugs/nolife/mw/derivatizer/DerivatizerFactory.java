package nobugs.nolife.mw.derivatizer;

import java.io.File;
import java.nio.file.Path;

public class DerivatizerFactory {

	public static Derivatizer getDerivatizer(Path path) {
		int pos = path.toString().lastIndexOf(".");
		String suffix = path.toString().substring(pos+1);
		
		if (suffix.equals("jpg")) {
			return new JpegDerivatizer(path);
		
		} else if (suffix.equals("mov")){
			return new QuicktimeDerivatizer(path);
		
		} else {
			System.out.println("Not supported :["+suffix+"]");
			return null;
		}

	}
	
	public static Derivatizer getDerivatizer(File path) {
		return getDerivatizer(path.toPath());
	}
	
}
