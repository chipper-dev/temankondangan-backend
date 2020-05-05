package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.VerificationCodeRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.AuthService;
import com.mitrais.chipper.temankondangan.backendapps.service.EmailService;

@Service
public class AuthServiceImpl implements AuthService {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    SimpleMailMessage template;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           ProfileRepository profileRepository, EmailService emailService, SimpleMailMessage template,
                           VerificationCodeRepository verificationCodeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public User save(RegisterUserWrapper register) {
        // check email exist
        if (userRepository.existsByEmail(register.getEmail())) {
            throw new BadRequestException("Error: Email is already exist!");
        }

        // check email format valid
        String regexEmail = "^(.+)@(.+)\\.(.+)$";
        Pattern patternEmail = Pattern.compile(regexEmail);
        if (!patternEmail.matcher(register.getEmail()).matches()) {
            throw new BadRequestException("Error: Email not valid!");
        }

        // check password empty
        if (register.getPassword() == null || register.getPassword().equals("")) {
            throw new BadRequestException("Error: Password cannot empty!");
        }

        // check password pattern
        String regexPass = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{6,20}$";
        Pattern patternPassword = Pattern.compile(regexPass);
        if (!patternPassword.matcher(register.getPassword()).matches()) {
            throw new BadRequestException("Error: Password pattern not valid!");
        }

        // check password match
        if (!register.getPassword().equals(register.getConfirmPassword())) {
            throw new BadRequestException("Error: Password and Confirm Password not match!");
        }

        // check dob valid
        LocalDate dob;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
        try {
            dob = LocalDate.parse(register.getDob(), df);
        } catch (Exception e) {
            throw new BadRequestException("Error: Date not valid!");
        }

        // check age over 18
        if (Period.between(dob, LocalDate.now()).getYears() < 18) {
            throw new BadRequestException("Error: Age should not under 18!");
        }

        // register
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
        boolean result;
        User data = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Error: Email not found"));

        if (password != null) {
            result = passwordEncoder.matches(password, data.getPasswordHashed());
        } else {
            throw new BadRequestException("Error: Password cannot be empty");
        }

        if(!result) {
            throw new UnauthorizedException("Error: Username or password not match");
        }
        return true;
    }

    @Override
    public boolean logout(Long userId) {
        User data = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Error: User Not Found"));

        data.setLogout(new Date());
        userRepository.save(data);

        return true;
    }
}
