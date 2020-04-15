package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Override
    public User save(RegisterUserWrapper register) {
        if (userRepository.existsByEmail(register.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Username is already exist!");
        }

        if(register.getPassword().equals(register.getConfirmPassword())) {
            User user = new User(
                    register.getEmail(),
                    passwordEncoder.encode(register.getPassword()),
                    register.getEmail(),
                    new Date(),
                    register.getEmail(),
                    new Date()
            );
            user = userRepository.save(user);
            profileRepository.save(new Profile(
                    user, register.getFullname(), register.getDob(), register.getGender(),
                    register.getEmail(),
                    new Date(),
                    register.getEmail(),
                    new Date()));
            return user;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Password and Confirm Password not match");
        }
    }

    @Override
    public boolean login(String email, String password) {
        boolean result = false;
        Optional<User> data = userRepository.findByEmail(email);
        if(data.isPresent()) {
            User user = data.get();
            result = passwordEncoder.matches(password, user.getPasswordHashed());
        }
        return result;
    }
}
