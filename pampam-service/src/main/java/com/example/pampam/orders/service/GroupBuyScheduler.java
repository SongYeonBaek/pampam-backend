package com.example.pampam.orders.service;

import com.example.pampam.member.model.request.SendEmailReq;
import com.example.pampam.member.service.EmailVerifyService;
import com.example.pampam.orders.model.entity.OrderedProduct;
import com.example.pampam.orders.repository.OrderedProductRepository;
import com.example.pampam.orders.repository.OrdersRepository;
import com.example.pampam.product.model.entity.Product;
import com.example.pampam.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupBuyScheduler {
    private final OrderedProductRepository orderedProductRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final EmailVerifyService emailVerifyService;

//    @Scheduled(cron="0 5 0 * * *")
    public void groupBuy() throws IOException {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        LocalDateTime dateTime = now.atStartOfDay().withHour(9).withMinute(0).withSecond(0).withNano(0);

        Date date = Date.from(dateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        List<Product> productList = productRepository.findAllByCloseAt(date);

        for(Product product : productList){
            Integer people = product.getPeople();
            Integer peopleCount = product.getPeopleCount();

            List<OrderedProduct> orderedProducts = orderedProductRepository.findAllByProductIdx(product.getIdx());
            for(OrderedProduct orderedProduct : orderedProducts) {
                if (people <= peopleCount) {
                    // 공동 구매 체결
                    orderedProduct.setStatus(1);

                    //공동 구매 체결, 배송 시작 이메일 발송
                    SendEmailReq sendEmailReq = SendEmailReq.buildSendEmailReq(orderedProduct.getConsumerEmail());
                    emailVerifyService.sendSuccessEmail(sendEmailReq);


                } else {
                    // 공동 구매 취소
                    orderedProduct.setStatus(2);

                    //결제 취소
                    paymentService.paymentCancel(orderedProduct.getImpUid(), orderedProduct.getPrice());

                    //환불 완료 이메일 발송
                    SendEmailReq sendEmailReq = SendEmailReq.buildSendEmailReq(orderedProduct.getConsumerEmail());
                    emailVerifyService.sendRefundEmail(sendEmailReq);
                }
                orderedProductRepository.save(orderedProduct);
            }

            //상품 상태를 마감된 상품으로 변경
            product.setAvailable(false);
            productRepository.save(product);

        }
    }
}
