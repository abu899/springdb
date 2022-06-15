package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Using transaction
 */
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false); // 트랜잭션 시작

            businessLogic(conn, fromId, toId, money);

            conn.commit(); // 트랜잭션 종료
        } catch (Exception e) {
            conn.rollback(); // 실패 시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }


    }

    private void businessLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId, conn);
        Member toMember = memberRepository.findById(toId, conn);

        memberRepository.update(fromId, fromMember.getMoney() - money, conn);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money, conn);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException(" Error during transfer");
        }
    }

    private void release(Connection conn) {
        if (null != conn) {
            try {
                conn.setAutoCommit(true); // 커넥션 풀을 고려해서 true로 바꾸고 돌려준다
                conn.close();
            } catch (Exception e) {
                System.out.println("error = " + e);
            }
        }
    }
}
