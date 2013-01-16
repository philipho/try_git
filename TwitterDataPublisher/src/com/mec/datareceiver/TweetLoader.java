package com.mec.datareceiver;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.mec.datareceiver.service.ActivityManager;
import com.mec.datareceiver.service.UserManager;
import com.mec.twitterdatapublisher.TweetData;
import com.mec.twitterdatapublisher.TwitterDataPublisher;

/**
 * TweetLoader uses the configured message listener container to retrieve messages
 * from the message broker. It knows nothing about JMS messaging. It only knows how 
 * to process the received messages.
 * @author pho
 */
final public class TweetLoader implements DataLoader {
	private DefaultMessageListenerContainer msgListenerContainer;
	private TwitterDataListener twitterDataListener;
	private ExecutorService executorService;
	private UserManager userManager;
	private ActivityManager activityManager;
	
	public TweetLoader(DefaultMessageListenerContainer container, UserManager userManager, ActivityManager activityManager) 
	{
		this.msgListenerContainer = container;
		this.userManager = userManager;
		this.activityManager = activityManager;
		this.executorService = Executors.newFixedThreadPool(3);
	}
	

	
	@Override
	public void connect() {
		// Connect to message broker and register message listener
		twitterDataListener = new TwitterDataListener(executorService, userManager, activityManager);
		msgListenerContainer.setMessageListener(twitterDataListener);
		msgListenerContainer.initialize();
		msgListenerContainer.start();
	}
	
	@Override
	public void disconnect() {
		if (msgListenerContainer != null) {
			try {
				msgListenerContainer.stop();
				msgListenerContainer.shutdown();
				msgListenerContainer.destroy();	
			}
			catch (JmsException e) {
				e.printStackTrace();
			}
			finally {
				msgListenerContainer = null;				
			}
		}
	}

	@Override
	public boolean isConnected() {
		return msgListenerContainer == null ? false : true;
	}
	
	@Override
	public int getNumOfMessagesProcessed() {
		return twitterDataListener.getNumOfMessagesProcessed();
	}



	static class  TwitterDataListener implements MessageListener
	{
		private ExecutorService executorService;
		private UserManager userManager;
		private ActivityManager activityManager;
	
		int numOfMessagesProcessed = 0;
		
		public TwitterDataListener(ExecutorService executorService, UserManager userManager, ActivityManager activityManager) {
			this.executorService = executorService;
			this.userManager = userManager;
			this.activityManager = activityManager;
		}
		
	    public void onMessage(final Message message)
	    {
	        if ( message instanceof ObjectMessage )
	        {
	        	ObjectMessage objMsg = (ObjectMessage)message;
	        	try {
		        	TweetData data = (TweetData)(objMsg.getObject());
		        	executorService.execute(new TweetDataProcessor(userManager, activityManager, data));
		        	numOfMessagesProcessed++;
		        	//System.out.println("=======================> TwitterDataListener : [" + data + "]");
	        	}
	        	catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        }
	    }
	    
	    public int getNumOfMessagesProcessed() {
	    	return numOfMessagesProcessed;
	    }
	}
	
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("dataloader-appContextProd.xml");

		TweetLoader loader = (TweetLoader)ctx.getBean(TweetLoader.class);
    	loader.connect();
	}


}
