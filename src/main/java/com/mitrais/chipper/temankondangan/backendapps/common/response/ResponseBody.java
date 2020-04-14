package com.mitrais.chipper.temankondangan.backendapps.common.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class ResponseBody {
    
    private Date timestamp;
    private int status;
    private String error;
    private String exception;
    private Object message;
    private Object content;
    private String path;
    
    public Date getTimestamp() {
	return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
    }
    
    public int getStatus() {
	return status;
    }
    
    public void setStatus(int status) {
	this.status = status;
    }
    
    public String getError() {
	return error;
    }
    
    public void setError(String error) {
	this.error = error;
    }
    
    public String getException() {
	return exception;
    }
    
    public void setException(String exception) {
	this.exception = exception;
    }
    
    public Object getMessage() {
	return message;
    }
    
    public void setMessage(Object message) {
	this.message = message;
    }
    
    public Object getContent() {
	return content;
    }
    
    public void setContent(Object content) {
	this.content = content;
    }
    
    public String getPath() {
	return path;
    }
    
    public void setPath(String path) {
	this.path = path;
    }
    
    @Override
    public String toString() {
	return "ResponseBody [timestamp=" + timestamp + ", status=" + status + ", error=" + error + ", message="
	        + message + ", content=" + content + ", path=" + path + "]";
    }
}
