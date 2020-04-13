package com.mitrais.chipper.temankondangan.backendapps.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegisterUserWrapper {

    private String fullname;
    private Date dob;
    private String gender;
    private String email;
    private String password;
    private String confirmPassword;
}
