package com.mec.datareceiver.model.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Activity implements Serializable {
	private int activityId;
	private int senderId;
	private int receiverId;
	private int txtLength;
	private Timestamp createDate;
	
	public int getActivityId() {
		return activityId;
	}
	
	public void setActivityId(int id) {
		activityId = id;
	}
	
	public int getSenderId() {
		return senderId;
	}
	
	public void setSenderId(int id) {
		senderId = id;
	}
	
	public int getReceiverId() {
		return receiverId;
	}
	
	public void setReceiverId(int id) {
		receiverId = id;
	}
	
	public int getTxtLength() {
		return txtLength;
	}
	
	public void setTxtLength(int len) {
		txtLength = len;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Timestamp date) {
		createDate = date;
	}

}
