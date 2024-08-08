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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

    //공동 구매 체결 후 발송되는 이메일
    // 공동구매 체결 알림 이메일 전송
    public void sendSuccessEmail(SendEmailReq sendEmailReq) {
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>공동구매 체결 알림</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #4CAF50; color: white; padding: 10px 0; text-align: center; border-radius: 8px 8px 0 0; }" +
                ".content { padding: 20px; background-color: #f4f4f4; border-radius: 0 0 8px 8px; }" +
                ".footer { text-align: center; font-size: 12px; color: #888; padding: 10px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<h1>공동구매 체결 알림</h1>" +
                "</div>" +
                "<div class=\"content\">" +
                "<h2>안녕하세요!</h2>" +
                "<p>공동구매가 성공적으로 체결되었습니다.</p>" +
                "<p>아래는 공동구매의 세부 사항입니다:</p>" +
                "<ul>" +
                "<li><strong>구매자 이메일:</strong> " + sendEmailReq.getEmail() + "</li>" +
                "</ul>" +
                "<p>추가적인 정보가 필요하시면 저희에게 문의해 주세요.</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>감사합니다,<br>로컬푸드 Pampam 팀</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendHtmlEmail(sendEmailReq.getEmail(), "공동구매 체결 알림", htmlContent);
    }

    // 환불 알림 이메일 전송
    public void sendRefundEmail(SendEmailReq sendEmailReq) {
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>환불 알림</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #f44336; color: white; padding: 10px 0; text-align: center; border-radius: 8px 8px 0 0; }" +
                ".content { padding: 20px; background-color: #f4f4f4; border-radius: 0 0 8px 8px; }" +
                ".footer { text-align: center; font-size: 12px; color: #888; padding: 10px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<h1>환불 알림</h1>" +
                "</div>" +
                "<div class=\"content\">" +
                "<h2>안녕하세요!</h2>" +
                "<p>공동구매가 체결되지 않아 환불이 진행되었습니다.</p>" +
                "<p>아래는 환불의 세부 사항입니다:</p>" +
                "<ul>" +
                "<li><strong>구매자 이메일:</strong> " + sendEmailReq.getEmail() + "</li>" +
                "</ul>" +
                "<p>추가적인 정보가 필요하시면 저희에게 문의해 주세요.</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>감사합니다,<br>로컬푸드 Pampam 팀</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendHtmlEmail(sendEmailReq.getEmail(), "환불 알림", htmlContent);
    }

    // HTML 이메일 전송 메서드
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Set to true for HTML content
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
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
