package com.mitrais.chipper.temankondangan.backendapps.common.response;

import java.util.List;

public class ContentList {
    
    private int pageSize;
    private int pageNumber;
    private int actualSize;
    private List<?> contents;
    
    public ContentList() {
    }
    
    public ContentList(int pageSize, int pageNumber, int actualSize, List<?> list) {
	this.pageSize = pageSize;
	this.pageNumber = pageNumber;
	this.actualSize = actualSize;
	this.contents = list;
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
	return contents;
    }
    
    public void setContentList(List<?> list) {
	this.contents = list;
    }
    
    @Override
    public String toString() {
	return "ContentList [pageSize=" + pageSize + ", pageNumber=" + pageNumber + ", actualSize=" + actualSize
	        + ", contents=" + contents + "]";
    }
    
}
