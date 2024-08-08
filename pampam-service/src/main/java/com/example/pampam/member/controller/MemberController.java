package com.example.pampam.member.controller;

import com.example.pampam.common.BaseResponse;
import com.example.pampam.member.model.entity.Consumer;
import com.example.pampam.member.model.request.*;
import com.example.pampam.member.model.response.ConsumerDeleteRes;
import com.example.pampam.member.service.EmailVerifyService;
import com.example.pampam.member.service.KakaoService;
import com.example.pampam.member.service.MemberService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Api(value = "회원 컨트롤러 v1", tags = "회원 API")
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MemberController {
    private final MemberService memberService;
    private final EmailVerifyService emailVerifyService;
    private final KakaoService kakaoService;

    @ApiOperation(value = "구매자 회원 가입")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberSignupReq", paramType = "query", value = "등록할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.POST, value = "/consumer/signup")
    public ResponseEntity consumerSignup(@RequestPart ConsumerSignupReq memberSignupReq, @RequestPart MultipartFile profileImage){
        return ResponseEntity.ok().body(memberService.consumerSignup(memberSignupReq, profileImage));
    }

    @ApiOperation(value = "판매자 회원 가입")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerSignupReq", paramType = "query", value = "등록할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.POST, value = "/seller/signup")
    public ResponseEntity sellerSignup(@RequestPart SellerSignupReq sellerSignupReq, @RequestPart MultipartFile profileImage){
        return ResponseEntity.ok().body(memberService.sellerSignup(sellerSignupReq,profileImage));
    }

    @ApiOperation(value = "구매자 로그인")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consumerLoginReq", paramType = "query", value = "로그인할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.POST, value = "/consumer/login")
    public ResponseEntity memberLogin(@RequestBody ConsumerLoginReq consumerLoginReq){
        return ResponseEntity.ok().body(memberService.consumerLogin(consumerLoginReq));
    }

    @ApiOperation(value = "판매자 로그인")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerLoginReq", paramType = "query", value = "로그인할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.POST, value = "/seller/login")
    public ResponseEntity sellerLogin(@RequestBody SellerLoginReq sellerLoginReq){
        return ResponseEntity.ok().body(memberService.sellerLogin(sellerLoginReq));
    }

    @ApiOperation(value = "구매자 회원 정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consumerUpdateReq", paramType = "query", value = "수정할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/consumer/update")
    public ResponseEntity consumerUpdate(@RequestBody ConsumerUpdateReq consumerUpdateReq){
        return ResponseEntity.ok().body(memberService.consumerUpdate(consumerUpdateReq));
    }

    @ApiOperation(value = "판매자 회원 정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerUpdateReq", paramType = "query", value = "수정할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/seller/update")
    public ResponseEntity sellerUpdate(@RequestBody SellerUpdateReq sellerUpdateReq){
        return ResponseEntity.ok().body(memberService.sellerUpdate(sellerUpdateReq));
    }

    @ApiOperation(value = "판매자 회원 탈퇴")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consumerDeleteReq", paramType = "query", value = "탈퇴할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/consumer/delete")
    public ResponseEntity<BaseResponse<ConsumerDeleteRes>> consumerDelete(@RequestBody ConsumerDeleteReq consumerDeleteReq){
        return ResponseEntity.ok().body(memberService.consumerDelete(consumerDeleteReq));
    }

    @ApiOperation(value = "구매자 회원 탈퇴")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerDeleteReq", paramType = "query", value = "탈퇴할 회원 정보", required = true)
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/seller/delete")
    public ResponseEntity sellerDelete(@RequestBody SellerDeleteReq sellerDeleteReq){
        return ResponseEntity.ok().body(memberService.sellerDelete(sellerDeleteReq));
    }

    @RequestMapping(method = RequestMethod.GET,value = "confirm")
    public RedirectView confirm(GetEmailConfirmReq getEmailConfirmReq){
        return emailVerifyService.verify(getEmailConfirmReq);

    }

    @ApiOperation(value = "구매자 프로필 이미지 불러오기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", paramType = "query", value = "프로필 이미지를 불러올 회원 이메일", required = true)
    })
    @RequestMapping(method = RequestMethod.GET, value = "/profile")
    public ResponseEntity getProfileImage(String email) {
        return ResponseEntity.ok().body(memberService.getConsumerProfileImage(email));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/kakao")
    // 인가 코드 받아오는 코드
    public ResponseEntity kakao(String code) {
        System.out.println(code);
        String accessToken = kakaoService.getKakaoToken(code);
        KakaoEmailReq kakaoEmailReq = kakaoService.getUserInfo(accessToken);
        Consumer consumer = memberService.getMemberByConsumerID(kakaoEmailReq.getEmail());
        if(consumer == null) {
            consumer = kakaoService.kakaoSignup(kakaoEmailReq);
        }
        return ResponseEntity.ok().body(kakaoService.kakaoLogin(consumer));
    }
}
