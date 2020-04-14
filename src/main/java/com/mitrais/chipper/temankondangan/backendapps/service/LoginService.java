package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.json.LoginWrapper;

public interface LoginService {
    boolean login(LoginWrapper request);
}
