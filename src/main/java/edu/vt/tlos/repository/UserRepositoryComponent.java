package edu.vt.tlos.repository;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryComponent {
	private static UserRepository userRepositoryStatic;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostConstruct     
	public void initStaticDao () {
		userRepositoryStatic = this.userRepository;
	}
	
	public static UserRepository getUserRepository() {
		return userRepositoryStatic;
	}
	
	
}
