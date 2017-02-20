package edu.vt.tlos;

import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import edu.vt.tlos.domain.Site;
import edu.vt.tlos.domain.User;
import edu.vt.tlos.repository.SiteRepository;
import edu.vt.tlos.repository.UserRepository;
import edu.vt.tlos.service.GradebookExportLogicImpl;
import edu.vt.tlos.service.ResourceExportLogicImpl;
import edu.vt.tlos.service.RosterExportLogicImpl;
import edu.vt.tlos.service.Utilities;
import edu.vt.tlos.service.WsGradebookService;
import edu.vt.tlos.service.WsLoginService;
import edu.vt.tlos.service.ZipUtils;
import edu.vt.tlos.service.exception.SiteException;
import edu.vt.tlos.service.exception.UserException;

@SpringBootApplication
public class Application implements CommandLineRunner{

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ApplicationConfig applicationConfig;
	
	@Autowired
	private ResourceExportLogicImpl resourceExportLogic;
	
	@Autowired
	private RosterExportLogicImpl rosterExportLogic;
	
	@Autowired
	private GradebookExportLogicImpl gradebookExportLogic;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SiteRepository siteRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	private static String ARCHIVE_EXT = "_archive.zip";
	
	@Override
	public void run(String... args) throws Exception{
		validateProperties();
		
		String sessionId = WsLoginService.login(applicationConfig.getEnvrDomain(), applicationConfig.getWsUsername(), applicationConfig.getWsPassword());
		logger.info("Session Id: " + sessionId);
		
		Site site = getSite(args[0]);
		User user = getUser(sessionId, args[1], site);
		
		String tempDirectory = applicationConfig.getArchiveTempDirectory() + File.separator + site.siteId + File.separator + user.pid + File.separator;
		
		logger.info("Temp Directory: " + tempDirectory);
		Utilities.createDirectories(tempDirectory);
		
		if (args.length == 2){
			try {
				logger.info("EXPORT RESOURCES");
				resourceExportLogic.setSite(site);
				resourceExportLogic.setUser(user);
				resourceExportLogic.export(tempDirectory);
				logger.info("#########################################################");
				
				logger.info("EXPORT ROSTERS");
				rosterExportLogic.setSite(site);
				rosterExportLogic.setUser(user);
				rosterExportLogic.export(tempDirectory);
				logger.info("#########################################################");

				logger.info("EXPORT GRADEBOOK");
				gradebookExportLogic.setSite(site);
				gradebookExportLogic.setUser(user);
				gradebookExportLogic.export(tempDirectory);
				
				String archiveZipFilename = site.title + "_" + user.pid + ARCHIVE_EXT;
				archiveZipFilename = archiveZipFilename.trim().replaceAll(" +", "_").replaceAll("[^a-zA-Z0-9.]", "_").replaceAll("_+", "_").toLowerCase();
				String archiveDirectory = applicationConfig.getArchiveZipDirectory() + File.separator + site.siteId + File.separator + user.pid + File.separator;
				ZipUtils.createZip(tempDirectory, archiveDirectory, archiveZipFilename);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				// Delete Temp Directory
				logger.info("Delete Temp Directory: " + tempDirectory);
				ZipUtils.deleteTemp(tempDirectory);
			}
			
		} else {
			displayHelp();
		}
		
	}
	
	public static void displayHelp() {
		logger.error("Scolar Migration Tool");
		
		logger.error("USAGE: java ./scholar-magration-tool.jar <SITE-ID> <USER-ID>");
	}
	
	public Site getSite(String siteId) throws Exception {
		Site site = siteRepository.findSiteById(siteId);
		
		if (site != null) {
			logger.info("Site: " + site.title);
			return site;
		} else {
			throw new SiteException("Unable to find site: " + site);
		}
	}

	public User getUser(String sessionId, String userId, Site site) throws Exception {
		if (site == null) {
			throw new SiteException("Invalid site: " + site);
		}
		
		if (userId != null) {
			User user = userRepository.findUser(userId);

			if (user != null) {
			
				if (userRepository.isAdmin(user.userId)) {
					logger.info("User " + user.pid + "has Admin role");
				}else if (userRepository.hasPermission(user.userId, site.siteId)) {
					logger.info("User " + user.pid + " has Organizer or Instructor role.");
				} else {
					throw new UserException("User " + user.pid + " is not allowed to access this site.");
				}
				user.sessionId = sessionId;
				
				return user;
			} else {
				throw new UserException("Unable to find user: " + userId);
			}
		} else {
			throw new UserException("Unable to find user: " + userId);
		}
		
	}
	
	private void validateProperties() {
		logger.info("Temp Directory: " + applicationConfig.getArchiveTempDirectory());
		
		boolean  tempDirectory = Utilities.createDirectories(applicationConfig.getArchiveTempDirectory());
		
		if (!tempDirectory) {
			logger.error("Unable to create archive directory: " + tempDirectory);
			System.exit(-1);
		}
		
		logger.info("Archive Directory: " + applicationConfig.getArchiveZipDirectory());
		
		boolean archiveDirectory = Utilities.createDirectories(applicationConfig.getArchiveZipDirectory());
		
		if (!archiveDirectory) {
			logger.error("Unable to create termp directory: " + archiveDirectory);
			System.exit(-1);
		}
		
		if (applicationConfig.getHost() != null)
			logger.info("Host: " + applicationConfig.getHost());
		else {
			logger.info("Unable to load the host property from application-default.properties");
			System.exit(-1);
		}
	}
	
	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(Locale.US);
	    return slr;
	}
}

