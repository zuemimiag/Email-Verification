package org.example.emailverification.service;

import org.example.emailverification.entity.Token;
import org.example.emailverification.entity.User;
import org.example.emailverification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private final EmailService emailService;

    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailService = emailService;
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
        user.setPassword(password);
        userRepository.save(user);

        //send an email with validation link with token
        //user click the link to confirm
        //after confirmation the user becomes enabled

        Token confirmationToken = new Token(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        tokenService.save(confirmationToken);
        //Todo: send email
        emailService.send(user.getEmail(),confirmationToken.getToken());
    }

    public void confirmToken(String token) {
        // 1. check if the token exist
        Token confirmedToken = tokenService.findByToken(token)
                .orElseThrow(
                        ()-> new IllegalStateException("Invalid token")
                );
        //2. check if user already verified
        if(confirmedToken.getConfirmedAt()!=null){
            throw new IllegalStateException("User already verified");
        }
        //3. check if token expired
        LocalDateTime expiresAt = confirmedToken.getExpiresAt();
        if(expiresAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token expired");
        }

        // if everything is ok then update the confirmation time
        confirmedToken.setConfirmedAt(LocalDateTime.now());
        tokenService.save(confirmedToken);
        //enable the user
        enableUser(confirmedToken.getUser());

    }

    private void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
}
