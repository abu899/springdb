package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.logic())
                .isInstanceOf(Exception.class);
    }


    static class Controller {

        public void logic() throws SQLException, ConnectException {
            Service service = new Service();
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("connect fail");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
