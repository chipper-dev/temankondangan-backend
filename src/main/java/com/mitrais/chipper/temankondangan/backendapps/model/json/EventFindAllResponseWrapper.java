package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "All details about Event Find All ")
public class EventFindAllResponseWrapper {

	private List<EventDetailResponseWrapper> events;

}
