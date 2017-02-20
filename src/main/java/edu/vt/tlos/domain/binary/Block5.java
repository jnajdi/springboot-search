package edu.vt.tlos.domain.binary;

public class Block5 {

	public static final int BLOCK_5 = 14;
	
	private String contentType = "";
	private long contentLength;
	private String filePath = "";
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
