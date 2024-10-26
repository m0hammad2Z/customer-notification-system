package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.User;
import com.digitinarytask.customer.domain.enumeration.UserRole;
import com.digitinarytask.customer.dto.domain.CreateUserRequestDTO;
import com.digitinarytask.customer.exception.UserAlreadyExistsException;
import com.digitinarytask.customer.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service("userService")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initializes the service by creating a default admin user.
     */
    @PostConstruct
    public void init() {
        log.info("Creating default admin user");
        createUser(CreateUserRequestDTO.builder()
            .username("admin")
            .password("admin")
            .firstName("Admin")
            .lastName("Admin")
            .role(UserRole.ROLE_ADMIN)
            .email("test@test.com").build());
    }

    /**
     * Loads a user by username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return createUserDetails(user);
    }

    /**
     * Creates user details from a user entity.
     */
    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name().substring(5))
            .build();
    }

    /**
     * Creates a new user.
     */
    public User createUser(CreateUserRequestDTO request) {
        validateNewUser(request);

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .role(request.getRole())
            .build();

        return userRepository.save(user);
    }

    /**
     * Validates a new user request.
     */
    private void validateNewUser(CreateUserRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
    }
}
