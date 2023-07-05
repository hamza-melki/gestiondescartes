package com.securityEcommerce.service.Imp;


import com.securityEcommerce.models.User;
import com.securityEcommerce.repository.RefreshTokenRepository;
import com.securityEcommerce.repository.UserRepository;
import com.securityEcommerce.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;


    @Override
    public User CreateUser(User user) {

        return userRepository.save(user);
    }

    @Override
    public User UpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> AllUser() {

        return userRepository.findAll();
    }

    @Override
    public User GetbyId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void DeleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("username");
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Long userId = optionalUser.get().getId();
        System.out.println("id");
        System.out.println(userId);
        System.out.println("testauthentification");
        System.out.println(username);
        return optionalUser.orElse(null);
    }
    @Override
    public boolean isUserIdMatchingToken(Long userId, String token) {
        Long tokenUserId = refreshTokenRepository.findUserIdByToken(token);
        return tokenUserId != null && tokenUserId.equals(userId);
    }
}

