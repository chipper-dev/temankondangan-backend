package com.mitrais.chipper.temankondangan.backendapps.common.response;

import java.util.List;

/**
 * Class that defined response message from server containing list object and
 * the return type should be
 * written straight to the HTTP response body.
 * 
 * @author aulia.istamar
 * @since 0.0.1-SNAPSHOT
 * @version 0.0.1-SNAPSHOT
 * @updated 5 Sept 2016
 */
public class ContentList {
    
    private int pageSize;
    private int pageNumber;
    private int actualSize;
    private List<?> contentList;
    
    public ContentList() {
    }
    
    public ContentList(int pageSize, int pageNumber, int actualSize, List<?> list) {
	this.pageSize = pageSize;
	this.pageNumber = pageNumber;
	this.actualSize = actualSize;
	this.contentList = list;
    }
    
    public int getPageSize() {
	return pageSize;
    }
    
    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
    
    public int getPageNumber() {
	return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
	this.pageNumber = pageNumber;
    }
    
    public int getActualSize() {
	return actualSize;
    }
    
    public void setActualSize(int actualSize) {
	this.actualSize = actualSize;
    }
    
    public List<?> getContentList() {
	return contentList;
    }
    
    public void setContentList(List<?> list) {
	this.contentList = list;
    }
    
    @Override
    public String toString() {
	return "ContentList [pageSize=" + pageSize + ", pageNumber=" + pageNumber + ", actualSize=" + actualSize
	        + ", contentList=" + contentList + "]";
    }
    
}
