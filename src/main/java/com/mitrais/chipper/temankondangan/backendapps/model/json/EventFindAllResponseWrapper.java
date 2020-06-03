package com.mitrais.chipper.temankondangan.backendapps.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFindAllResponseWrapper {
	private Integer pageSize;
	private Integer pageNumber;
	private long actualSize;
	private List<EventFindAllListDBResponseWrapper> contentList;
}
