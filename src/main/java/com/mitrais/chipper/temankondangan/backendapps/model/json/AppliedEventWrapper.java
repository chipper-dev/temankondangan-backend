package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppliedEventWrapper {
    private Long eventId;
    private String photoProfileUrl;
    private String creatorFullName;
    private String title;
    private String city;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;
    private ApplicantStatus applicantStatus;
    private Gender creatorGender;
    private Boolean cancelled;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime appliedDateTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDateTime;
}
