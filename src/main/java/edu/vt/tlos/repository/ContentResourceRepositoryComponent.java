package edu.vt.tlos.repository;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentResourceRepositoryComponent {
	private static ContentResourceRepository contentResourceRepositoryStatic;
	
	@Autowired
	private ContentResourceRepository contentResourceRepository;
	
	@PostConstruct     
	public void initStaticDao () {
		contentResourceRepositoryStatic = this.contentResourceRepository;
	}
	
	public static ContentResourceRepository getContentResourceRepository() {
		return contentResourceRepositoryStatic;
	}
	
	
}
