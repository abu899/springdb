package hello.jdbc.exception.basic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckedTest {


    @Test
    void unCheckedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unCheckedThrow() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUnCheckedException.class);
    }

    /**
     * RuntimeException 을 상속받은 언체크 예외
     */
    static class MyUnCheckedException extends RuntimeException {
        public MyUnCheckedException(String message) {
            super(message);
        }
    }

    /**
     * UnChecked 예외는 잡으면 처리하고, 잡지 않으면 자동으로 밖으로 던진다
     */
    static class Service {
        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyUnCheckedException e) {
                // 예외 처리 로직
                System.out.println("message = " + e);
            }
        }

        /**
         * 예외를 잡지 않는다면 자연스럽게 밖으로 넘어감
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUnCheckedException("ex");
        }
    }
}
