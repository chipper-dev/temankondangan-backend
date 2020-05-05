package com.mitrais.chipper.temankondangan.backendapps.service;


import com.mitrais.chipper.temankondangan.backendapps.model.json.OauthResponseWrapper;

public interface OAuthService {
    OauthResponseWrapper getToken(String email, String uid);
}
