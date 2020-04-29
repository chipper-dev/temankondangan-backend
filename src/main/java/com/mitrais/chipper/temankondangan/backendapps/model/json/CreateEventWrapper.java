package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.time.LocalDateTime;

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
public class CreateEventWrapper {

	private String title;
	private String city;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime dateAndTime;
	private Integer minimumAge;
	private Integer maximumAge;
	private Gender companionGender;
	private String additionalInfo;

}
