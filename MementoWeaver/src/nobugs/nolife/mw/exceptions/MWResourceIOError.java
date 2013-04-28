package nobugs.nolife.mw.exceptions;

public class MWResourceIOError extends MWRuntimeError {

	private static final long serialVersionUID = -4840210628494824800L;

	public MWResourceIOError() {
	}

	public MWResourceIOError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MWResourceIOError(String message, Throwable cause) {
		super(message, cause);
	}

	public MWResourceIOError(String message) {
		super(message);
	}

	public MWResourceIOError(Throwable cause) {
		super(cause);
	}

}
