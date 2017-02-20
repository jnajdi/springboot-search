package edu.vt.tlos.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.vt.tlos.ApplicationConfig;
import edu.vt.tlos.domain.ContentResource;
import edu.vt.tlos.repository.ContentResourceRepository;
import edu.vt.tlos.service.exception.SiteException;
import edu.vt.tlos.service.exception.UserException;

@Service
public class ResourceExportLogicImpl extends ExportLogic{

	@Autowired
	private ApplicationConfig applicationConfig;

	@Autowired
	private ContentResourceRepository contentResourceRepository;
	
	private static final String RESOURCE_DIR = "resources";
	
	@Override
	public void export(String directory) throws UserException, SiteException {
		
		if (user == null) {
			throw new UserException("Invalid user: " + user);
		}
		
		if (site == null) {
			throw new SiteException("Invalid site: " + site);
		}
		
		logger.info("Retrieving resources for site: " + site.siteId  + " ("+ user.pid +") ");
		
		List<ContentResource> contentResources = contentResourceRepository.findSiteResources(site.siteId);
		if (contentResources != null) {
			logger.info("Site Resources: " + contentResources.size());
			site.contentResources = contentResources;
			for (ContentResource resource: site.contentResources) {
				
				logger.info("Resource Id: " + resource.resourceId);
				logger.info("Resource Name: " + resource.filename);
				
				logger.info("Resource Server File Path: " + resource.serverFilePath);
				logger.info(applicationConfig.getArchiveTempDirectory());
				
				resource.downloadLocalResource(directory, RESOURCE_DIR);
			}
		}
	}

}
