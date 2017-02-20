package edu.vt.tlos.service;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.vt.tlos.ApplicationConfig;
import edu.vt.tlos.domain.User;
import edu.vt.tlos.repository.UserRepository;
import edu.vt.tlos.service.exception.SiteException;
import edu.vt.tlos.service.exception.UserException;

@Service
public class RosterExportLogicImpl extends ExportLogic{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ApplicationConfig applicationConfig;
	
	private static final String ROSTERS_CSV = "rosters.csv";
	
	@Override
	public void export(String directory) throws UserException, SiteException {
		
		if (user == null) {
			throw new UserException("Invalid user: " + user);
		}
		
		if (site == null) {
			throw new SiteException("Invalid site: " + site);
		}
		
		logger.info("Retrieving rosters for site: " + site.siteId + " ("+ user.pid +") ");
		
		List<User> users = userRepository.findRosters(site.siteId);
		
		if (users != null) {
			Collections.sort(users);

			StringBuffer rosters = new StringBuffer("");
			rosters.append("Name,User ID, Email Address, Role\n");
			for (User roster: users) {
				rosters.append(roster.toString());
				rosters.append("\n");
			}
			String rostersFilePath = directory + ROSTERS_CSV; 
			Utilities.createFile(rostersFilePath, site.siteId, rosters.toString());

		}
	}
	

}
