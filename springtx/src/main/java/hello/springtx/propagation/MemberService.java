package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    /**
     * 트랜잭션을 각각 사용
     */
    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMsg = new Log(username);

        log.info("========== memberRepository call start==============");
        memberRepository.save(member);
        log.info("========== memberRepository call end ==============");

        log.info("========== logRepository call start ==============");
        logRepository.save(logMsg);
        log.info("========== logRepository call end ==============");
    }

    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMsg = new Log(username);

        log.info("========== memberRepository call start ==============");
        memberRepository.save(member);
        log.info("========== memberRepository call end ==============");

        log.info("========== logRepository call start ==============");
        try {
            logRepository.save(logMsg);
        } catch (RuntimeException e){
            log.info("save log failed = {}", logMsg.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("========== logRepository call end ==============");
    }
}
