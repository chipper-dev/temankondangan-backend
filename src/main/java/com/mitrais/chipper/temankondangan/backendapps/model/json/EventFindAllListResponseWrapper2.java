package com.mitrais.chipper.temankondangan.backendapps.model.json;

public class EventFindAllListResponseWrapper2 {
	private Long eventId;
	private String title;
	private String city;

	public EventFindAllListResponseWrapper2(Long eventId, String title, String city) {
		super();
		this.eventId = eventId;
		this.title = title;
		this.city = city;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
