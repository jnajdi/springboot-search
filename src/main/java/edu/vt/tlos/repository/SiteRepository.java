package edu.vt.tlos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.vt.tlos.domain.GlossaryTerm;
import edu.vt.tlos.domain.Site;

@Repository
public class SiteRepository {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional(readOnly=true)
	public List<Site> findAllSitesWithMatrix() {
		
		String sql = "select unique s.SITE_ID, s.TITLE, s.createdon, s.modifiedon from OSP_SCAFFOLDING m,  SAKAI_SITE s where  m.WORKSITEID = s.SITE_ID  and m.PUBLISHED = 1 order by site_id asc";
		logger.debug("--->" + sql);
		
		return jdbcTemplate.query(sql, new SiteRowMapper());
	}
	
	
	@Transactional(readOnly=true)
	public Site findSiteById(String siteId) {
		
		logger.info("Retrieving site information: " + siteId);
		
		String sql = "select * FROM sakai_site where site_id='" + siteId + "'";
		logger.debug("--->" + sql);
		
		List<Site> emplist = jdbcTemplate.query(sql, new SiteRowMapper());
		
		if (emplist == null) {
			logger.error("Unable to find site: " + siteId);
			return null;
		}
		
		if (emplist.size() > 0) {
			logger.debug("Site: " + emplist.get(0).title);;
		}
		
		return emplist.get(0);
	}
	
	

	@Transactional(readOnly=true)
	public List<Site> findUserSites(String userId) {
		String sql = "select site.SITE_ID, site.title, realm_id, site.createdon,  site.modifiedon from sakai_realm sr, sakai_realm_rl_gr srrg, sakai_user su, sakai_site site  where "
				+ "srrg.user_id='" + userId + "' " 
				+ "and sr.realm_key = srrg.realm_key "
				+ "and srrg.user_id = su.user_id "
				+ "and realm_id = '/site/' || site.site_id "
				+ "and site.SITE_ID in (select WORKSITEID from osp_scaffolding where published = '1') order by site.MODIFIEDON desc";
			
			logger.debug("--->" + sql);
			
			List<Site> sites = jdbcTemplate.query(sql, new SiteRowMapper());
			
			if (sites == null) {
				logger.debug("sites: []");
				return new ArrayList<Site>();
			}
			
			return sites;
	}
	
	@Transactional(readOnly=true)
	public String findToolId(String registration, String siteId) {
		
		//select tool_id from SAKAI_SITE_TOOL where registration = 'sakai.gradebook.gwt.rpc' and site_id = 'b8f508d6-1bf3-467c-b519-03213224a8f1'
		String sql = "select tool_id from SAKAI_SITE_TOOL where registration = '" + registration + "' and site_id = '" + siteId + "'";
		logger.debug("--->" + sql);
		
		List<String> rows = jdbcTemplate.query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rset, int rowNum) throws SQLException {
				return rset.getString("tool_id");
			}
		});
		
		if (rows != null && rows.size() > 0) {
			return rows.get(0);
		}
		return null;
	}
	
}

class SiteRowMapper implements RowMapper<Site> {

	@Override
	public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
		Site site = new Site();
    	site.siteId = rs.getString("site_id");
		site.title = rs.getString("title");
		site.dateCreated = rs.getString("createdon");
		site.modifiedOn = rs.getString("modifiedon");
        
		return site;
	}
	
}


class GlossaryRowMapper implements RowMapper<GlossaryTerm> {

	@Override
	public GlossaryTerm mapRow(ResultSet rset, int rowNum) throws SQLException {
		
		GlossaryTerm glossary = new GlossaryTerm();
		glossary.id = rset.getString("id");
		glossary.term = rset.getString("term");
		glossary.description = rset.getString("description");
		glossary.longDescription = rset.getString("long_description");
		
		return glossary;
	}
	
}


