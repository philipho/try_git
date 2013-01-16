package com.mec.datareceiver.model.dao;

import java.util.List;
import java.util.Map;

import com.mec.datareceiver.model.domain.Activity;

public interface ActivityDao {
	public List<Activity> getActivityList();
    public void insert(Activity activity);
    public void update(Activity activity);
    public void deleteActivitiesOfSender(int userId);
    public List<Activity> findByActivitySenderScreenName(String screenName);
    public void insertBatch(List<Activity> activities);
    public List<Map<String, Object>> findAll();
    public int getActivitiesCount();
    public void deleteAll();
}
