package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CheckedTest {

    @Test
    void checkedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checkedThrow() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception 을 상속받은 예외는 체크 예외
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * 체크 예외는 잡아서 처리하거나 던지거나 선택을 해야한다
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외 잡아서 처리
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                System.out.println("message = " + e);
            }
        }

        /**
         * 예외 밖으로 던지기
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException { // 밖으로 던짐을 선언한다
            throw new MyCheckedException("ex");
        }
    }
}
