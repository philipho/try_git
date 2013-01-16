package com.mec.datareceiver.service;

import java.util.List;

import com.mec.datareceiver.model.dao.ActivityDao;
import com.mec.datareceiver.model.domain.Activity;

public class SimpleActivityManager implements ActivityManager {

    private ActivityDao activityDao;
    
	public  void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}
	
	@Override
	public  void insert(Activity activity) {
		activityDao.insert(activity);
	}

	@Override
    public  List<Activity> getAllActivities() {
    	return activityDao.getActivityList();
    }
    
	@Override
    public  List<Activity> getActivityListFor(String screenName) {
    	return activityDao.findByActivitySenderScreenName(screenName);
    }
    
	@Override
    public  int getActivitiesCount() {
    	return activityDao.getActivitiesCount();
    }

	@Override
	public  void deleteAllActivities() {
		activityDao.deleteAll();
	}

	@Override
	public  void deleteActivitiesOfSender(int userId) {
		activityDao.deleteActivitiesOfSender(userId);
	}
    
}
