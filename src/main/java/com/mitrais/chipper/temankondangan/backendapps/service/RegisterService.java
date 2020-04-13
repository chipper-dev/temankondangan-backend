package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Users;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;

public interface RegisterService {
    Users save(RegisterUserWrapper register);
}
