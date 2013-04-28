package nobugs.nolife.mw.exceptions;

public class MWConfigurationError extends MWRuntimeError {

	private static final long serialVersionUID = 6408884059203838919L;

	public MWConfigurationError() {
	}

	public MWConfigurationError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MWConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}

	public MWConfigurationError(String message) {
		super(message);
	}

	public MWConfigurationError(Throwable cause) {
		super(cause);
	}
}
