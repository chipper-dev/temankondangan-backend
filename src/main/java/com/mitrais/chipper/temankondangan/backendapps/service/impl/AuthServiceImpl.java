package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.config.FirebaseConfig;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.VerificationCode;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.VerificationCodeRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.AuthService;
import com.mitrais.chipper.temankondangan.backendapps.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {
    private final static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    SimpleMailMessage template;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, ProfileRepository profileRepository, EmailService emailService, SimpleMailMessage template, VerificationCodeRepository verificationCodeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public User save(RegisterUserWrapper register) {
        //check email exist
        if (userRepository.existsByEmail(register.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Email is already exist!");
        }

        //check email format valid
        String regexEmail = "^(.+)@(.+)\\.(.+)$";
        Pattern patternEmail = Pattern.compile(regexEmail);
        if (!patternEmail.matcher(register.getEmail()).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Email not valid!");
        }

        //check password empty
        if (register.getPassword() == null || register.getPassword().equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Password cannot empty!");
        }

        //check password pattern
        String regexPassword = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{6,20}$";
        Pattern patternPassword = Pattern.compile(regexPassword);
        if (!patternPassword.matcher(register.getPassword()).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Password pattern not valid!");
        }

        //check password match
        if (!register.getPassword().equals(register.getConfirmPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Password and Confirm Password not match!");
        }


        //check dob valid
        LocalDate dob;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
        try {
            dob = LocalDate.parse(register.getDob(), df);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Date not valid!");
        }

        //check age over 18
        if (Period.between(dob, LocalDate.now()).getYears() < 18) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Age should not under 18!");
        }

        //register
        User user = new User();
        user.setEmail(register.getEmail());
        user.setPasswordHashed(passwordEncoder.encode(register.getPassword()));
        user.setProvider(AuthProvider.email);
        user.setDataState(DataState.ACTIVE);
        user = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFullName(register.getFullname());
        profile.setDob(dob);
        profile.setGender(register.getGender());
        profileRepository.save(profile);

        return user;
    }

    @Override
    public boolean login(String email, String password) {
        boolean result = false;
        Optional<User> data = userRepository.findByEmail(email);
        if (data.isPresent()) {
            User user = data.get();
            if (password != null) {
                result = passwordEncoder.matches(password, user.getPasswordHashed());
            }
        }
        return result;
    }

    @Override
    public boolean logout(Long userId) {
        boolean result = false;
        Optional<User> data = userRepository.findById(userId);
        if (data.isPresent()) {
            User user = data.get();
            user.setLogout(new Date());
            userRepository.save(user);
            result = true;
        }
        return result;
    }
}
