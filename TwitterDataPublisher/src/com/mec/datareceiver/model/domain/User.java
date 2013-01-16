package com.mec.datareceiver.model.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
	private int userId;
	private String screenName;
	private Timestamp createDate;
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int id) {
		userId = id;
	}
	
	public String getScreenName() {
		return screenName;
	}
	
	public void setScreenName(String name) {
		screenName = name;
	}
	
	public Timestamp getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Timestamp date) {
		createDate = date;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean res = false;
		if ((obj == null) || !(obj instanceof User)) return false;
		User aUser = (User)obj;
		if (screenName.equals(aUser.getScreenName())) {
			res = true;
		}
		return res;
	}
	
	@Override
	public int hashCode() {
		return screenName.hashCode();
	}

}
