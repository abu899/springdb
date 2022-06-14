package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

public class ConnectionTest {

    @Test
    void driverManagerTest() throws SQLException {
        Connection conn1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection conn2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println("conn1 = " + conn1);
        System.out.println("conn2 = " + conn2);
    }

    @Test
    void dataSourceDriverManagerTest() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    @Test
    void dataSourceConnectionPoolTest() throws SQLException, InterruptedException {
        // connection pooling
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("myHikariPool");

        useDataSource(dataSource);
        Thread.sleep(1000); // 커넥션 풀에 커넥션을 추가하는 걸 보기위해
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        System.out.println("conn1 = " + conn1);
        System.out.println("conn2 = " + conn2);
    }
}
