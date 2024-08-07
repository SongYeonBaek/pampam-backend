package com.example.pampam.member.service;

import com.example.pampam.common.BaseResponse;
import com.example.pampam.exception.EcommerceApplicationException;
import com.example.pampam.exception.ErrorCode;
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
        try {
            if (consumerRepository.findByEmail(consumerSignupReq.getEmail()).isPresent()) {
                throw new EcommerceApplicationException(ErrorCode.DUPLICATE_USER);
            }

            Consumer consumer = consumerRepository.save(Consumer.buildConsumer(consumerSignupReq, passwordEncoder.encode(consumerSignupReq.getConsumerPW())));

            if (profileImage != null) {
                profileImageService.saveProfileImage(profileImage, consumer);
            }

            String accessToken = JwtUtils.generateAccessToken(consumer, secretKey, expiredTimeMs);

            SendEmailReq sendEmailReq = SendEmailReq.buildSendEmailReq(consumer, accessToken);
            emailVerifyService.sendEmail(sendEmailReq);

            Optional<Consumer> result = consumerRepository.findByEmail(consumer.getEmail());

            if (result.isPresent()) {
                consumer = result.get();
            } else {
                throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
            }

            return BaseResponse.successResponse("회원가입에 성공하였습니다.", ConsumerSignupRes.buildConsumerSignupRes(consumer));
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public BaseResponse<SellerSignupRes> sellerSignup(SellerSignupReq sellerSignupReq, MultipartFile image) {
        try {
            if (sellerRepository.findByEmail(sellerSignupReq.getEmail()).isPresent()) {
                throw new EcommerceApplicationException(ErrorCode.DUPLICATE_USER);
            }

            String saveFileName = profileImageService.saveProfileImage(image);
            sellerSignupReq.setSellerPW(passwordEncoder.encode(sellerSignupReq.getSellerPW()));

            Seller seller = sellerRepository.save(Seller.buildSeller(sellerSignupReq, saveFileName));
            String accessToken = JwtUtils.generateAccessToken(seller, secretKey, expiredTimeMs);

            SendEmailReq sendEmailReq = SendEmailReq.buildSendEmailReq(seller, accessToken);
            emailVerifyService.sendEmail(sendEmailReq);

            return BaseResponse.successResponse("회원가입에 성공하였습니다.", SellerSignupRes.buildSellerSignupRes(seller));
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse<ConsumerLoginRes> consumerLogin(ConsumerLoginReq consumerLoginReq) {
        try {
            Optional<Consumer> consumer = consumerRepository.findByEmail(consumerLoginReq.getEmail());
            if (consumer.isPresent()) {
                if (passwordEncoder.matches(consumerLoginReq.getPassword(), consumer.get().getPassword())) {
                    ConsumerLoginRes consumerLoginRes = ConsumerLoginRes.buildConsumerLoginRes(consumer.get(), secretKey, expiredTimeMs);
                    return BaseResponse.successResponse("로그인에 성공하였습니다.", consumerLoginRes);
                } else {
                    throw new EcommerceApplicationException(ErrorCode.INVALID_PASSWORD);
                }
            } else {
                throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse<SellerLoginRes> sellerLogin(SellerLoginReq sellerLoginReq) {
        try {
            Optional<Seller> seller = sellerRepository.findByEmail(sellerLoginReq.getEmail());
            if (seller.isPresent()) {
                if (passwordEncoder.matches(sellerLoginReq.getPassword(), seller.get().getPassword())) {
                    SellerLoginRes sellerLoginRes = SellerLoginRes.buildSellerLoginRes(seller.get(), secretKey, expiredTimeMs);
                    return  BaseResponse.successResponse("로그인에 성공하였습니다.",sellerLoginRes);
                }else {
                    throw new EcommerceApplicationException(ErrorCode.INVALID_PASSWORD);
                }
            }
            throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse<ConsumerUpdateRes> consumerUpdate(ConsumerUpdateReq consumerUpdateReq) {
        try {
            Optional<Consumer> consumer = consumerRepository.findByEmail(consumerUpdateReq.getEmail());
            if (consumer.isPresent()) {
                consumerUpdateReq.setConsumerPW(passwordEncoder.encode(consumerUpdateReq.getConsumerPW()));
                Consumer updatedConsumer = consumerRepository.save(Consumer.buildConsumerUpdate(consumer.get(), consumerUpdateReq));
                return BaseResponse.successResponse("회원정보 수정을 완료하였습니다.", ConsumerUpdateRes.buildConsumerUpdateRes(updatedConsumer));
            } else {
                throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse<SellerUpdateRes> sellerUpdate(SellerUpdateReq sellerUpdateReq) {
        try {
            Optional<Seller> seller = sellerRepository.findByEmail(sellerUpdateReq.getEmail());
            if (seller.isPresent()) {
                sellerUpdateReq.setSellerPW(passwordEncoder.encode(sellerUpdateReq.getSellerPW()));
                Seller updatedSeller = sellerRepository.save(Seller.buildSellerUpdate(seller.get(), sellerUpdateReq));
                return BaseResponse.successResponse("회원정보 수정을 완료하였습니다.", SellerUpdateRes.buildSellerUpdateRes(updatedSeller));
            } else {
                throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse<ConsumerDeleteRes> consumerDelete(ConsumerDeleteReq consumerDeleteReq) {
        try {
            Optional<Consumer> consumer = consumerRepository.findByEmail(consumerDeleteReq.getEmail());
            if (consumer.isPresent()) {
                consumerRepository.delete(consumer.get());
                return BaseResponse.successResponse("회원 탈퇴가 정상적으로 처리되었습니다.", ConsumerDeleteRes.buildConsumerDeleteRes(consumerDeleteReq));
            }
            throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    public BaseResponse<SellerDeleteRes> sellerDelete(SellerDeleteReq sellerDeleteReq) {
        try {
            Optional<Seller> seller = sellerRepository.findByEmail(sellerDeleteReq.getEmail());
            if (seller.isPresent()) {
                sellerRepository.delete(seller.get());
                return BaseResponse.successResponse("회원 탈퇴가 정상적으로 처리되었습니다.", SellerDeleteRes.buildSellerDeleteRes(sellerDeleteReq));
            }
            throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse<GetProfileImageRes> getConsumerProfileImage(String email) {
        try {
            String imageAddr = profileImageService.findProfileImage(email);
            return BaseResponse.successResponse("프로필 조회를 성공하였습니다.", GetProfileImageRes.buildProfileImageRes(imageAddr));
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public Consumer getMemberByConsumerID(String email) {
        try {
            Optional<Consumer> consumer = consumerRepository.findByEmail(email);
            if(consumer.isPresent()){
                return consumer.get();
            } else {
                throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Consumer> result = consumerRepository.findByEmail(email);
        Consumer member = null;
        if (result.isPresent()) member = result.get();
        return member;
    }
}
