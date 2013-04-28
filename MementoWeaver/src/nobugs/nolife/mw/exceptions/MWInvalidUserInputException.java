package nobugs.nolife.mw.exceptions;


public class MWInvalidUserInputException extends MWException {
	private static final long serialVersionUID = -7984431383320243417L;

	public MWInvalidUserInputException() {
	}

	public MWInvalidUserInputException(String arg0, Throwable arg1,
			boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public MWInvalidUserInputException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MWInvalidUserInputException(String arg0) {
		super(arg0);
	}

	public MWInvalidUserInputException(Throwable arg0) {
		super(arg0);
	}

}
