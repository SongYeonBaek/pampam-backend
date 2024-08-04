package com.example.pampam.member.service;

import com.example.pampam.common.BaseResponse;
import com.example.pampam.member.model.entity.Consumer;
import com.example.pampam.member.model.entity.Seller;
import com.example.pampam.member.model.request.*;
import com.example.pampam.member.model.response.*;
import com.example.pampam.member.repository.ConsumerRepository;
import com.example.pampam.member.repository.SellerRepository;
import com.example.pampam.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    private final ConsumerRepository consumerRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerifyService emailVerifyService;
    private final ProfileImageService profileImageService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Integer expiredTimeMs;


    @Transactional
    public BaseResponse<ConsumerSignupRes> consumerSignup(ConsumerSignupReq consumerSignupReq, MultipartFile profileImage) {
        if (consumerRepository.findByEmail(consumerSignupReq.getEmail()).isPresent()) {
            return BaseResponse.failResponse(7000, "요청실패");
        }

        Consumer consumer = consumerRepository.save(Consumer.buildConsumer(consumerSignupReq, passwordEncoder.encode(consumerSignupReq.getConsumerPW())));

        if (profileImage != null) {
            profileImageService.saveProfileImage(profileImage, consumer);
        }

        String accessToken = JwtUtils.generateAccessToken(consumer, secretKey, expiredTimeMs);

        SendEmailReq sendEmailReq = SendEmailReq.buildSendEmailReq(consumer, accessToken);
        emailVerifyService.sendEmail(sendEmailReq);

        Optional<Consumer> result = consumerRepository.findByEmail(consumer.getEmail());

        if (result.isPresent()){
            consumer = result.get();
        }

        return BaseResponse.successResponse("요청성공",ConsumerSignupRes.buildConsumerSignupRes(consumer));

    }
    public BaseResponse<SellerSignupRes> sellerSignup(SellerSignupReq sellerSignupReq, MultipartFile image) {

        if (sellerRepository.findByEmail(sellerSignupReq.getEmail()).isPresent()) {
            return BaseResponse.failResponse(7000, "요청실패");
        }

        String saveFileName = profileImageService.saveProfileImage(image);
        sellerSignupReq.setSellerPW(passwordEncoder.encode(sellerSignupReq.getSellerPW()));

        Seller seller = sellerRepository.save(Seller.buildSeller(sellerSignupReq, saveFileName));
        String accessToken = JwtUtils.generateAccessToken(seller, secretKey, expiredTimeMs);

        SendEmailReq sendEmailReq = SendEmailReq.buildSendEmailReq(seller, accessToken);
        emailVerifyService.sendEmail(sendEmailReq);

        return BaseResponse.successResponse("요청성공", SellerSignupRes.buildSellerSignupRes(seller));
    }

    public BaseResponse<ConsumerLoginRes> consumerLogin(ConsumerLoginReq consumerLoginReq) {
        Optional<Consumer> consumer = consumerRepository.findByEmail(consumerLoginReq.getEmail());
        if (consumer.isPresent()) {
            if (passwordEncoder.matches(consumerLoginReq.getPassword(), consumer.get().getPassword())) {
                ConsumerLoginRes consumerLoginRes = ConsumerLoginRes.buildConsumerLoginRes(consumer.get(), secretKey, expiredTimeMs);
                return BaseResponse.successResponse("요청성공", consumerLoginRes);
            }else {
                return BaseResponse.failResponse(7000, "요청실패");
            }
        }
        return BaseResponse.failResponse(7000, "요청실패");
    }

    public BaseResponse<SellerLoginRes> sellerLogin(SellerLoginReq sellerLoginReq) {
        Optional<Seller> seller = sellerRepository.findByEmail(sellerLoginReq.getEmail());
        if (seller.isPresent()) {
            if (passwordEncoder.matches(sellerLoginReq.getPassword(), seller.get().getPassword())) {
                SellerLoginRes sellerLoginRes = SellerLoginRes.buildSellerLoginRes(seller.get(), secretKey, expiredTimeMs);
                return  BaseResponse.successResponse("요청성공",sellerLoginRes);
            }else {
                return BaseResponse.failResponse(7000, "요청실패");
            }
        }
        return BaseResponse.failResponse(7000, "요청실패");
    }

    public BaseResponse<ConsumerUpdateRes> consumerUpdate(ConsumerUpdateReq consumerUpdateReq) {
        Optional<Consumer> result = consumerRepository.findByEmail(consumerUpdateReq.getEmail());
        Consumer consumer = null;
        if (result.isPresent()) {
            consumer = result.get();
            consumerUpdateReq.setConsumerPW(passwordEncoder.encode(consumerUpdateReq.getConsumerPW()));
            consumer = Consumer.buildConsumerUpdate(consumer, consumerUpdateReq);
            consumerRepository.save(consumer);

            return BaseResponse.successResponse("요청성공", ConsumerUpdateRes.buildConsumerUpdateRes(consumer));
        } else {
            return BaseResponse.failResponse(7000, "요청실패");
        }
    }

    public BaseResponse<SellerUpdateRes> sellerUpdate(SellerUpdateReq sellerUpdateReq) {
        Optional<Seller> result = sellerRepository.findByEmail(sellerUpdateReq.getEmail());
        Seller seller = null;

        if (result.isPresent()) {
            seller = result.get();
            sellerUpdateReq.setSellerPW(passwordEncoder.encode(sellerUpdateReq.getSellerPW()));
            seller = Seller.buildSellerUpdate(seller, sellerUpdateReq);
            sellerRepository.save(seller);

            return BaseResponse.successResponse("요청성공", SellerUpdateRes.buildSellerUpdateRes(seller));

        } else {
            return null;
        }

    }

    public BaseResponse<ConsumerDeleteRes> consumerDelete(ConsumerDeleteReq consumerDeleteReq) {
        Optional<Consumer> result = consumerRepository.findByEmail(consumerDeleteReq.getEmail());
        if (result.isPresent()) {
            consumerRepository.delete(result.get());
            return BaseResponse.successResponse("요청성공", ConsumerDeleteRes.buildConsumerDeleteRes(consumerDeleteReq));
        }
        return BaseResponse.failResponse(7000, "요청실패");

    }
    public BaseResponse<SellerDeleteRes> sellerDelete(SellerDeleteReq sellerDeleteReq) {
        Optional<Seller> result = sellerRepository.findByEmail(sellerDeleteReq.getEmail());
        if (result.isPresent()) {
            sellerRepository.delete(result.get());
            return BaseResponse.successResponse("요청성공", SellerDeleteRes.buildSellerDeleteRes(sellerDeleteReq));
        }
        return BaseResponse.failResponse(7000, "요청실패");

    }

    public BaseResponse<GetProfileImageRes> getConsumerProfileImage(String email) {
        String imageAddr = profileImageService.findProfileImage(email);
        return BaseResponse.successResponse("요청 성공", GetProfileImageRes.buildProfileImageRes(imageAddr));
    }

    public Consumer getMemberByConsumerID(String email) {
        Optional<Consumer> result = consumerRepository.findByEmail(email);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Consumer> result = consumerRepository.findByEmail(email);
        Consumer member = null;
        if (result.isPresent())
            member = result.get();

        return member;
    }
}
