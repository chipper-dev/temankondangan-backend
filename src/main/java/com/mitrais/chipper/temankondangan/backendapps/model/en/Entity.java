package com.mitrais.chipper.temankondangan.backendapps.model.en;

public enum Entity {
	USER("User"), PROFILE("Profile"), EVENT("Event"), APPLICANT("Applicant");

	private String label;

	private Entity(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
