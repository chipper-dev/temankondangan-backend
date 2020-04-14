package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.json.LoginWrapper;
import com.mitrais.chipper.temankondangan.backendapps.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public boolean login(LoginWrapper request) {
        return false;
    }
}
