package com.wit.exception;

/**
 * Reflects an error returned from the server.
 */
public class ServerException extends Exception {
	private static final long serialVersionUID = -3875483260449303416L;

	public ServerException() {
	}

	public ServerException(final String message) {
		super(message);
	}

	public ServerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ServerException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerException(final Throwable cause) {
		super(cause);
	}
}