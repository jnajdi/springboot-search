package edu.vt.tlos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import edu.vt.tlos.ApplicationConfig;
import edu.vt.tlos.domain.ContentResource;
import edu.vt.tlos.service.Utilities;

@Repository
public class ContentResourceRepository {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ApplicationConfig applicationConfig;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<ContentResource> findSiteResources(String siteId) {
		
		String sql = "select resource_type_id, resource_uuid, resource_id, file_path, binary_entity from content_resource where resource_id like '/group/" + siteId + "%'";
		logger.debug("--->" + sql);
		
		List<ContentResource> contentResouces = jdbcTemplate.query(sql, new  ContentResourceMapper(applicationConfig.getRootContentPath(), applicationConfig.getHost()));
		
		if (contentResouces != null) {
			return contentResouces;
		}
		return new ArrayList<ContentResource>();
		
	}
	
	public ContentResource findContentResourceByUuid(String resource_uuid) {
		String sql = "select resource_type_id, resource_uuid, resource_id, file_path, binary_entity from content_resource where resource_uuid = '" + resource_uuid + "'";
		logger.debug("--->" + sql);
		
		List<ContentResource> contentResouces = jdbcTemplate.query(sql, new  ContentResourceMapper(applicationConfig.getRootContentPath(), applicationConfig.getHost()));
		if (contentResouces != null && contentResouces.size() > 0) {
			return contentResouces.get(0);
		} else {
			logger.debug("No contentResouces");
		}
		
		return null;
	}
	
	public ContentResource findContentResourceById(String resource_id) {
		String sql = "select resource_type_id, resource_uuid, resource_id, file_path, binary_entity from content_resource where resource_id = '" + resource_id + "'";
		logger.debug("--->" + sql);
		
		List<ContentResource> contentResouces = jdbcTemplate.query(sql, new  ContentResourceMapper(applicationConfig.getRootContentPath(), applicationConfig.getHost()));
		if (contentResouces != null && contentResouces.size() > 0) {
			return contentResouces.get(0);
		}
		
		return null;
	}
	
	
}

class ContentResourceMapper implements RowMapper<ContentResource> {
	private String rootContent;
	private String host;
	
	public ContentResourceMapper(String rootContent, String host) {
		this.rootContent = rootContent;
		this.host = host;
	}
	@Override
	public ContentResource mapRow(ResultSet rset, int rowNum) throws SQLException {
		ContentResource contentResource = new ContentResource();
			
		contentResource.resourceId = rset.getString("resource_id");
		
		String filePath = rset.getString("file_path");
		
		if (this.host.equals("scholar.vt.edu")) {
			contentResource.serverFilePath= Utilities.replaceContentPath(this.rootContent, filePath, ApplicationConfig.PROD_CONTENT_ID);
		} else {
			contentResource.serverFilePath= this.rootContent + filePath;
		}
		contentResource.resourceUuid = rset.getString("resource_uuid");
		contentResource.resourceTypeId = rset.getString("resource_type_id");
		contentResource.blobBinaryEntity  = rset.getBlob("binary_entity");
		
		if (contentResource.resourceId != null) {
			int index = contentResource.resourceId.lastIndexOf("/");
			if (index != -1) {
				contentResource.filename = contentResource.resourceId.substring(index+1);
			}
		}
		return contentResource;
	}
}