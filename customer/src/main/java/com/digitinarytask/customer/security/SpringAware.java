package com.digitinarytask.customer.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication()).map(authentication -> authentication.getName());
    }
}
