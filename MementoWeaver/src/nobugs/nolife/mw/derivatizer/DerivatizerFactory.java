package nobugs.nolife.mw.derivatizer;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

import nobugs.nolife.mw.exceptions.MWImplementationError;
import nobugs.nolife.mw.exceptions.MWInvalidUserInputException;

public class DerivatizerFactory {
	private static Logger logger = Logger.getGlobal();

	public static Derivatizer getDerivatizer(Path path) throws MWInvalidUserInputException {
		int pos = path.toString().lastIndexOf(".");
		String suffix = path.toString().substring(pos+1);
		
		if (suffix.equals("jpg")) {
			logger.fine("�g���q��jpg�ł��BJpegDerivatizer���g�p���܂�");
			return new JpegDerivatizer(path);
		
		} else if (suffix.equals("mov")){
			logger.fine("�g���q��mov�ł��BQuicktimeDerivatizer���g�p���܂�");
			return new QuicktimeDerivatizer(path);
		
		} else {
			throw new MWImplementationError("Not supported :["+suffix+"]");
		}

	}
	
	public static Derivatizer getDerivatizer(File path) throws MWInvalidUserInputException {
		return getDerivatizer(path.toPath());
	}
	
}
