package com.mec.datareceiver.service;

import java.io.Serializable;
import java.util.List;

import com.mec.datareceiver.model.domain.Activity;

public interface ActivityManager extends Serializable{
	public void insert(Activity activity);
	public void deleteActivitiesOfSender(int userId);
    public List<Activity> getActivityListFor(String screenName);
//    public List<Activity> getActivities(String screenName, int numOfDays);
//    public List<Activity> getActivities(String screenName, String fromDate, String toDate);
    public List<Activity> getAllActivities();
    public int getActivitiesCount();
    public void deleteAllActivities();
}