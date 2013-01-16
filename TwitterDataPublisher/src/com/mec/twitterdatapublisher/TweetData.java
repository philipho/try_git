package com.mec.twitterdatapublisher;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TweetData implements Serializable {
	private boolean isValid = false;
	private String sender;
	private List<String> receivers;
	private Date creationDateTime;
	private int txtLength;
	
	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}
	
	public List<String> getReceivers() {
		return receivers;
	}
		
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}
	
	public Date getCreationDateTime() {
		return creationDateTime;
	}
	
	public void setTxtLength(int len) {
		this.txtLength = len;
	}
	
	public int getTxtLength() {
		return txtLength;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("sender [").append(sender).append("], createdAt [").append(creationDateTime).append("], receivers [");
		for (String receiver : receivers) {
			sb.append(receiver).append(", ");
		}
		sb.append("], text length [").append(txtLength).append("]\n");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj == null || !(obj instanceof TweetData)) {
			isEqual = false;
		}
		else {
			TweetData data = (TweetData)obj;
			if (sender.equals(data.sender) &&
					creationDateTime.equals(data.creationDateTime) &&
					compareTwoArrays(receivers, data.receivers))
			{
				isEqual = true;
			}
		}
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int res = sender.hashCode() + 17 * creationDateTime.hashCode();
		for (String s : receivers) {
			res ^= s.hashCode(); 
		}
		return res;
	}

	private boolean compareTwoArrays(List<String> l1, List<String> l2) {
		if (l1.size() != l2.size()) return false;
		for (String s : l1) {
			if (!l2.contains(s)) return false;
		}
		return true;
	}
}
