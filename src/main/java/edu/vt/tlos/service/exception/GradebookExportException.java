package edu.vt.tlos.service.exception;

public class GradebookExportException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public GradebookExportException(String message) {
		super(message);
	}
	
	public GradebookExportException(String message, Throwable t) {
		super(message, t);
	}
}
