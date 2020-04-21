package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.AuthProvider;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

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
        if(! patternEmail.matcher(register.getEmail()).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Email not valid!");
        }

        //check password empty
        if(register.getPassword() == null || register.getPassword().equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Password cannot empty!");
        }

        //check password pattern
        String regexPassword = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{6,20}$";
        Pattern patternPassword = Pattern.compile(regexPassword);
        if(! patternPassword.matcher(register.getPassword()).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Password pattern not valid!");
        }

        //check password match
        if(! register.getPassword().equals(register.getConfirmPassword())) {
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
        if(Period.between(dob, LocalDate.now()).getYears() < 18) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error: Age should not under 18!");
        }

        //register
        User user = new User();
        user.setEmail(register.getEmail());
        user.setPasswordHashed(passwordEncoder.encode(register.getPassword()));
//        user.setCreatedBy(register.getEmail());
//        user.setCreatedDate(LocalDateTime.now());
//        user.setModifiedBy(register.getEmail());
//        user.setModifiedDate(LocalDateTime.now());
        user.setProvider(AuthProvider.email);
        user = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFullName(register.getFullname());
        profile.setDob(dob);
        profile.setGender(register.getGender());
//        profile.setCreatedBy(register.getEmail());
//        profile.setCreatedDate(LocalDateTime.now());
//        profile.setModifiedBy(register.getEmail());
//        profile.setModifiedDate(LocalDateTime.now());
        profileRepository.save(profile);

        return user;
    }

    @Override
    public boolean login(String email, String password) {
        boolean result = false;
        Optional<User> data = userRepository.findByEmail(email);
        if(data.isPresent()) {
            User user = data.get();
            if(password != null) {
                result = passwordEncoder.matches(password, user.getPasswordHashed());
            }
        }
        return result;
    }
}
