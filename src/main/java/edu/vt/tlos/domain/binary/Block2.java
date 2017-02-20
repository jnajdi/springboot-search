package edu.vt.tlos.domain.binary;

public class Block2 {

	public static final int BLOCK_2 = 12;
	
	private long releaseDate = -1;
	private long retractDate = -1;
	
	public long getReleaseDate() {
		return releaseDate;
	}
	
	public void setReleaseDate(long releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	public long getRetractDate() {
		return retractDate;
	}

	public void setRetractDate(long retractDate) {
		this.retractDate = retractDate;
	}

	
}
