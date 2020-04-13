package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.Users;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Override
    public Users save(RegisterUserWrapper register) {
        if(register.getPassword().equals(register.getConfirmPassword())) {
            Users user = new Users(
                    register.getEmail(),
                    passwordEncoder.encode(register.getPassword()),
                    register.getEmail(),
                    new Date(),
                    register.getEmail(),
                    new Date()
            );
            user = userRepository.save(user);
            profileRepository.save(new Profile(user, register.getFullname(), register.getDob(), register.getGender()));
            return user;
        } else {
            return null;
        }
    }
}
