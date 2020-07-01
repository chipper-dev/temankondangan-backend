package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.mitrais.chipper.temankondangan.backendapps.model.en.RatingType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingWrapper {
    @ApiModelProperty(notes = "Rating Type. APPLICANT if you want give rating to the Accepted Applicant, CREATOR if you want give rating to the Creator of the Event")
    @Enumerated(EnumType.STRING)
    RatingType ratingType;

    @ApiModelProperty(notes = "User ID who will be given the rating")
    Long userId;

    @ApiModelProperty(notes = "Score for the Rating, 1~5")
    Object score;
}
