package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.util.HashMap;

@Data
@Builder
@ApiModel(description = "All details about Profile. ")
public class ProfileResponseWrapper {

    @ApiModelProperty(notes = "Profile DB id")
    private Long profileId;

    @ApiModelProperty(notes = "Profile full name")
    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(notes = "Profile birth of date")
    private LocalDate dob;

    @ApiModelProperty(notes = "Profile gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ApiModelProperty(notes = "Profile photo profile data byte")
    private String photoProfileUrl;

    @ApiModelProperty(notes = "Profile city")
    private String city;

    @ApiModelProperty(notes = "Profile about me")
    private String aboutMe;

    @ApiModelProperty(notes = "Profile interest")
    private String interest;
    
    @ApiModelProperty(notes = "User profile email")
    private String email;
    
    @ApiModelProperty(notes = "User profile password null or not")
    private boolean hasPassword;

    @ApiModelProperty(notes = "Rating")
    private HashMap<String, Double> ratingData;
}
