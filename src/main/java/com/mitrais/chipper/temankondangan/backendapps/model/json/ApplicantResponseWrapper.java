package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantResponseWrapper {
    private Long applicantId;
    private Long userId;
    private String fullName;
    private ApplicantStatus status;
    private boolean isRated;
}
