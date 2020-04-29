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

    @Value("${app.verificationExpirationMsec}")
    Long expiration;

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    VerificationCodeRepository verificationRepository;
    EmailService emailService;
    SimpleMailMessage template;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, ProfileRepository profileRepository, EmailService emailService, SimpleMailMessage template, VerificationCodeRepository verificationCodeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.emailService = emailService;
        this.template = template;
        this.verificationRepository = verificationCodeRepository;
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

    @Override
    public void forgotPassword(String email) {
        Integer code = generateRandomCode(6);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: Your email is not registered. Please try again"));

        saveCode(user.getEmail(), code);
        sendEmailJob(user.getEmail(), code);
    }

    @Override
    public void resetPassword(ResetPasswordWrapper wrapper) {
        resetPasswordValidation(wrapper);

        VerificationCode verificationCode = verificationRepository.findByCode(wrapper.getVerificationCode())
                .orElseThrow(() -> new RuntimeException("Error: Please input correct verification code from your email"));

        // check verification code is expired?
        if (isCodeValid(verificationCode.getCreatedAt())) {
            User user = userRepository.findByEmail(verificationCode.getEmail())
                    .orElseThrow(() -> new RuntimeException("Error: User not Found"));

            // check the new password can't be same with old password
            if (passwordEncoder.matches(wrapper.getNewPassword(), user.getPasswordHashed()))
                throw new RuntimeException("Error: New password must be different from current password");

            // Update password
            user.setPasswordHashed(passwordEncoder.encode(wrapper.getNewPassword()));
            userRepository.save(user);

            // Delete verification code
            deleteCode(user.getEmail());
        } else {
            //Expired
            throw new RuntimeException("Error: Please input correct verification code from your email");
        }
    }

    private Integer generateRandomCode(Integer n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }

    private void saveCode(String email, Integer code) {
        VerificationCode temp = VerificationCode.builder()
                .code(String.valueOf(code))
                .email(email)
                .createdAt(LocalDateTime.now())
                .build();

        verificationRepository.save(temp);
    }

    private void deleteCode(String email) {
        List<VerificationCode> list = verificationRepository.findByEmail(email);
        if(!list.isEmpty()) {
            list.forEach(verificationCode -> {
                logger.info("Delete code: {}", verificationCode.getCode());
                verificationRepository.delete(verificationCode);
            });
        }
    }

    private void sendEmailJob(String email, Integer code) {
        String subject = "Teman Kondangan - Reset Password";
        String body = String.format(Objects.requireNonNull(template.getText()), email, code);
        emailService.sendMessage(email, subject, body);
    }

    private boolean isCodeValid(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        return duration.getSeconds() * 1000 < expiration;
    }

    private void resetPasswordValidation(ResetPasswordWrapper wrapper) {
        if (StringUtils.isAnyEmpty(wrapper.getVerificationCode(), wrapper.getNewPassword(), wrapper.getConfirmPassword())) {
            throw new RuntimeException("Error: Field can't be blank.");
        }

        if (!wrapper.getNewPassword().equals(wrapper.getConfirmPassword())) {
            throw new RuntimeException("Error: Confirmed password do not match!");
        }

        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (wrapper.getNewPassword().length() < 6 || wrapper.getNewPassword().length() > 20) {
            throw new RuntimeException("Error: Password length must be 6-20 characters!");
        }

        if (!specialCharPatten.matcher(wrapper.getNewPassword()).find()) {
            throw new RuntimeException("Error: Password must have at least one special character!");
        }

        if (!digitCasePatten.matcher(wrapper.getNewPassword()).find()) {
            throw new RuntimeException("Error: Password must have at least one digit character!");
        }
    }
}
