package nobugs.nolife.mw.exceptions;

public abstract class MWException extends Exception {
	private static final long serialVersionUID = -2560528615788895061L;

	public MWException() {
		super();
	}

	public MWException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public MWException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MWException(String arg0) {
		super(arg0);
	}

	public MWException(Throwable arg0) {
		super(arg0);
	}
}
