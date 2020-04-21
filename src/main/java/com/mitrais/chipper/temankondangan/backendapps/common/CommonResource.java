package com.mitrais.chipper.temankondangan.backendapps.common;

import com.mitrais.chipper.temankondangan.backendapps.common.response.ContentList;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class that defined common function that exist in every resource.
 * 
 * @author david.christianto
 * @since 0.0.1-SNAPSHOT
 * @version 0.0.1-SNAPSHOT
 * @updated Aug 29, 2016
 */
public class CommonResource {

	public final static Logger LOGGER = LoggerFactory.getLogger(CommonResource.class);

	/**
	 * Method to get Response Message (Success process).
	 * 
	 * @param status  {@link int} HTTP Status.
	 * @param content {@link Object} Content of body.
	 * @param path    {@link String} Path URL.
	 * @return {@link ResponseBody} Response message.
	 */
	protected ResponseBody getResponseBody(int status, Object content, String path) {
		ResponseBody responseBody = new ResponseBody();
		responseBody.setTimestamp(LocalDateTime.now());
		responseBody.setStatus(status);
		responseBody.setContent(content);
		responseBody.setPath(path);

		return responseBody;
	}

	/**
	 * Method to get Response Message (Failed process).
	 * 
	 * @param status  {@link HttpStatus} HTTP Status.
	 * @param {@link  String} Exception.
	 * @param message {@link Object} Message error.
	 * @param path    {@link String} Path URL.
	 * @return {@link ResponseBody} Response message.
	 */
	protected ResponseBody getResponseBody(HttpStatus status, String exception, Object message, String path) {
		ResponseBody responseBody = new ResponseBody();
		responseBody.setTimestamp(LocalDateTime.now());
		responseBody.setStatus(status.value());
		responseBody.setException(exception);
		responseBody.setError(status.getReasonPhrase());
		responseBody.setMessage(message);
		responseBody.setPath(path);

		return responseBody;
	}

	/**
	 * Method to get ContentList
	 * 
	 * @param pageNumber {@link Integer} Number of Page
	 * @param pageSize   {@link Integer} Size of Page
	 * @param contents   {@link List} contents
	 * @return {@link ContentList}
	 */
	protected ContentList getContentList(Integer pageNumber, Integer pageSize, List<?> contents) {
		ContentList contentList = new ContentList();
		contentList.setPageNumber(pageNumber);
		contentList.setPageSize(pageSize);
		contentList.setContentList(contents);
		contentList.setActualSize(contents.size());
		return contentList;
	}

	protected String getToken(String authTokenHeader) {
		if (authTokenHeader.contains("Bearer")) {
			authTokenHeader = authTokenHeader.split(" ")[1].trim();
		} else {
			authTokenHeader = authTokenHeader.trim();
		}
		return authTokenHeader;
	}
}
