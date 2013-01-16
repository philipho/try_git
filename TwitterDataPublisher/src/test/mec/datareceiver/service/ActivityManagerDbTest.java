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
import junit.runner.Version;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mec.datareceiver.model.dao.JdbcActivityDao;
import com.mec.datareceiver.model.dao.JdbcUserDao;
import com.mec.datareceiver.model.domain.Activity;
import com.mec.datareceiver.model.domain.User;
import com.mec.datareceiver.service.SimpleActivityManager;
import com.mec.datareceiver.service.SimpleUserManager;

public class ActivityManagerDbTest {
	static final Logger log = Logger.getLogger(ActivityManagerDbTest.class);
	
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
		activityManager.deleteAllActivities();
		jdbcTemplate = null;
		dataSrc = null;
		userDao = null;
		userManager = null;
		activityDao = null;
		activityManager = null;
	}
	
	
	private int getUserIdByScreenName(String screenName) {
		return (userManager.getUser(screenName)).get(0).getUserId();
	}

	private void addOneActivity() {
		int tester1Id = getUserIdByScreenName("tester1");
		int tester2Id = getUserIdByScreenName("tester2");
		Activity act = new Activity();
		act.setSenderId(tester1Id);
		act.setReceiverId(tester2Id);
		act.setTxtLength(123);
		activityManager.insert(act);
	}
	
	@Test
	public void shouldAddOneActivityToDatabase() {
		assertThat(activityManager.getActivitiesCount(), is(equalTo(0)));
		addOneActivity();
		assertThat(activityManager.getActivitiesCount(), is(equalTo(1)));
	}
	
	@Test
	public void deleteAllActivitiesShouldClearTheAcitivityTable() {
		assertThat(activityManager.getActivitiesCount(), is(equalTo(0)));
		addOneActivity();		
		assertThat(activityManager.getActivitiesCount(), is(equalTo(1)));
		activityManager.deleteAllActivities();
		assertThat(activityManager.getActivitiesCount(), is(equalTo(0)));		
	}

	@Test(expected = Throwable.class) // Should throw a RuntimeException (DataIntegrityViolationException) about fk constraint.
	public void insertAnActivityWithANonExistentReceiverInTheUserTableShouldFail() {
		assertThat(activityManager.getActivitiesCount(), is(equalTo(0)));
		int tester1Id = getUserIdByScreenName("tester1");
		int tester2Id = 1;
		Activity act = new Activity();
		act.setSenderId(tester1Id);
		act.setReceiverId(tester2Id);
		act.setTxtLength(123);
		activityManager.insert(act); 
	}
	
	@Test(expected = Throwable.class) // Should throw a RuntimeException (DataIntegrityViolationException) about fk constraint.
	public void insertAnActivityWithANonExistentSenderInTheUserTableShouldFail() {
		assertThat(activityManager.getActivitiesCount(), is(equalTo(0)));
		int tester1Id = getUserIdByScreenName("tester1");
		int tester2Id = 1;
		Activity act = new Activity();
		act.setSenderId(tester2Id);
		act.setReceiverId(tester1Id);
		act.setTxtLength(123);
		activityManager.insert(act); 
	}
}
