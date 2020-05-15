package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFindAllListResponseWrapper {
	private Long eventId;
	private String creatorFullName;
	private String createdBy;
	private String photoProfileUrl;
	private String title;
	private String city;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
	private LocalDateTime startDateTime;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
	private LocalDateTime finishDateTime;
	private Integer minimumAge;
	private Integer maximumAge;
	private Gender creatorGender;
	private Gender companionGender;

}
