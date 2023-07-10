package lk.directpay.Login.model;

import lk.directpay.merchantportal.entities.directpay.MerchantUser;
import lk.directpay.merchantportal.entities.directpay.MerchantUserPermission;
import lk.directpay.merchantportal.entities.directpay.MerchantUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MerchantUserPrinciple implements UserDetails {

    @Autowired
    private final MerchantUser merchant;

    public MerchantUserPrinciple(MerchantUser merchant) {
        this.merchant = merchant;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        for(MerchantUserRole role: merchant.getRoles()) {
            for (MerchantUserPermission permission : role.getPermissions()) {
                authorityList.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return authorityList;
    }

    @Override
    public String getPassword() {
        return merchant.getPassword();
    }

    @Override
    public String getUsername() {
        return merchant.getEmail();
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

    public MerchantUser getMerchantUser() {
        return merchant;
    }
}
