package nobugs.nolife.mw.exceptions;

public class MWImplementationError extends MWConfigurationError {

	private static final long serialVersionUID = 6408884059203838919L;

	public MWImplementationError() {
	}

	public MWImplementationError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MWImplementationError(String message, Throwable cause) {
		super(message, cause);
	}

	public MWImplementationError(String message) {
		super(message);
	}

	public MWImplementationError(Throwable cause) {
		super(cause);
	}
}
