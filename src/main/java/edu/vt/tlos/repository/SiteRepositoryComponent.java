package edu.vt.tlos.repository;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteRepositoryComponent {
	private static SiteRepository siteRepositoryStatic;
	
	@Autowired
	private SiteRepository siteRepository;
	
	@PostConstruct     
	public void initStaticDao () {
		siteRepositoryStatic = this.siteRepository;
	}
	
	public static SiteRepository getSiteRepository() {
		return siteRepositoryStatic;
	}
	
	
}
