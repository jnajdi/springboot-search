package edu.vt.tlos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.vt.tlos.ApplicationConfig;
import edu.vt.tlos.domain.Site;
import edu.vt.tlos.domain.User;
import edu.vt.tlos.repository.ContentResourceRepository;
import edu.vt.tlos.repository.SiteRepository;
import edu.vt.tlos.repository.UserRepository;
import edu.vt.tlos.service.exception.SiteException;
import edu.vt.tlos.service.exception.UserException;

@Service 
public class ExportLogicImpl {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SiteRepository siteRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	private Site site; 
	
	
	
	
}
