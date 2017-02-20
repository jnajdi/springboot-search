package edu.vt.tlos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vt.tlos.domain.Site;
import edu.vt.tlos.domain.User;
import edu.vt.tlos.service.exception.SiteException;
import edu.vt.tlos.service.exception.UserException;

public abstract class ExportLogic  {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected User user; 
	
	protected Site site; 

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public abstract void export(String directory) throws UserException, SiteException;
}
