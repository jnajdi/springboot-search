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

import edu.vt.tlos.domain.User;

@Repository
public class UserRepository {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String PERMISSION_ORGANIZER_KEY="84";
	private static String PERMISSION_INSTRUCTOR_KEY="21";
	
	
	
	@Transactional(readOnly=true)
	public List<User> findSiteUsers(String siteId) {
		
		String sql = "select su.user_id, srrg.role_key, last_name, first_name, email from sakai_realm sr, sakai_realm_rl_gr srrg, sakai_user su"
				+ " where realm_id = '/site/" + siteId + "' "
				+ "and sr.realm_key = srrg.realm_key and srrg.user_id = su.user_id";
		
		logger.debug("--->" + sql);
		
		List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
		
		if (users == null) {
			logger.debug("Users: []");
			return new ArrayList<User>();
		}
		
		return users;
	}
	
	public List<User> findRosters(String siteId) {
		logger.debug("Find rosters: ");
		
		String sql = "select su.user_id, srrg.role_key, last_name, first_name, usermap.eid, email, srr.role_name from sakai_realm sr, sakai_realm_rl_gr srrg, sakai_user su, sakai_realm_role srr, sakai_user_id_map usermap where sr.realm_key = srrg.realm_key and srrg.user_id = su.user_id and srr.role_key = srrg.role_key and usermap.user_id = su.user_id and realm_id ='/site/" + siteId + "' order by last_name, first_name";
		
		logger.info(sql);
		
		List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
		
		return users;
	}
	
	
	@Transactional(readOnly=true)
	public User findUser(String user) {
		
		logger.info("Retrieving user information: " + user);
		
		String sql = "select u.user_id, email, first_name, last_name, eid from sakai_user u, SAKAI_USER_ID_MAP m where m.USER_ID = u.USER_ID and (u.user_id = '" + user + "' or m.eid = '" + user +"')";
		
		logger.debug("--->" + sql);
		
		List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
		
		if (users == null || users.size() <= 0) {
			logger.debug("Users: []");
			return null;
		}
	
		return users.get(0);
	}
	
	public boolean hasPermission(String userId, String siteId) {
		
		String sql = "select sr.realm_key from sakai_user su, sakai_realm sr, sakai_realm_rl_gr srrg,  sakai_realm_role srr where sr.realm_key = srrg.realm_key and srrg.user_id = su.user_id and srr.role_key = srrg.role_key and realm_id ='/site/" + siteId + "'  and su.user_id = '" + userId + "' and srr.role_key in ('"+ PERMISSION_ORGANIZER_KEY+"', '"+PERMISSION_INSTRUCTOR_KEY +"')";
		logger.debug("--->" + sql);
		
		List<String> rows = jdbcTemplate.query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rset, int rowNum) throws SQLException {
				return rset.getString("realm_key");
			}
		});
		
		if (rows != null && rows.size() > 0) {
			return true;
		}
		
		return false; 
	}
	
	public boolean isAdmin(String userId) {
		String sql = "select realm_key from sakai_realm_rl_gr g, SAKAI_USER_ID_MAP m where role_key in (6) and g.USER_ID = m.user_id and m.user_id='"+ userId + "'";
		logger.debug("--->" + sql);
		
		List<String> rows = jdbcTemplate.query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rset, int rowNum) throws SQLException {
				return rset.getString("realm_key");
			}
		});
			
		if (rows != null && rows.size() >0 ) {
			return true;
			
		}
		return false;
	}
}

class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rset, int rowNum) throws SQLException {
		
		User user = new User(rset.getString("user_id"));
		String eid = null;
		
		try {
			user.roleId = rset.getInt("role_key");	
			user.roleName = rset.getString("role_name");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		user.email = rset.getString("email");
		user.lastName = rset.getString("last_name");
		user.firstName = rset.getString("first_name");
		
		if (user.lastName == null) {
			user.lastName = user.email;
		}
		
		if (user.firstName == null) {
			user.firstName = user.email;
		}
		
		eid = rset.getString("eid");
				
		if (eid != null) 
			user.pid = eid;
		else {
			if (user.email != null) {
				int idx = user.email.indexOf("@");
				if (idx != -1) {
					user.pid = user.email.substring(0, idx);
				}
			}
		}
		return user;
	}
	
}




