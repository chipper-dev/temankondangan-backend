package com.mitrais.chipper.temankondangan.backendapps.service;

public interface EmailService {
    void sendMessage(String to, String subject, String text);
}
