package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Nested
    @DisplayName("기본 트랜잭션")
    class SimpleTransactionTest {
        @Test
        void commit() {
            log.info("==============tx Start===============");
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

            log.info("===============commit start================");
            txManager.commit(status);
            log.info("===============commit end=================");
        }

        @Test
        void rollback() {
            log.info("==============tx Start===============");
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

            log.info("===============rollback start================");
            txManager.rollback(status);
            log.info("===============rollback end=================");
        }
    }

    @Nested
    @DisplayName("서로 다른 트랜잭션 사용")
    class SeparateTransactionTest {
        @Test
        void doubleCommit() {
            log.info("==============tx_1 Start===============");
            TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

            log.info("===============commit tx_1 start================");
            txManager.commit(tx1);

            log.info("==============tx_2 Start===============");
            TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

            log.info("===============commit tx_2 start================");
            txManager.commit(tx2);
        }

        @Test
        void doubleCommitRollback() {
            log.info("==============tx_1 Start===============");
            TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

            log.info("===============commit tx_1 start================");
            txManager.commit(tx1);

            log.info("==============tx_2 Start===============");
            TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

            log.info("===============rollback tx_2 start================");
            txManager.commit(tx2);
        }
    }

    @Nested
    @DisplayName("하나의 트랜잭션 내에서 다른 트랜잭션 동작")
    class PropagationTest {
        @Test
        @DisplayName("Commit 내 Commit 실행")
        void inner_commit() {
            log.info("====================외부 트랜잭션 시작===================");
            TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

            log.info("====================내부  트랜잭션 시작==================");
            TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("inner.isNewTransaction = {}", inner.isNewTransaction());
            log.info("====================내부  트랜잭션 커밋==================");
            txManager.commit(inner);

            log.info("====================외부  트랜잭션 커밋==================");
            txManager.commit(outer);
        }

        @Test
        @DisplayName("외부 rollback, 내부 commit")
        void outer_rollback() {
            log.info("====================외부 트랜잭션 시작===================");
            TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

            log.info("====================내부  트랜잭션 시작==================");
            TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("inner.isNewTransaction = {}", inner.isNewTransaction());
            log.info("====================내부  트랜잭션 커밋==================");
            txManager.commit(inner);

            log.info("====================외부  트랜잭션 롤백==================");
            txManager.rollback(outer);
        }

        @Test
        @DisplayName("외부 commit, 내부 rollback")
        void inner_rollback() {
            log.info("====================외부 트랜잭션 시작===================");
            TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

            log.info("====================내부  트랜잭션 시작==================");
            TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("inner.isNewTransaction = {}", inner.isNewTransaction());
            log.info("====================내부  트랜잭션 롤백==================");
            txManager.rollback(inner);

            log.info("====================외부  트랜잭션 커밋==================");
            assertThatThrownBy(()->txManager.commit(outer)).isInstanceOf(UnexpectedRollbackException.class);
        }
    }

    @Nested
    @DisplayName("다른 트랜잭션 옵션을 사용하는 테스트")
    class DifferentPropagationOptionTest {
        @Test
        @DisplayName("REQUIRES NEW")
        void requires_new_commit() {
            log.info("====================외부 트랜잭션 시작===================");
            TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
            log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

            log.info("====================내부  트랜잭션 시작==================");
            DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus inner = txManager.getTransaction(definition);
            log.info("inner.isNewTransaction = {}", inner.isNewTransaction());
            log.info("====================내부  트랜잭션 롤백==================");
            txManager.rollback(inner);

            log.info("====================외부  트랜잭션 커밋==================");
            txManager.commit(outer);
        }
    }

}
