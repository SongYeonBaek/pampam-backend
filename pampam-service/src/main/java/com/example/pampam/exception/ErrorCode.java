package com.example.pampam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /**
     * 1xxx: 회원 관련 에러코드
     * 2xxx: 상품 관련 에러코드
     * 3xxx: 주문 관련 에러코드
     * 4xxx: 장바구니 관련 에러코드
     * 5xxx: 공통 에러 처리
     */

    // 회원
    DUPLICATE_USER(1000, HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "존재하지 않은 회원입니다."),
    INVALID_TOKEN(1002, HttpStatus.UNAUTHORIZED, "인증되지 않은 토큰입니다."),
    INVALID_PASSWORD(1003, HttpStatus.UNAUTHORIZED, "비밀번호가 틀립니다."),
    NOT_MATCH_AUTHORITY(1004, HttpStatus.FORBIDDEN, "접근할 수 없는 사용자입니다."),
    PROFILE_NOT_FOUND(1005, HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."),
    INVALID_UUID(1006, HttpStatus.UNAUTHORIZED, "이메일 인증 정보가 틀렸습니다."),

    // 상품
    NOT_FOUND_CATEGORY(2000, HttpStatus.NOT_FOUND, "찾을 수 없는 카테고리 입니다."),
    DUPLICATE_PRODUCT(2001, HttpStatus.CONFLICT, "이미 존재하는 상품입니다."),
    PRODUCT_NOT_FOUND(2002, HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),

    // 주문
    NOT_MATCH_AMOUNT(3000, HttpStatus.BAD_REQUEST, "입금하신 금액이 상품의 가격과 다릅니다."),


    // 장바구니
    CART_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),


    // 공통
    INTERNAL_SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 에러가 발생하였습니다."),




    ;

    private final Integer code;
    private final HttpStatus status;
    private final String message;
}
