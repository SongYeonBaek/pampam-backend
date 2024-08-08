package com.example.pampam.orders.controller;

import com.example.pampam.common.BaseResponse;
import com.example.pampam.exception.EcommerceApplicationException;
import com.example.pampam.exception.ErrorCode;
import com.example.pampam.orders.model.entity.PaymentValidationResult;
import com.example.pampam.orders.model.response.GetOrderedProductList;
import com.example.pampam.orders.model.response.OrdersListRes;
import com.example.pampam.orders.model.response.PostOrderInfoRes;
import com.example.pampam.orders.service.GroupBuyScheduler;
import com.example.pampam.orders.service.OrdersService;
import com.example.pampam.orders.service.PaymentService;
import com.example.pampam.product.model.response.GetProductReadRes;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Api(value = "주문/결제 컨트롤러 v1", tags = "주문/결제 API")
@RequiredArgsConstructor
@RequestMapping("/order")
@CrossOrigin("*")
public class OrdersController {
    private final OrdersService ordersService;
    private final PaymentService paymentService;
    private final GroupBuyScheduler groupBuyScheduler;

    @ApiOperation(value = "상품 주문")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일을 받기 위한 토큰 입력",
                    required = true, paramType = "query", dataType = "string", defaultValue = ""),
            @ApiImplicitParam(name = "impUid", value = "주문 번호 입력",
                    required = true, paramType = "query", dataType = "string", defaultValue = "")})
    @RequestMapping(method = RequestMethod.GET,value = "/validation")
    public BaseResponse<List<PostOrderInfoRes>> ordersCreate(@RequestHeader(value = "Authorization") String token, String impUid) {
        try {
            PaymentValidationResult validationResult = paymentService.paymentValidation(impUid);

            if (validationResult.isValid()) {
                // orders와 orderedProduct에 저장
                return ordersService.createOrder(token, impUid, validationResult.getPaymentProducts(), validationResult.getAmount());
            }

            throw new EcommerceApplicationException(ErrorCode.NOT_MATCH_AMOUNT);
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    //Consumer의 주문 내역을 확인
    @ApiOperation(value = "주문 내역 조회")
    @ApiImplicitParam(name = "email", value = "이메일을 받기 위한 토큰 입력",
            required = true, paramType = "query", dataType = "string", defaultValue = "")
    @RequestMapping(method = RequestMethod.GET,value = "/list")
    public BaseResponse<List<OrdersListRes>>  orderList(@RequestHeader(value = "Authorization") String token) {
        return ordersService.orderList(token);
    }

    //Consumer가 구매를 취소
    @ApiOperation(value = "주문 취소")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "impUid", value = "취소할 주문의 주문 번호 입력",
                    required = true, paramType = "query", dataType = "string", defaultValue = ""))
    @RequestMapping(method = RequestMethod.GET,value = "/cancel")
    public BaseResponse<String> orderCancel(String impUid, Integer price) throws IOException {
        return paymentService.paymentCancel(impUid, price);
    }

    //마감 시간이 지나고 인원 수가 다 차지 않았다면 결제를 취소
    @ApiOperation(value = "공동 구매 전체 취소")
    @ApiImplicitParam(name = "productId", value = "공동 구매를 취소할 상품의 상품 번호 입력",
                    required = true, dataType = "Long", paramType = "query", defaultValue = "")
    @RequestMapping(method = RequestMethod.GET,value = "/group/cancel")
    public BaseResponse<String> groupCancel(Long productId) throws IOException {
        return ordersService.groupCancel(productId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/product/list")
    public BaseResponse<List<GetOrderedProductList>> orderedProductList(@RequestHeader(value = "Authorization") String token) throws IOException {
        return ordersService.orderedProductList(token);
    }


    //테스트
    @RequestMapping(method = RequestMethod.GET, value = "test")
    public void scheduler() throws IOException {
       groupBuyScheduler.groupBuy();
    }

}
