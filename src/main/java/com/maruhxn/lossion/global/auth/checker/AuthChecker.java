package com.maruhxn.lossion.global.auth.checker;

import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthChecker {

    public boolean isSelf(Long memberId) {
        CustomUserDetails principal = getPrincipal();
        return principal.getId().equals(memberId);
    }

    public boolean isVerified() {
        CustomUserDetails principal = getPrincipal();
        if (!principal.isEnabled()) {
            throw new ForbiddenException(ErrorCode.UNVERIFIED_EMAIL);
        }
        return true;
    }

    private static CustomUserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal;
    }

}
