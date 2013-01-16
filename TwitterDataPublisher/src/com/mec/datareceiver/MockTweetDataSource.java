package com.mec.datareceiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageProducer;

import org.apache.log4j.Logger;
import org.springframework.jms.JmsException;

import com.mec.twitterdatapublisher.TweetData;
import com.mockrunner.mock.jms.MockObjectMessage;

public class MockTweetDataSource {
	final private static Logger log = Logger.getLogger(MockTweetDataSource.class);
	private MessageProducer msgProducer;
	private int numOfMessagesToPublish;
	
	public void setMessageProducer(MessageProducer msgProducer) {
		this.msgProducer = msgProducer;
	}
	
	public int getNumOfMessagesInQueue() {
		return numOfMessagesToPublish;
	}

	public void setUpNumberOfDataItemsToPublish(int numOfMessagesToSend) {
		this.numOfMessagesToPublish = numOfMessagesToSend;
	}

	public void publish() {
		for (int i = 0; i < numOfMessagesToPublish; i++) {
			TweetData msg = new TweetData();
			msg.setCreationDateTime(new Date());
			msg.setSender("sender-" + i);
			List<String> receivers = new ArrayList<String>();
			receivers.add("receiver-" + (10+i));
			receivers.add("receiver-" + (20+i));
			receivers.add("receiver-" + (30+i));
			msg.setReceivers(receivers);
			msg.setIsValid(true);
			try {
				msgProducer.send(new MockObjectMessage(msg));
				log.debug("Send message [" + i + "]");
			}
			catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}


}
