package nobugs.nolife.mw.exceptions;

public class MWFxmlError extends MWConfigurationError {
	private static final long serialVersionUID = -6152004698170268884L;

	public MWFxmlError() {
	}

	public MWFxmlError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MWFxmlError(String message, Throwable cause) {
		super(message, cause);
	}

	public MWFxmlError(String message) {
		super(message);
	}

	public MWFxmlError(Throwable cause) {
		super(cause);
	}

}
