package com.example.pampam.orders.service;

import com.example.pampam.cart.service.CartService;
import com.example.pampam.common.BaseResponse;
import com.example.pampam.exception.EcommerceApplicationException;
import com.example.pampam.exception.ErrorCode;
import com.example.pampam.orders.model.entity.OrderedProduct;
import com.example.pampam.orders.model.entity.Orders;
import com.example.pampam.orders.model.entity.PaymentProducts;
import com.example.pampam.orders.model.response.GetOrderedProductList;
import com.example.pampam.orders.model.response.GetPortOneRes;
import com.example.pampam.orders.model.response.OrdersListRes;
import com.example.pampam.orders.model.response.PostOrderInfoRes;
import com.example.pampam.orders.repository.OrderedProductRepository;
import com.example.pampam.orders.repository.OrdersRepository;
import com.example.pampam.product.model.entity.Product;
import com.example.pampam.product.model.entity.ProductImage;
import com.example.pampam.product.model.response.GetProductReadRes;
import com.example.pampam.product.repository.ProductRepository;
import com.example.pampam.utils.JwtUtils;
import com.siot.IamportRestClient.exception.IamportResponseException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrderedProductRepository orderedProductRepository;
    private final PaymentService paymentService;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public BaseResponse<List<PostOrderInfoRes>> createOrder(String token, String impUid, PaymentProducts paymentProducts, Integer amount) throws IamportResponseException, IOException {
        List<PostOrderInfoRes> orderList = new ArrayList<>();

        token = JwtUtils.replaceToken(token);
        Long consumerIdx = JwtUtils.getUserIdx(token, secretKey);
        String userEmail = JwtUtils.getUsername(token, secretKey);

        if (consumerIdx != null) {
            Orders order = ordersRepository.save(Orders.dtoToEntity(impUid, userEmail, consumerIdx, amount));

            // Custom Data 안에 있던 Product 리스트 하나씩 꺼내와서 OrderedProduct에 저장
            for (GetPortOneRes getPortOneRes : paymentProducts.getProducts()) {
                orderedProductRepository.save(OrderedProduct.buildOrderedProduct(order, getPortOneRes, consumerIdx, impUid, getPortOneRes.getPrice(), userEmail));
                orderList.add(PostOrderInfoRes.dtoToEntity(order.getIdx(), impUid, getPortOneRes, order));

                // 카트 삭제
                cartService.deleteOrderedCart(consumerIdx, getPortOneRes.getId());

                //공동 구매 참여 인원 증가
                Optional<Product> product = productRepository.findById(getPortOneRes.getId());
                if (product.isPresent()) {
                    Product p = product.get();
                    p.setPeopleCount(p.getPeopleCount() + 1);

                    productRepository.save(p);
                }
            }

            return BaseResponse.successResponse("주문 완료", orderList);
        } else {
            throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
        }
    }

    public BaseResponse<List<OrdersListRes>> orderList(String token) {
        token = JwtUtils.replaceToken(token);
        Claims consumerInfo = JwtUtils.getConsumerInfo(token, secretKey);
        String email = consumerInfo.get("email", String.class);
        List<OrdersListRes> result = new ArrayList<>();

        if (email != null) {
            List<Orders> orders = ordersRepository.findAllByConsumerEmail(email);
            for(Orders order : orders){
                Product product = order.getOrderProductsList().get(0).getProduct();
                result.add(OrdersListRes.dtoToEntity(order, product));
            }

            return BaseResponse.successResponse("주문 내역 조회.", result);
        }
        return null;
    }

    public BaseResponse<String> groupCancel(Long productId) throws IOException {
        Optional<Product> product = productRepository.findById(productId);

        List<String> impUidList = new ArrayList<>();
        if(product.isPresent()){
            for(OrderedProduct p : product.get().getOrderedProducts()){
                impUidList.add(p.getOrders().getImpUid());
            }
        } else {
            throw new EcommerceApplicationException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        for(String impUid : impUidList){
            paymentService.paymentCancel(impUid, product.get().getPrice());
        }
        return  BaseResponse.successResponse("공동구매 전원 취소 완료", "[결제 취소] 인원 부족으로 인해 공동구매가 취소되었습니다.");
    }

    public BaseResponse<List<GetOrderedProductList>> orderedProductList(String token) {
        token = JwtUtils.replaceToken(token);
        Long consumerIdx = JwtUtils.getUserIdx(token, secretKey);
        List<GetOrderedProductList> orderedProductList = new ArrayList<>();
        List<String> images = new ArrayList<>();

        if (consumerIdx != null) {
            List<OrderedProduct> orderedProducts = orderedProductRepository.findAllByConsumerIdx(consumerIdx);
            for(OrderedProduct orderedProduct : orderedProducts){
                Product product = orderedProduct.getProduct();
                Optional<Product> p = productRepository.findById(product.getIdx());

                if(p.isPresent()){
                    product = p.get();

                    for (ProductImage image : product.getImages()) {
                        images.add(image.getImagePath());
                    }

                    orderedProductList.add(GetOrderedProductList.entityToDto(product, images, orderedProduct.getStatus()));
                }
            }

            return BaseResponse.successResponse("주문한 상품 목록 불러오기 완료", orderedProductList);
        }
        return null;


    }
}
