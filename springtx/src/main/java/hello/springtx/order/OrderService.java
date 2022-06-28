package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("call order");
        orderRepository.save(order);

        log.info("call payment process");
        if (order.getUsername().equals("예외")) {
            log.info("시스템 예외 발생");
            throw new RuntimeException("시스템 예외");
        } else if (order.getUsername().equals("잔고부족")) {
            log.info("잔고 부족 비지니스 예외 발생");
            order.setPayStatus("대기");
            throw new NotEnoughMoneyException("잔고 부족해요");
        } else {
            // 정상
            log.info("정상");
            order.setPayStatus("완료");
        }
        log.info("payment process complete");
    }
}
