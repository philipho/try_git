package test.mec.datareceiver;

import static org.junit.Assert.*;

import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers.*;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.MockConnectionFactory;
import com.mockrunner.mock.jms.MockConnection;
import com.mockrunner.mock.jms.MockMessageProducer;
import com.mockrunner.mock.jms.MockQueue;
import com.mockrunner.mock.jms.MockQueueSender;
import com.mockrunner.mock.jms.MockSession;
import com.mockrunner.mock.jms.MockTopic;
import com.mockrunner.mock.jms.MockTopicPublisher;

import com.mec.datareceiver.DataLoader;
import com.mec.datareceiver.MockTweetDataSource;
import com.mec.datareceiver.TweetLoader;

public class DataLoaderLoadDataToDbEndToEndTest {
	final static Logger log = Logger.getLogger(DataLoaderLoadDataToDbEndToEndTest.class);
	
	// Publisher
	ConnectionFactory connectionFactory;
	ConfigurationManager configManager;
	DestinationManager destManager;
	MockMessageProducer messageProducer ;
	MockConnection conn;
	MockSession session;
	MockTopic topic;
	MockQueue queue;
	
	// Message Listener
	DefaultMessageListenerContainer dmlc;
	
	// Domain objects
	DataLoader dataLoader;
	MockTweetDataSource dataSource;
	
	
	@Before
	public void setUp() {
		// Config for mock publisher
		configManager = new ConfigurationManager();
		destManager = new DestinationManager();
//		destManager.createTopic("TWITTER.USER");
		destManager.createQueue("TWITTER.USER");
		conn = new MockConnection(destManager, configManager);
		//session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		session = new MockSession(conn, false, Session.AUTO_ACKNOWLEDGE);
//		topic = destManager.getTopic("TWITTER.USER");
//		messageProducer = new MockTopicPublisher(conn, session, topic);
		queue = destManager.getQueue("TWITTER.USER");
		messageProducer = new MockQueueSender(conn, session, queue);

		dataSource = new MockTweetDataSource();
		dataSource.setMessageProducer(messageProducer);
		
		// Config for message listener container
		connectionFactory = new MockConnectionFactory(destManager, configManager);
		dmlc = new DefaultMessageListenerContainer();
		dmlc.setConnectionFactory(connectionFactory);
		dmlc.setDestination(queue);
		dmlc.setPubSubDomain(false);
		
		dataLoader = new TweetLoader(dmlc, null, null);
	}
	
	@After
	public void tearDown() {
		configManager = null;
		destManager =  null;
		conn = null;
		session = null;
		topic = null;
		messageProducer = null;
		dataSource = null;
		dataLoader.disconnect();
		dataLoader = null;
	}
	
	private void startDataLoaderToSubscribeAndDataSourceToPublish(int numOfMessagesToSend) {
		dataSource.setUpNumberOfDataItemsToPublish(numOfMessagesToSend);
		assertEquals(dataSource.getNumOfMessagesInQueue(), numOfMessagesToSend);
		dataLoader.connect();
		dataSource.publish();
	}

	@Test
	public void dataLoaderShouldConnectToDataSourceAndDownloadDataSuccessfully() throws Exception {
		int numOfMessagesToSend = 5;
		startDataLoaderToSubscribeAndDataSourceToPublish(numOfMessagesToSend);
		Thread.sleep(100); // time for receiver to process messages.
		log.info("Number of message in queue [" + queue.getQueueName() + "] has [" + queue.getCurrentMessageList().size() + "] messages.");
		assertEquals(numOfMessagesToSend, dataLoader.getNumOfMessagesProcessed());		
	}
	
	@Test
	public void dataLoaderShutdownAfterReceivingThreeMessagesFromDataSource() throws Exception {
		int numOfMessagesToSend = 15000;
		startDataLoaderToSubscribeAndDataSourceToPublish(numOfMessagesToSend);
		dataLoader.disconnect();
		log.info("Number of message in queue [" + queue.getQueueName() + "] has [" + queue.getCurrentMessageList().size() + "] messages.");
		assertTrue(numOfMessagesToSend != dataLoader.getNumOfMessagesProcessed());				
	}


}
