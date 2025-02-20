package com.paymybuddy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

//@ActiveProfiles("test")
@SpringBootTest
class PaymybuddyApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void testMySQLConnection() throws SQLException {
		assertNotNull(dataSource.getConnection());
		System.out.println("MySQL Connection Successful!");
	}

	@Test
	void testH2Connection() {
		assertNotNull(jdbcTemplate);
		System.out.println("H2 Connection Successful!");
	}
}
