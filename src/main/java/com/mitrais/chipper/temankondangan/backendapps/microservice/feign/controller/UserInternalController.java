package com.mitrais.chipper.temankondangan.backendapps.microservice.feign.controller;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/legacy/user")
public class UserInternalController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.findById(id);
    }
}
