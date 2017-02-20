package edu.vt.tlos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import edu.vt.tlos.service.exception.GradebookServiceException;
import edu.vt.tlos.service.exception.InvalidLoginException;


public class WsGradebookService {
	
	private static final Logger logger = LoggerFactory.getLogger("WsGradebookService");
	
	
	public static String exportGradebook(String sessionId, String siteId, String toolId, String host) throws InvalidLoginException, GradebookServiceException{
		try {
		    if (sessionId == null) {
		    	throw new InvalidLoginException("unable to login - session: " + sessionId);
		    }
		    
		    //https://preprod.scholar.vt.edu/portal/tool/738d22cd-de8e-4e09-ad22-c102b228736c/gradebook/rest/exportGradebook/b8f508d6-1bf3-467c-b519-03213224a8f1?sakai.session=da2b75b2-941b-4f19-a55f-903db80e5dd8
		    String url = "https://" + host + "/portal/tool/" + toolId + "/gradebook/rest/exportGradebook/" + siteId + "?sakai.session=" + sessionId;
		    
		    RestTemplate restTemplate = new RestTemplate();
		    String output = (String) restTemplate.getForObject(url, String.class);
		    
		    logger.info("Gradebook Webservice output: " + output);
		    
		    return output;
		    
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new GradebookServiceException("Error exporting Gradebook: " + siteId);
		}
	}
}



