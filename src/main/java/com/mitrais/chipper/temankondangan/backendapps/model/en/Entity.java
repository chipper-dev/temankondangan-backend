package com.mitrais.chipper.temankondangan.backendapps.model.en;

public enum Entity {
	USER("User"), PROFILE("Profile"), EVENT("Event"), APPLICANT("Applicant"), CHATROOM("Chatroom"), USER_ID("userId"),
	PROFILE_ID("profileId"), EVENT_ID("eventId"), APPLICANT_ID("applicantId");

	private String label;

	private Entity(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
