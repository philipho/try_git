package com.mec.datareceiver;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.mec.twitterdatapublisher.TweetData;

@Component
public class  TwitterDataListener implements MessageListener
{
    public void onMessage(final Message message)
    {
        if ( message instanceof ObjectMessage )
        {
        	ObjectMessage objMsg = (ObjectMessage)message;
        	try {
	        	TweetData data = (TweetData)(objMsg.getObject());
	        	System.out.println("=======================> TwitterDataListener : [" + data + "]");
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }
    
    public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("appContextTest.xml");

    	while (true) {
    		Thread.yield();
    	}
    }
    
}