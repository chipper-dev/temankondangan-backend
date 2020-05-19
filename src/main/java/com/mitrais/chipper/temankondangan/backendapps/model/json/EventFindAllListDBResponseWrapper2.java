package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class EventFindAllListDBResponseWrapper2 {
	private Long eventId;
	private Long profileId;
	private String creatorFullName;
	private String createdBy;
	private String photoProfileUrl;
	private byte[] photoProfileUrlRaw;
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

	public EventFindAllListDBResponseWrapper2(Long eventId, Long profileId, String creatorFullName, String createdBy,
			String photoProfileUrl, byte[] photoProfileUrlRaw, String title, String city, LocalDateTime startDateTime,
			LocalDateTime finishDateTime, Integer minimumAge, Integer maximumAge, Gender creatorGender,
			Gender companionGender) {
		super();
		this.eventId = eventId;
		this.profileId = profileId;
		this.creatorFullName = creatorFullName;
		this.createdBy = createdBy;
		this.photoProfileUrl = photoProfileUrl;
		this.photoProfileUrlRaw = photoProfileUrlRaw;
		this.title = title;
		this.city = city;
		this.startDateTime = startDateTime;
		this.finishDateTime = finishDateTime;
		this.minimumAge = minimumAge;
		this.maximumAge = maximumAge;
		this.creatorGender = creatorGender;
		this.companionGender = companionGender;
	}

	public EventFindAllListDBResponseWrapper2(Long eventId, Long profileId, String creatorFullName, String createdBy,
			byte[] photoProfileUrlRaw, String title, String city, LocalDateTime startDateTime,
			LocalDateTime finishDateTime, Integer minimumAge, Integer maximumAge, Gender creatorGender,
			Gender companionGender) {
		super();
		this.eventId = eventId;
		this.profileId = profileId;
		this.creatorFullName = creatorFullName;
		this.createdBy = createdBy;
		this.photoProfileUrl = "";
		this.photoProfileUrlRaw = photoProfileUrlRaw;
		this.title = title;
		this.city = city;
		this.startDateTime = startDateTime;
		this.finishDateTime = finishDateTime;
		this.minimumAge = minimumAge;
		this.maximumAge = maximumAge;
		this.creatorGender = creatorGender;
		this.companionGender = companionGender;
	}
}
