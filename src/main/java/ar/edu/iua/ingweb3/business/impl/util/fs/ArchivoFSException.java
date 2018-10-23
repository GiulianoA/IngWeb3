package ar.edu.iua.ingweb3.business.impl.util.fs;

public class ArchivoFSException extends RuntimeException {

	public ArchivoFSException() {
		super();
	}

	public ArchivoFSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ArchivoFSException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchivoFSException(String message) {
		super(message);
	}

	public ArchivoFSException(Throwable cause) {
		super(cause);
	}

}
