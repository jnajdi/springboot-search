package edu.vt.tlos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

	public static final String  PROD_CONTENT_ID  = "XX";
	
	@Autowired
	private Environment env;
	

	public String getHost() {
		return env.getProperty("host");
	}
	
	public String getArchiveTempDirectory() {
		return env.getProperty("archive_temp_directory");
	}
	
	public String getArchiveZipDirectory() {
		return env.getProperty("archive_zip_directory");
	}
	
	public String getEnvrDomain() {
		return env.getProperty("envr-domain");
	}
	
	public String getWsUsername() {
		return env.getProperty("username");
	}
	
	public String getWsPassword() {
		return env.getProperty("password");
	}
	
	private String getNfsContentPath() {
		return env.getProperty("nfs_content_path");
	}
	
	public String getRootContentPath() {
		String property = getNfsContentPath();
		
		if (property == null || property.isEmpty()) {
			if (getHost().equals("scholar.vt.edu")) {
				return "/apps/nfs/sakai"+ PROD_CONTENT_ID +"/scholar.vt.edu/content";
			}
			return "/nfs/scholarclone/" +  getHost()+ "/content";
		}
		


		return property; 
	}
	
}
