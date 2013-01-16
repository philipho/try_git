package com.mec.datareceiver.model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mec.datareceiver.model.domain.Activity;

public class JdbcActivityDao extends NamedParameterJdbcDaoSupport implements ActivityDao {
	
    public List<Activity> getActivityList() {
        String sql = "SELECT Activity_id, sender_id, receiver_id, txt_length, create_date FROM activity";
        List<Activity> Activitys = this.getJdbcTemplate().query(sql, new ActivityMapper());
        return Activitys;
    }

    public void insert(Activity Activity) {
        String sql = "INSERT INTO Activity (sender_id, receiver_id, txt_length, create_date) VALUES " +
        		"(:senderId, :receiverId, :txtLength, :createDate)";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(Activity);
        this.getNamedParameterJdbcTemplate().update(sql, parameterSource);
    }

    public void insertBatch(List<Activity> Activitys) {
        String sql = "INSERT INTO Activity (sender_id, receiver_id, txt_length, create_date) VALUES " +
        		"(:senderId, :receiverId, :txtLength, :createDate)";
        SqlParameterSource[]  batch = SqlParameterSourceUtils.createBatch(Activitys.toArray());
		getNamedParameterJdbcTemplate().batchUpdate (sql, batch);
    }

    public List<Activity> findByActivitySenderScreenName(String screenName) {
        String sql = "SELECT * FROM activity WHERE sender_id in (SELECT user_id FROM User WHERE screen_name = ?) ";
        List<Activity> activities = this.getJdbcTemplate().queryForList(sql, Activity.class, screenName);
        return activities;
    }

    public void update(Activity Activity) {}

    public void delete(Activity Activity) {}
    
	@Override
	public void deleteActivitiesOfSender(int userId) {
		String sql = "DELETE FROM activity WHERE senderId = ?";
		this.getJdbcTemplate().update(sql, new Object[] { userId });
	}
	
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM activity";
        List<Map<String, Object>> Activitys = this.getJdbcTemplate().queryForList(sql, new ActivityMapper());
        return Activitys;
    }

    public int getActivitiesCount() {
        String sql = "SELECT COUNT(*) FROM activity";
        int count = this.getJdbcTemplate().queryForInt(sql);
        return count;
    }
    
    public void deleteAll() {
    	String sql = "DELETE FROM activity";
    	this.getJdbcTemplate().execute(sql);
    }
    
    
    private static class ActivityMapper implements ParameterizedRowMapper<Activity> {
    	public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        	Activity Activity = new Activity();
            Activity.setActivityId(rs.getInt("activity_id"));
            Activity.setSenderId(rs.getInt("sender_id"));
            Activity.setReceiverId(rs.getInt("receiver_id"));
            Activity.setTxtLength(rs.getInt("txt_length"));
            Activity.setCreateDate(rs.getTimestamp("create_date"));
            return Activity;
        }
    }


 
}
