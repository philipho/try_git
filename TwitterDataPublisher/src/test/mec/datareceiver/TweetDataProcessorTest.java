package test.mec.datareceiver;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import test.mec.datareceiver.service.ActivityManagerDbTest;

import static org.hamcrest.Matchers.*;

import com.mec.datareceiver.TweetDataProcessor;
import com.mec.datareceiver.model.dao.JdbcActivityDao;
import com.mec.datareceiver.model.dao.JdbcUserDao;
import com.mec.datareceiver.service.ActivityManager;
import com.mec.datareceiver.service.SimpleActivityManager;
import com.mec.datareceiver.service.SimpleUserManager;
import com.mec.datareceiver.service.UserManager;
import com.mec.twitterdatapublisher.TweetData;

public class TweetDataProcessorTest {
	static final Logger log = Logger.getLogger(ActivityManagerDbTest.class);
	
	private TweetDataProcessor dp;
	private TweetData data;
	private DriverManagerDataSource dataSrc;
	private JdbcTemplate jdbcTemplate;
	private JdbcUserDao userDao;
	private SimpleUserManager userManager;
	private JdbcActivityDao activityDao;
	private SimpleActivityManager activityManager;


	protected void setDataSource() {
		dataSrc = new DriverManagerDataSource();
		dataSrc.setDriverClassName("com.mysql.jdbc.Driver");
		dataSrc.setUrl("jdbc:mysql://localhost:3306/test");
		dataSrc.setUsername("soba31admin");
		dataSrc.setPassword("admin_123");		
	}
	
	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
	}
	
	@Before
	public void setUp() {
		setDataSource();
		jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSrc);
		
		userDao = new JdbcUserDao();
		userDao.setDataSource(dataSrc);
		userManager = new SimpleUserManager();
		userManager.setUserDao(userDao);
		
		activityDao = new JdbcActivityDao();
		activityDao.setDataSource(dataSrc);
		activityManager = new SimpleActivityManager();
		activityManager.setActivityDao(activityDao);

		data = new TweetData();
		data.setSender("tester1");
		List<String> receivers = new ArrayList<String>();
		receivers.add("tester2");
		receivers.add("tester3");		
		data.setReceivers(receivers);
		data.setCreationDateTime(new Date());
		data.setTxtLength(123);
		data.setIsValid(true);
		dp = new TweetDataProcessor(userManager, activityManager, data);

	}
	
	@After
	public void tearDown() {
		dp = null;
		data = null;
		activityManager.deleteAllActivities();
		jdbcTemplate = null;
		dataSrc = null;
		userDao = null;
		userManager = null;
		activityDao = null;
		activityManager = null;

	}
	
	@Test
	public void processorShouldConnectToUserManager() {
		int numOfUsers = userManager.getNumberOfUsers();
		assertThat(numOfUsers, is(not(lessThan(0))));
	}

	@Test
	public void processorShouldConnectToActivityManager() {
		int numOfActivities = activityManager.getActivitiesCount();
		assertThat(numOfActivities, is(not(lessThan(0))));
	}
	
	@Test
	public void processorShouldAddTwoActivitiesToDatabase() {
		int initialNumOfActivities = activityManager.getActivitiesCount();
		dp.process();
		assertThat(activityManager.getActivitiesCount(), is(equalTo(initialNumOfActivities+2)));
	}
	
	@Test
	public void processorShouldAddOneNewUserAndOneActivityToDatabase() {
		data.getReceivers().add("tester4");
		int initialNumOfActivities = activityManager.getActivitiesCount();
		int initialNumOfUsers = userManager.getNumberOfUsers();
		dp.process();
		assertThat(userManager.getNumberOfUsers(), is(equalTo(initialNumOfUsers+1)));
		assertThat(activityManager.getActivitiesCount(), is(equalTo(initialNumOfActivities+3)));
	}

}
