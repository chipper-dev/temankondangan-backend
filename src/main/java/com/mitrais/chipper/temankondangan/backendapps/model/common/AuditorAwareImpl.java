package com.mitrais.chipper.temankondangan.backendapps.model.common;

import com.mitrais.chipper.temankondangan.backendapps.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String currentEmail = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentEmail = ((UserPrincipal) authentication.getPrincipal()).getEmail();
        }

        return Optional.of(currentEmail);
    }

}
