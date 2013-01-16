package test.mec.datareceiver.service;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.dbcp.BasicDataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mec.datareceiver.model.dao.JdbcActivityDao;
import com.mec.datareceiver.model.dao.JdbcUserDao;
import com.mec.datareceiver.model.dao.UserDao;
import com.mec.datareceiver.model.domain.User;
import com.mec.datareceiver.service.SimpleActivityManager;
import com.mec.datareceiver.service.UserManager;
import com.mec.datareceiver.service.SimpleUserManager;

public class UserAndActivityEndToEndTest {
	static final Logger log = Logger.getLogger(UserAndActivityEndToEndTest.class);
	//private BasicDataSource dataSrc;
	private DriverManagerDataSource dataSrc;
	private JdbcUserDao userDao;
	private SimpleUserManager userManager;
	private JdbcActivityDao activityDao;
	private SimpleActivityManager activityManager;
	
	private void setDataSource() {
		dataSrc = new DriverManagerDataSource(); //BasicDataSource();
		dataSrc.setDriverClassName("com.mysql.jdbc.Driver");
		dataSrc.setUrl("jdbc:mysql://localhost:3306/test");
		dataSrc.setUsername("soba31admin");
		dataSrc.setPassword("admin_123");
	}
	
	@Before
	public void setUp() {
		setDataSource();
		userDao = new JdbcUserDao();
		userDao.setDataSource(dataSrc);
		userManager = new SimpleUserManager();
		userManager.setUserDao(userDao);
		
		activityDao = new JdbcActivityDao();
		activityDao.setDataSource(dataSrc);
		activityManager = new SimpleActivityManager();
		activityManager.setActivityDao(activityDao);
	}
	
	@After
	public void tearDown() throws SQLException {
		activityManager.deleteAllActivities();
		userManager.deleteAllUsers();		
		dataSrc = null;
		userDao = null;
		userManager = null;
		activityDao = null;
		activityManager = null;
	}
	
	@Test
	public void userTableShouldHaveTwoUsers() {
		int numOfUsers = userManager.getNumberOfUsers();
		log.info("Number of users [" + numOfUsers + "]");
		assertEquals(0, numOfUsers);
	}
	
	@Test
	public void deleteSingleUser() {
		User user = new User();
		user.setScreenName("tester1");
		userManager.deleteUser(user);
		int numOfUsers = userManager.getNumberOfUsers();
		log.info("Number of users [" + numOfUsers + "]");
		assertEquals(0, numOfUsers);
	}

	@Test
	public void shouldBeEmptyAfterDeletingAllRowsInTheUserTable() {
		userManager.deleteAllUsers();
		int numOfUsers = userManager.getNumberOfUsers();
		log.info("Number of users [" + numOfUsers + "]");
		assertEquals(0, numOfUsers);		
	}
}
