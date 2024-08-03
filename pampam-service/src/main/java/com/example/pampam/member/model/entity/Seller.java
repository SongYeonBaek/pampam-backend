package com.example.pampam.member.model.entity;

import com.example.pampam.member.model.request.SellerSignupReq;
import com.example.pampam.member.model.request.SellerUpdateReq;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerIdx;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String sellerPW;
    @Column(nullable = false)
    private String sellerName;
    @Column(nullable = false)
    private String sellerAddr;
    @Column(nullable = false)
    private String sellerPhoneNum;
    @Column(nullable = false)
    private String sellerBusinessNumber;
    private String authority;
    private Boolean status;         // email 인증 여부
    @Column(length = 200)
    private String image;

    public static Seller buildSeller(SellerSignupReq sellerSignupReq, String saveFileName) {
        return Seller.builder()
                .email(sellerSignupReq.getEmail())
                .sellerPW(sellerSignupReq.getSellerPW())
                .sellerName(sellerSignupReq.getSellerName())
                .sellerAddr(sellerSignupReq.getSellerAddr())
                .sellerPhoneNum(sellerSignupReq.getSellerPhoneNum())
                .sellerBusinessNumber(sellerSignupReq.getSellerBusinessNumber())
                .authority("SELLER")
                .status(false)
                .image(saveFileName)
                .build();
    }

    public static Seller buildSellerUpdate(Seller seller, SellerUpdateReq sellerUpdateReq) {
       return Seller.builder()
               .sellerIdx(seller.getSellerIdx())
               .email(sellerUpdateReq.getEmail())
               .sellerName(sellerUpdateReq.getSellerName())
               .sellerAddr(sellerUpdateReq.getSellerAddr())
               .sellerPhoneNum(sellerUpdateReq.getSellerPhoneNum())
               .authority(seller.getAuthority())
               .sellerBusinessNumber(sellerUpdateReq.getSellerBusinessNumber())
               .status(seller.getStatus())
               .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton((GrantedAuthority) () -> authority);
    }

    @Override
    public String getPassword() {
        return sellerPW;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
