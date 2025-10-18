package org.example.emailverification.service;

import org.example.emailverification.entity.Token;
import org.example.emailverification.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Optional<Token> findByToken(String token){
        return tokenRepository.findByToken(token);
    }
    public Token save(Token token){
        return tokenRepository.save(token);
    }
}
