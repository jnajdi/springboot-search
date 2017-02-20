package edu.vt.tlos.service.exception;

public class ResourcesExportException  extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public ResourcesExportException(String message) {
		super(message);
	}
	
	public ResourcesExportException(String message, Throwable t) {
		super(message, t);
	}
}
