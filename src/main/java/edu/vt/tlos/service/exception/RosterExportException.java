package edu.vt.tlos.service.exception;

public class RosterExportException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public RosterExportException(String message) {
		super(message);
	}
	
	public RosterExportException(String message, Throwable t) {
		super(message, t);
	}
}
