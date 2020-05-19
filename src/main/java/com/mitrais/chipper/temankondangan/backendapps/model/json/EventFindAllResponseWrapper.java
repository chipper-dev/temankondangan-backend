package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
