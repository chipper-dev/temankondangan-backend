package com.mitrais.chipper.temankondangan.backendapps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class HomeController {
    @GetMapping("/")
    public String index() { return "redirect:/swagger-ui.html"; }

}
