package com.expensemanager.security;

import com.expensemanager.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * The JwtAuthFilter puts the actual User entity (not just a username string)
 * as the Authentication principal, so this just reads it back out.
 * Centralizing this avoids every controller casting the principal itself.
 */
@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
