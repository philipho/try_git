package com.mec.datareceiver;


import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import com.mec.datareceiver.model.domain.Activity;
import com.mec.datareceiver.model.domain.User;
import com.mec.datareceiver.service.ActivityManager;
import com.mec.datareceiver.service.UserManager;
import com.mec.twitterdatapublisher.TweetData;

final public class TweetDataProcessor implements Runnable {
	private UserManager userManager;
	private ActivityManager activityManager;
	private TweetData data;
	
	public TweetDataProcessor(UserManager userManager, ActivityManager activityManager, TweetData data) {
		this.userManager = userManager;
		this.activityManager = activityManager;
		this.data = data;
	}
	
	public void setUserManager(UserManager um) {
		this.userManager = um;
	}
	
	public UserManager getUserManager() {
		return userManager;
	}
	
	public void setActivityManager(ActivityManager am) {
		this.activityManager = am;
	}
	
	public ActivityManager getActivityManager() {
		return activityManager;
	}
	
	public void run() {
		process();
	}
	
	public void process() {
		String sender = data.getSender();
		createUserInDBIfNotExistInDB(sender);

		List<String> receivers = data.getReceivers();
		if (receivers.size() == 0) {
			createNoReceiverActivityInDB(data);
		}
		else {
			for (String receiver : receivers) {
				createUserInDBIfNotExistInDB(receiver);
				createHasReceiverActivityInDB(receiver, data);
			}
		}
		
	}

	private void createActivityInDB(int senderId, int receiverId, TweetData data) {
		Activity activity = new Activity();
		activity.setSenderId(senderId);
		activity.setReceiverId(receiverId);
		activity.setTxtLength(data.getTxtLength());
		long t = (long)((new Date()).getTime());
		activity.setCreateDate(new Timestamp(t));
		activityManager.insert(activity);		
	}
	
	private int getUserIdInDB(String userScreenName) {
		return (userManager.getUser(userScreenName)).get(0).getUserId();
	}
	
	private void createHasReceiverActivityInDB(String receiver, TweetData data) {
		int senderId = getUserIdInDB(data.getSender());
		int receiverId = getUserIdInDB(receiver);
		createActivityInDB(senderId, receiverId, data);
	}

	private void createNoReceiverActivityInDB(TweetData data) {
		int senderId = getUserIdInDB(data.getSender());
		int receiverId = 0;
		createActivityInDB(senderId, receiverId, data);
	}

	private void createUserInDBIfNotExistInDB(String user) {
		if (!userExistInDB(user)) {
			createUserInDB(user);
		}
	}

	private void createUserInDB(String user) {
		User aUser = new User();
		aUser.setScreenName(user);
		long t = (long)((new Date()).getTime());
		aUser.setCreateDate(new Timestamp(t));
		userManager.addUser(aUser);
	}

	private boolean userExistInDB(String user) {
		List<User> userList = userManager.getUser(user);
		int n = userList.size();
		if (n == 0) {
			return false;
		}
		else if (n == 1) {
			return true;
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	

}
