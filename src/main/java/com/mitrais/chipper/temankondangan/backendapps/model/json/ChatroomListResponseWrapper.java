package com.mitrais.chipper.temankondangan.backendapps.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomListResponseWrapper {
	private Integer pageSize;
	private Integer pageNumber;
	private long actualSize;
	private List<ChatroomDto> contentList;
}
