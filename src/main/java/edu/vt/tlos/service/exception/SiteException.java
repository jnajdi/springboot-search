package edu.vt.tlos.service.exception;

public class SiteException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public SiteException(String message) {
		super(message);
	}
	
	public SiteException(String message, Throwable t) {
		super(message, t);
	}
}
