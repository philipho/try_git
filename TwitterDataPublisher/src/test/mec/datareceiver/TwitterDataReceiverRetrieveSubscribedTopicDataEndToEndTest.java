package test.mec.datareceiver;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import twitter4j.internal.logging.Logger;


import com.mec.datareceiver.TwitterDataListener;
import com.mec.twitterdatapublisher.TweetData;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockObjectMessage;
import com.mockrunner.mock.jms.MockTopic;

@ContextConfiguration(locations = { "classpath:appContextTest.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TwitterDataReceiverRetrieveSubscribedTopicDataEndToEndTest {
	final static Logger log = Logger.getLogger(TwitterDataReceiverRetrieveSubscribedTopicDataEndToEndTest.class);
	final static String JSM_TOPIC = "TWITTER.USERS";

	@Resource
	private JmsTemplate jmsTemplate;
	
	
	private DestinationManager destinationManager;
	
	@Resource
	private DefaultMessageListenerContainer jmsListenerContainer;
	
	@Resource
	private Topic destination;
	
	private TweetData topicData;
		
	@Before
	public void setUp() throws Exception {
		log.info("jmsTemplate [" + jmsTemplate + "]");
		log.info("mockDestinationManager [" + destinationManager + "]");
		log.info("jmsListenerContainer [" + jmsListenerContainer + "]");
		log.info("destination topic name [" + destination.getTopicName() + "]");
		assertNotNull(jmsTemplate);
		//assertNotNull(destinationManager);
		assertNotNull(jmsListenerContainer);
		assertNotNull(destination);
		assertEquals(JSM_TOPIC, getDefaultDestinationName(jmsTemplate.getDefaultDestination())); 
		assertEquals(JSM_TOPIC, getDefaultDestinationName(jmsListenerContainer.getDestination()));
		assertEquals(destination.getTopicName(), JSM_TOPIC);
		populateTweetData();
		log.info("test data [" + topicData + "]");
	}
	
	private void populateTweetData() {
		topicData = new TweetData();
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 12, 10, 15, 23, 33);
		topicData.setCreationDateTime(cal.getTime());
		topicData.setSender("tweet-test-sender");
		List<String> receivers = new ArrayList<String>();
		receivers.add("tweet-test-receiver-1");
		receivers.add("tweet-test-receiver-2");
		receivers.add("tweet-test-receiver-3");
		topicData.setReceivers(receivers);
		topicData.setIsValid(true);
	}
	
	private MessageCreator createTestTweetDataObjectMessageCreator(final TweetData data) {
		return new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage objMsg = session.createObjectMessage(data);
				return objMsg;
			}
		};
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

	
	@Test
	public void dataReceiverShouldRetrieveTopicMessageFromJMSPublisherSuccessfully() throws JMSException {

		// Use mock connectionFactory to publish test data
        //jmsTemplate.send(destination, createTestTweetDataObjectMessageCreator(topicData));
        
        // Use jmsTemplate to retrieve topic data
//        MockObjectMessage message = (MockObjectMessage) jmsTemplate.receive(topic);
//        Assert.assertNotNull("The object message cannot be null!", message.getObject());
//        TweetData data = (TweetData)message.getObject();
//        Assert.assertEquals(topicData, data);
//        message = (MockObjectMessage) jmsTemplate.receive(topic);
        
//	      TweetData subscribedData = dataReceiver.asynSubscribe(brokerURL, topic);
//        helloWorldHandler.handleHelloWorld(message.getText());
//		dataReceiver.asynSubscribe(dataPublisher, topic);
	}

}
