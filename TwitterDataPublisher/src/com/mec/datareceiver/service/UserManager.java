package com.mec.datareceiver.service;

import java.io.Serializable;
import java.util.List;

import com.mec.datareceiver.model.domain.User;

public interface UserManager extends Serializable{
    public List<User> getUser(String screenName);
    public List<User> getAllUsers();
    public void addUser(User user);
    public int getNumberOfUsers();
	public void deleteUser(User user);
	public void deleteAllUsers();
}