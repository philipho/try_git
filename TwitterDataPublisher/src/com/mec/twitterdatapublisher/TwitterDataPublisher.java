package com.mec.twitterdatapublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;


import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.internal.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class TwitterDataPublisher {
	private final static Logger log = Logger.getLogger(TwitterDataPublisher.class);
    private final static String CONSUMER_KEY = "uuSHgwMlsG4N9UUU6qcPw";
    private final static String CONSUMER_KEY_SECRET = "ypw2uEWEgDNQIx372YNPqY3KUp3LEV9meF4oxlxfSNc";	 
    
    private TwitterStream twitterStream;
	private JmsTemplate jmsTemplate;
	
	/*
	 * A jmsTemplate object which publishes tweet data 
	 */
	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	/*
	 * Initialise Twitter connection
	 */
	private void init() {
        twitterStream = new TwitterStreamFactory().getInstance();
    	twitterStream.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
        //Twitter twitter = new TwitterFactory().getInstance();         
    	String accessToken = "14179854-BAwVBvRymFBptGuJhNbT3reH6gOgagyFtr8x9Iss";;
    	String accessTokenSecret = "0DwS14c04u51sallM7owB8JesWtsT2VL4O8fS7LYs";
    	AccessToken oauthAccessToken = new AccessToken(accessToken, accessTokenSecret);    	
    	twitterStream.setOAuthAccessToken(oauthAccessToken);		
    	
    	// Add listener to process stream data
    	twitterStream.addListener(createStatusListener());
	}
	
	/**
	 * Start to stream and process Twitter data
	 */
	public void start() {
		String destName = "No Default Destination Name";
		if (jmsTemplate == null) {
			log.error("jmsTemplate is NOT initialised! Exiting...");
			System.exit(1);
		}
		else {
			destName = getDefaultDestinationName();
			System.out.println("jmsTemplate destination [" + destName + "]");
			System.out.println("Message delivery mode (1 - non-persistent, 2 - persistent) [" + jmsTemplate.getDeliveryMode() + "]");
		}
		init();
		twitterStream.sample();
	}

	private String getDefaultDestinationName() {
		return getDefaultDestinationName(jmsTemplate.getDefaultDestination());
	}
	
	public static String getDefaultDestinationName(Destination dest) {
		String destName = "No Default Destination Name set";
		
		try {
			if (dest instanceof Topic) {
				destName = ((Topic)dest).getTopicName();
			}
			else if (dest instanceof Queue) {
				destName = ((Queue)dest).getQueueName();
			}
		}
		catch (JMSException e) {
			e.printStackTrace();
		}
		return destName;		
	}
	
	/**
	 * Publish tweet data using JMS pubsub
	 * @param data TweetData contains sender and receivers and creation time
	 */
	private void publishData(final TweetData data) {
		System.out.println("Publishing data [" + data + "]");
		jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage objMsg = session.createObjectMessage(data);
				return objMsg;
			}
		});
	}
	
	/*
	 * Create listener to process stream data
	 */
	private StatusListener createStatusListener() {
		return new StatusListener() {
        	Pattern p = Pattern.compile("\\@[\\w]+");
        	Matcher m;
        	
            @Override
            public void onStatus(Status status) {
            	TweetData data = processStatusLine(status);
            	if (data.isValid()) {
            		publishData(data);
            	}
            }

			private TweetData processStatusLine(Status status) {
				TweetData data = new TweetData();
				String sender = status.getUser().getScreenName();
				
				if (sender != null && !"".equals(sender)) {
					data.setSender(sender);
					data.setIsValid(true);
					data.setCreationDateTime(status.getCreatedAt());
					
					String msg = status.getText();
					int txtLength = msg.length();
	            	m = p.matcher(msg);
	        
		        	List<String> receivers = new ArrayList<String>();
	            	while (m.find()) {
	            		String rec = (m.group()).substring(1);
	            		txtLength -= (rec.length() + 1);
	            		receivers.add(rec);
	            	}
	            	data.setReceivers(receivers);
	            	data.setTxtLength(txtLength);
				}
              
            	return data;
			}
            
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
            
        };

	}
	
	/**
	 * Main method to start Twitter stream and return stream data
	 * @param args
	 * @throws TwitterException
	 */
    public static void main(String[] args) throws TwitterException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("appContextProd.xml");

    	TwitterDataPublisher publisher = (TwitterDataPublisher)ctx.getBean(TwitterDataPublisher.class);
    	publisher.start();
    }


}