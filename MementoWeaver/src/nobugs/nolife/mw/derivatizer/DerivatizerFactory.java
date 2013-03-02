package nobugs.nolife.mw.derivatizer;

import java.io.File;

public class DerivatizerFactory {
	public static Derivatizer getDerivatizer(File path) {

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
	
}
