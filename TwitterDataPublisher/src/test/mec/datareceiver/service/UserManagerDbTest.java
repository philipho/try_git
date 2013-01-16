package test.mec.datareceiver.service;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
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
import static org.hamcrest.CoreMatchers.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mec.datareceiver.model.dao.JdbcActivityDao;
import com.mec.datareceiver.model.dao.JdbcUserDao;
import com.mec.datareceiver.model.domain.User;
import com.mec.datareceiver.service.SimpleActivityManager;
import com.mec.datareceiver.service.SimpleUserManager;

public class UserManagerDbTest {
	static final Logger log = Logger.getLogger(UserManagerDbTest.class);
	
	private DriverManagerDataSource dataSrc;
	private JdbcTemplate jdbcTemplate;
	private JdbcUserDao userDao;
	private SimpleUserManager userManager;
	private JdbcActivityDao activityDao;
	private SimpleActivityManager activityManager;


	protected IDatabaseConnection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "soba31admin", "admin_123");
		return new DatabaseConnection(jdbcConnection);
	}

	protected IDataSet getDataSet() throws Exception {
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("src/test/mec/datareceiver/service/user-dataset.xml"));
		return dataSet;
	}
	
	protected DatabaseOperation getSetUpOperation() throws Exception {
		//return DatabaseOperation.REFRESH;
		return DatabaseOperation.CLEAN_INSERT;
	}

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
	public void setUp() throws Exception {
		setDataSource();
		jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSrc);
		DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
		
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
	public void tearDown() throws Exception {
		jdbcTemplate = null;
		dataSrc = null;
		userDao = null;
		userManager = null;
		activityDao = null;
		activityManager = null;
	}
	
	
	@Test
	public void userTableShouldHaveThreeUsers() {
		int numOfUsers = userManager.getNumberOfUsers();
		assertEquals(3, numOfUsers);
	}
	
	@Test
	public void deleteSingleUser() {
		int oldNumOfUsers = userManager.getNumberOfUsers();
		User user = new User();
		user.setScreenName("tester1");
		userManager.deleteUser(user);
		int newNumOfUsers = userManager.getNumberOfUsers();
		assertEquals(--oldNumOfUsers, newNumOfUsers);
	}
	
	@Test
	public void shouldBeEmptyAfterDeletingAllRowsInTheUserTable() {
		userManager.deleteAllUsers();
		int numOfUsers = userManager.getNumberOfUsers();
		assertEquals(0, numOfUsers);		
	}

	@Test
	public void deleteTwoUsersConsecutively() {
		int oldNumOfUsers = userManager.getNumberOfUsers();
		User user = new User();
		user.setScreenName("tester1");
		userManager.deleteUser(user);
		oldNumOfUsers--;
		user.setScreenName("tester2");
		userManager.deleteUser(user);
		oldNumOfUsers--;
		int newNumOfUsers = userManager.getNumberOfUsers();
		assertEquals(oldNumOfUsers, newNumOfUsers);		
	}
	
	@Test
	public void shouldRetrieveASingleUserByScreenName() {
		List<User> aUserList = userManager.getUser("tester3");
		User tester3 = new User();
		tester3.setScreenName("tester3");
		assertThat(aUserList.size(), is(equalTo(1)));
		assertThat(tester3, is(equalTo(aUserList.get(0))));
	}
	
	@Test
	public void shouldDeleteASingleUserByScreenName() {
		User aUser = new User();
		aUser.setScreenName("tester2");
		List<User> userList = userManager.getUser("tester2");		
		assertEquals(1, userList.size());
		userManager.deleteUser(aUser);
		userList = userManager.getUser("tester2");		
		assertEquals(0, userList.size());
	}
	
	@Test
	public void shouldAddASingleUserToDatabase() {
		User aUser = new User();
		aUser.setScreenName("tester4");
		userManager.addUser(aUser);
		assertThat(userManager.getNumberOfUsers(), is(equalTo(4)));
	}

}
