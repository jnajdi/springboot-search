package edu.vt.tlos.service;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vt.tlos.service.exception.InvalidLoginException;


public class WsLoginService {
	
	private static final Logger logger = LoggerFactory.getLogger("WsLoginService");
	private static final String SERVICE_PATH = "/webservices-vt/services/LoginService";
	private static final String NAMESPACE_URI_LOGIN = "http://login.lt.vt.edu";
	private static final String LOGIN_OPERATION = "login";
	
	
	public static String login(String envr, String wsUsername, String wsPassword) throws InvalidLoginException{
		try {
			String wsUrl = envr + SERVICE_PATH;
			
			logger.info("Login user: " + wsUsername);
			
			RPCServiceClient serviceClient = new RPCServiceClient();
		    Options options = serviceClient.getOptions();
		    EndpointReference targetEPR = new EndpointReference(wsUrl);
		    options.setTo(targetEPR);
			
		    QName opAddNewUser = new QName(NAMESPACE_URI_LOGIN, LOGIN_OPERATION);
		    
		
		    Object[] opAddNewUserArgs = new Object[] {wsUsername, wsPassword};
		    Class[] returnTypes = new Class[] { String.class };
		
		    Object[] response = serviceClient.invokeBlocking(opAddNewUser, opAddNewUserArgs, returnTypes);
		    
		    String sessionId = (String) response[0];
		    
		    if (sessionId == null) {
		    	throw new InvalidLoginException("unable to login - username: " + wsUsername);
		    }
		    return sessionId;
		    
		} catch (Exception e) {
			throw new InvalidLoginException("unable to login - username: " + wsUsername);
		}
	}
}



