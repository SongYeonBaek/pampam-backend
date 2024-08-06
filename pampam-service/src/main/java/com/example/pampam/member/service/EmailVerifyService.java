package com.example.pampam.member.service;


import com.example.pampam.exception.EcommerceApplicationException;
import com.example.pampam.exception.ErrorCode;
import com.example.pampam.member.model.entity.Consumer;
import com.example.pampam.member.model.entity.EmailVerify;
import com.example.pampam.member.model.entity.Seller;
import com.example.pampam.member.model.request.GetEmailConfirmReq;
import com.example.pampam.member.model.request.SendEmailReq;
import com.example.pampam.member.repository.ConsumerRepository;
import com.example.pampam.member.repository.EmailVerifyRepository;
import com.example.pampam.member.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerifyService {
    private final EmailVerifyRepository emailVerifyRepository;
    private final ConsumerRepository memberRepository;
    private final SellerRepository sellerRepository;
    private final JavaMailSender emailSender;

    public void sendEmail(SendEmailReq sendEmailReq) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendEmailReq.getEmail());
        message.setSubject("로컬푸드 pampam 이메일 인증");
        String uuid = UUID.randomUUID().toString();
        message.setText("http://localhost:8080/member/confirm?email="
                + sendEmailReq.getEmail()
                + "&authority=" + sendEmailReq.getAuthority()
                + "&token=" + uuid
                + "&jwt=" + sendEmailReq.getAccessToken()
        );
        emailSender.send(message);
        create(sendEmailReq.getEmail(), uuid);
    }

    public RedirectView verify(GetEmailConfirmReq getEmailConfirmReq) {
        Optional<EmailVerify> emailVerify = emailVerifyRepository.findByEmail(getEmailConfirmReq.getEmail());
        if(emailVerify.isPresent()){
            EmailVerify emailVerifyInfo = emailVerify.get();
            if(emailVerifyInfo.getUuid().equals(getEmailConfirmReq.getToken())) {
                update(getEmailConfirmReq.getEmail(), getEmailConfirmReq.getAuthority());
                return new RedirectView("http://localhost:3000/emailconfirm/" + getEmailConfirmReq.getJwt());
            } else {
                throw new EcommerceApplicationException(ErrorCode.INVALID_UUID);
            }
        }
        return new RedirectView("http://localhost:3000/emailCertError");
    }

    private void update(String email, String authority) {
        try {
            if (authority.equals("CONSUMER")){
                Optional<Consumer> consumer = memberRepository.findByEmail(email);
                if(consumer.isPresent()) {
                    Consumer consumerInfo = consumer.get();
                    consumerInfo.setStatus(true);
                    memberRepository.save(consumerInfo);
                } else {
                    throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
                }
            } else if (authority.equals("SELLER")){
                Optional<Seller> seller = sellerRepository.findByEmail(email);
                if(seller.isPresent()) {
                    Seller sellerInfo = seller.get();
                    sellerInfo.setStatus(true);
                    sellerRepository.save(sellerInfo);
                } else {
                    throw new EcommerceApplicationException(ErrorCode.USER_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            throw new EcommerceApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    private void create(String email, String uuid) {
        EmailVerify emailVerify = EmailVerify.buildEmailVerify(email, uuid);
        emailVerifyRepository.save(emailVerify);
    }
}
