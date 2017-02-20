package edu.vt.tlos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.vt.tlos.ApplicationConfig;
import edu.vt.tlos.repository.SiteRepository;
import edu.vt.tlos.service.exception.GradebookServiceException;
import edu.vt.tlos.service.exception.InvalidLoginException;
import edu.vt.tlos.service.exception.SiteException;
import edu.vt.tlos.service.exception.UserException;

@Service
public class GradebookExportLogicImpl extends ExportLogic{

	@Autowired
	private SiteRepository siteRepository;
	
	@Autowired
	private ApplicationConfig applicationConfig;
	
	private static String TOOL_REGISTRATION="sakai.gradebook.gwt.rpc";
	private static String GB_ARCHIVE = "gradebook.csv";
	
	@Override
	public void export(String directory) throws UserException, SiteException {
		if (user == null) {
			throw new UserException("Invalid user: " + user);
		}
		
		if (site == null) {
			throw new SiteException("Invalid site: " + site);
		}
		
		logger.info("Retrieving Tool ID for site: " + site.siteId  + " - Tool registration: " + TOOL_REGISTRATION +" ");
		
		String toolId = siteRepository.findToolId(TOOL_REGISTRATION, site.siteId);
		
		if (toolId != null) {
			logger.info("Site Tool Id: " + toolId);
			
			try {
				String output = WsGradebookService.exportGradebook(user.sessionId, site.siteId, toolId, applicationConfig.getHost());
				logger.info("Gradebook Logic Output: " + output);
				
				Utilities.copyFileFrom(output, directory + GB_ARCHIVE);
				
			} catch (InvalidLoginException e) {
				e.printStackTrace();
			} catch (GradebookServiceException e) {
				e.printStackTrace();
			}
		}
		
	}

}
