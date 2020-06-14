package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.util.List;

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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private String startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private String finishDate;
	private Integer startHourLowerRange;
	private Integer startHourUpperRange;
	private Integer finishHourLowerRange;
	private Integer finishHourUpperRange;
	private List<String> city;
}
