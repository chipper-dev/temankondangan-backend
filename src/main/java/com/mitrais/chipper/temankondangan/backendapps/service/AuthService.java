package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;

public interface AuthService {
    User save(RegisterUserWrapper register);
    boolean login(String email, String password);
    boolean logout(Long userId);
}
