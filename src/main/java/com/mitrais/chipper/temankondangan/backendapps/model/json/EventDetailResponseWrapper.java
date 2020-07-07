package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDetailResponseWrapper {
    private Long eventId;

    private Long creatorUserId;

    private String fullName;

    private String photoProfileUrl;

    private String title;

    private String city;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    private Integer minimumAge;

    private Integer maximumAge;

    private Gender companionGender;

    private String additionalInfo;

    private List<ApplicantResponseWrapper> applicantList;

    private Boolean isCreator;

    private Boolean isApplied;
    
    private ApplicantStatus applicantStatus;
    
    private Boolean hasAcceptedApplicant;
    
    private AcceptedApplicantResponseWrapper acceptedApplicant;

    private Boolean cancelled;

    private Boolean isRated;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDateTime;
}
