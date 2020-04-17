package com.mitrais.chipper.temankondangan.backendapps.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Class that defined response message from server and the return type should be
 * written straight to the HTTP response body.
 * 
 * @author david.christianto
 * @since 0.0.1-SNAPSHOT
 * @version 0.0.1-SNAPSHOT
 * @updated 29 Agt 2016
 */
@JsonInclude(Include.NON_NULL)
@Data
public class ResponseBody {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String exception;
    private Object message;
    private Object content;
    private String path;
    
    @Override
    public String toString() {
	return "ResponseBody [timestamp=" + timestamp + ", status=" + status + ", error=" + error + ", message="
	        + message + ", content=" + content + ", path=" + path + "]";
    }
}
