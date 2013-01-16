package com.mec.datareceiver.model.dao;

import java.util.List;
import java.util.Map;

import com.mec.datareceiver.model.domain.User;

public interface UserDao {
	public List<User> getUserList();
    public void insert(User user);
    public void update(User user);
    public void delete(User user);
    public List<User> findByUserScreenName(String screenName);
    public void insertBatch(List<User> users);
    public List<Map<String, Object>> findAll();
    public int getNumOfUsers();
    public void deleteAll();
}
