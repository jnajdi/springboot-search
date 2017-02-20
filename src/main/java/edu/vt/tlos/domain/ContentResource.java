package edu.vt.tlos.domain;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import edu.vt.tlos.domain.binary.BinaryEntity;
import edu.vt.tlos.service.Utilities;

public class ContentResource {
	public static String FORM_HELPER = "org.sakaiproject.metaobj.shared.FormHelper";
	public static String FILE_UPLOAD = "org.sakaiproject.content.types.fileUpload";
	
	public String resourceId;
	public String serverFilePath;
	
	public String resourceUuid;
	public String filename;
	public String resourceTypeId;
	public Blob blobBinaryEntity;
	public BinaryEntity binaryEntity;
	
	
	public String getDisplayName() {
		if (binaryEntity == null)
			binaryEntity = getBinaryEntity();
		
		return binaryEntity.getDisplayName();
	}
	
	public String getDescription() {
		if (binaryEntity == null)
			binaryEntity = getBinaryEntity();
		
		return binaryEntity.getDescription();
	}
	
	public String getModifiedBy() {
		if (binaryEntity == null)
			binaryEntity = getBinaryEntity();
		
		return binaryEntity.getModifiedBy();
	}
	
	public String getLastModified() {
		if (binaryEntity == null)
			binaryEntity = getBinaryEntity();
		
		return binaryEntity.getLastModified();
	}
	
	public String getCreator() {
		if (binaryEntity == null)
			binaryEntity = getBinaryEntity();
		
		return binaryEntity.getCreator();
	}
	
	private BinaryEntity getBinaryEntity() {
		try {
			return new BinaryEntity(blobBinaryEntity);
		} catch (SQLException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	public String downloadLocalResource(String archiveTempDirectory, String resourceDir) {
		
		return Utilities.downloadLocalResource(this, archiveTempDirectory, resourceDir);
	}
	
}
