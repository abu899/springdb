package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckedAppTest {

    @Test
    void unChecked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.logic())
                .isInstanceOf(Exception.class);
    }


    static class Controller {

        public void logic() {
            Service service = new Service();
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("connect fail");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e); // 예외 변환
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

}
