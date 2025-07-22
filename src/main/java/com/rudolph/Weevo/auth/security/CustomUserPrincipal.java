package com.rudolph.Weevo.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private Long memberId;
    private Collection<? extends GrantedAuthority> authorities;

    //userdetails 필수 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    @Override
    public String getPassword(){
        return null; // 소셜 로그인시 비밀번호 없을 수도 있
    }

    @Override
    public String getUsername(){
        return memberId.toString();
    }

    @Override
    public boolean isAccountNonExpired(){return true;}
    @Override
    public boolean isAccountNonLocked(){return true;}
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }



}
