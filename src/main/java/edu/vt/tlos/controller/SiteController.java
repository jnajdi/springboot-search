package edu.vt.tlos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.vt.tlos.domain.Site;
import edu.vt.tlos.domain.User;
import edu.vt.tlos.repository.SiteRepository;
import edu.vt.tlos.repository.UserRepository;

@Controller
public class SiteController {

	@Autowired
	private SiteRepository siteRepository;
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
    public String getHello() {
		
		String testuser = "46e4d7f8-bbaf-428c-80aa-3e4173d4c87c";
		
		return "index";
		
	}
	
	@RequestMapping("/sites")
    public String getSites(Model model) {
		
		String testuser = "46e4d7f8-bbaf-428c-80aa-3e4173d4c87c";
		
		List<Site> sites = siteRepository.findUserSites(testuser);
		
		model.addAttribute("allSites", sites);
		
		return "index";
		
	}

	@RequestMapping("/site/{id}")
    public String getSite(@PathVariable String id, Model model) {
		System.out.println("/sites/" + id);
		
		List<Site> sites = new ArrayList<Site>();
//		List<Scaffolding> scaffoldings = scaffoldingRepository.findScaffoldings(id);
		
		List<User> users = userRepository.findSiteUsers(id);
		
		model.addAttribute("allUsers", users);
//		model.addAttribute("allScaffoldings", scaffoldings);
		
		return "scaffoldings";
		
	}

}
