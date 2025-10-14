package org.example.emailverification.service;

import org.example.emailverification.entity.User;
import org.example.emailverification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
    }

    public void registerUser(User user) {
        //check the db if the user with same email address
        // or username already exist

        userRepository.findByUsernameOrEmail(user.getUsername(),user.getEmail())
                .ifPresent(
                        User ->{
                            throw new IllegalStateException("User already exists");
                        }

                );
        // if user with the username or email address does not exist
        // then save the user
        // first encrypt the pwd
        String password = passwordEncoder.encode(user.getPassword());
        userRepository.save(user);
    }
}
