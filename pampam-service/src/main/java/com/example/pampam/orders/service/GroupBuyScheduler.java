package com.example.pampam.orders.service;

import com.example.pampam.orders.model.entity.OrderedProduct;
import com.example.pampam.orders.repository.OrderedProductRepository;
import com.example.pampam.orders.repository.OrdersRepository;
import com.example.pampam.product.model.entity.Product;
import com.example.pampam.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

    @Scheduled(cron="0 5 0 * * *")
    public void groupBuy(){
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
                } else {
                    // 공동 구매 취소
                    orderedProduct.setStatus(2);
                }
                orderedProductRepository.save(orderedProduct);
            }
        }
    }
}
