package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.mitrais.chipper.temankondangan.backendapps.model.en.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingWrapper {
    RatingType ratingType;
    Long userId;
    Integer score;
}
