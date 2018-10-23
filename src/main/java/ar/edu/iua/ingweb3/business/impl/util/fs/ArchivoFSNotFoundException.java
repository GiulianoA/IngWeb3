package ar.edu.iua.ingweb3.business.impl.util.fs;

public class ArchivoFSNotFoundException extends RuntimeException {

	public ArchivoFSNotFoundException() {
		super();
	}

	public ArchivoFSNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ArchivoFSNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchivoFSNotFoundException(String message) {
		super(message);
	}

	public ArchivoFSNotFoundException(Throwable cause) {
		super(cause);
	}

}
