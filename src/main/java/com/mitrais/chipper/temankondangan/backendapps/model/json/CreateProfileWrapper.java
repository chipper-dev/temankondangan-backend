package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CreateProfileWrapper {

    private String fullname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private String dob;
    private Gender gender;
    private String email;
}
