package com.mec.datareceiver.model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mec.datareceiver.model.domain.User;

public class JdbcUserDao extends NamedParameterJdbcDaoSupport implements UserDao {
//	private JdbcTemplate jdbcTemplate;
//	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
//	public void setDataSource(DataSource dataSource) {
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate (dataSource);
//	}
	
    public  List<User> getUserList() {
        String sql = "SELECT user_id, screen_name, create_date FROM user";
        List<User> Users = this.getJdbcTemplate().query(sql, new UserMapper());
        return Users;
    }

    public  void insert(User User) {
        String sql = "INSERT INTO User (screen_name) VALUES (:screenName)";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(User);
        this.getNamedParameterJdbcTemplate().update(sql, parameterSource);
    }

    public  void insertBatch(List<User> Users) {
        String sql = "INSERT INTO User (screen_name) VALUES (:screenName)";
        SqlParameterSource[]  batch = SqlParameterSourceUtils.createBatch(Users.toArray());
		this.getNamedParameterJdbcTemplate().batchUpdate (sql, batch);
    }

    /**
     * @param screenName is the screen name used in Twitter
     * @return a list of users with the screenName. Theoretically, the list should have only one user. Or empty if no matching screen name.
     */
    public  List<User> findByUserScreenName(String screenName) {
        String sql = "SELECT * FROM user WHERE screen_name = ?";
        // use query instead of queryForObject because queryForYYY expects one row is returned and used
        // the mapper. If none is returned, an EmptyResultDataAccessException will be thrown.
        List<User> userList = this.getJdbcTemplate().query(sql, new Object[] { screenName }, new UserMapper());
        return userList;
    }

    public void update(User aUser) {}

    public  void delete(User aUser) {
        String sql = "DELETE FROM user WHERE screen_name = ?";
        this.getJdbcTemplate().update(sql, new Object[] {aUser.getScreenName()});
    }

    public  List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM user";
        List<Map<String, Object>> Users = this.getJdbcTemplate().queryForList(sql, new UserMapper());
        return Users;
    }

    public  int getNumOfUsers() {
        String sql = "SELECT COUNT(*) FROM user";
        int count = this.getJdbcTemplate().queryForInt(sql);
        return count;
    }
    
    public  void deleteAll() {
    	String sql = "DELETE FROM user";
    	this.getJdbcTemplate().execute(sql);
    }
    
    private static class UserMapper implements ParameterizedRowMapper<User> {
    	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        	User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setScreenName(rs.getString("screen_name"));
            user.setCreateDate(rs.getTimestamp("create_date"));
            return user;
        }
    }
 
}
