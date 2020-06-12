package com.mitrais.chipper.temankondangan.backendapps.model.json;

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
public class SearchEventWrapper {
	@Builder.Default
	private Integer pageNumber = 0;
	@Builder.Default
	private Integer pageSize = 10;
	@Builder.Default
	private String sortBy = "createdDate";
	@Builder.Default
	private String direction = "DESC";
	@Builder.Default
	private Gender creatorGender = Gender.B;
	@Builder.Default
	private Integer creatorMaximumAge = 150;
	@Builder.Default
	private Integer creatorMinimumAge = 18;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private String startDateTimeLowerLimit;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private String startDateTimeUpperLimit;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private String finishDateTimeLowerLimit;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	private String finishDateTimeUpperLimit;
	private String city;
}
