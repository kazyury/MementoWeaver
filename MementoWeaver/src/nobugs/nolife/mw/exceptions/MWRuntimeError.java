package nobugs.nolife.mw.exceptions;

public abstract class MWRuntimeError extends RuntimeException {
	private static final long serialVersionUID = -6795107834278206903L;

	public MWRuntimeError() {
		super();
	}

	public MWRuntimeError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MWRuntimeError(String message, Throwable cause) {
		super(message, cause);
	}

	public MWRuntimeError(String message) {
		super(message);
	}

	public MWRuntimeError(Throwable cause) {
		super(cause);
	}
}
