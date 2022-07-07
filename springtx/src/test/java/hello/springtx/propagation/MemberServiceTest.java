package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * MemberService        @Transactional: OFF
     * MemberRepository     @Transactional: ON
     * LogRepository        @Transactional: ON
     */
    @Nested
    @DisplayName("트랜잭션 각각 사용")
    class SeparateTransactionTest {
        @Test
        void outerTransactionOff_success() {
            String username = "outerTransactionOff_success";

            memberService.joinV1(username);

            Assertions.assertTrue(memberRepository.find(username).isPresent());
            Assertions.assertTrue(logRepository.find(username).isPresent());
        }

        @Test
        void outerTransactionOff_fail() {
            String username = "로그예외_outerTransactionOff_fail";

            assertThatThrownBy(() -> memberService.joinV1(username))
                    .isInstanceOf(RuntimeException.class);
            Assertions.assertTrue(memberRepository.find(username).isPresent());
            Assertions.assertFalse(logRepository.find(username).isPresent());
        }
    }


    /**
     * MemberService        @Transactional: ON
     * MemberRepository     @Transactional: OFF
     * LogRepository        @Transactional: OFF
     */
    @Nested
    @DisplayName("하나의 트랜잭션으로 묶기")
    class SingleTransactionTest {
        @Test
        void basicSingleTransaction() {
            String username = "basicSingleTransaction";

            memberService.joinV1(username);

            Assertions.assertTrue(memberRepository.find(username).isPresent());
            Assertions.assertTrue(logRepository.find(username).isPresent());
        }
    }

    /**
     * MemberService        @Transactional: ON
     * MemberRepository     @Transactional: ON
     * LogRepository        @Transactional: ON
     */
    @Nested
    @DisplayName("트랜잭션 전파 적용")
    class PropagationTest {
        @Test
        void outerTransaction_success() {
            String username = "outerTransaction_success";

            memberService.joinV1(username);

            Assertions.assertTrue(memberRepository.find(username).isPresent());
            Assertions.assertTrue(logRepository.find(username).isPresent());
        }

        @Test
        void outerTransaction_fail() {
            String username = "로그예외_outerTransaction_fail";

            assertThatThrownBy(() -> memberService.joinV1(username))
                    .isInstanceOf(RuntimeException.class);
            Assertions.assertFalse(memberRepository.find(username).isPresent());
            Assertions.assertFalse(logRepository.find(username).isPresent());
        }


        /**
         * MemberService        @Transactional: ON
         * MemberRepository     @Transactional: ON
         * LogRepository        @Transactional: ON EXCEPTION
         */
        @Test
        void recoverException_fail() {
            String username = "로그예외_recoverException_fail";

            assertThatThrownBy(() -> memberService.joinV2(username))
                    .isInstanceOf(UnexpectedRollbackException.class);

            Assertions.assertFalse(memberRepository.find(username).isPresent());
            Assertions.assertFalse(logRepository.find(username).isPresent());
        }

        /**
         * MemberService        @Transactional: ON
         * MemberRepository     @Transactional: ON
         * LogRepository        @Transactional: ON REQUIRES_NEW
         */
        @Test
        @DisplayName("REQUIRES NEW 적용")
        void recoverException_success() {
            String username = "로그예외_recoverException_success";

            memberService.joinV2(username);
            Assertions.assertTrue(memberRepository.find(username).isPresent());
            Assertions.assertFalse(logRepository.find(username).isPresent());
        }
    }
}