package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;

public interface RegisterService {
    User save(RegisterUserWrapper register);
}
