package com.mitrais.chipper.temankondangan.backendapps.controller.internal;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/{id}")
public class UserInternalController {

    @Autowired
    UserService userService;

    public User getUser(@PathVariable("id") Long id) {
        return userService.findById(id);
    }
}
