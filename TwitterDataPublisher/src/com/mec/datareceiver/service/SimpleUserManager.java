package com.mec.datareceiver.service;

import java.util.List;

import com.mec.datareceiver.model.dao.UserDao;
import com.mec.datareceiver.model.domain.User;

public class SimpleUserManager implements UserManager {

    private UserDao userDao;
    
	public  void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
    public  List<User> getUser(String screenName) {
		return userDao.findByUserScreenName(screenName);
	}
	
	@Override
    public  List<User> getAllUsers() {
		return userDao.getUserList();
	}
	
	@Override
    public  void addUser(User user) {
		userDao.insert(user);
	}
	
	@Override
	public  void deleteUser(User user) {
		userDao.delete(user);
	}
	
	@Override
    public  int getNumberOfUsers() {
		return userDao.getNumOfUsers();
	}
    
	@Override
	public  void deleteAllUsers() {
		userDao.deleteAll();
	}

	// Added a comment here 20190119
}
