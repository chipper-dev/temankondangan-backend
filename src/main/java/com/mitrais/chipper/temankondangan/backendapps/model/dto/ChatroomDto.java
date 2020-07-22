package com.mitrais.chipper.temankondangan.backendapps.model.dto;

import java.util.Date;

public interface ChatroomDto  {

	public Long getId() ;

	public void setId(Long id) ;
	
	public String getCreatedBy() ;

	public void setCreatedBy(String createdBy) ;
	
	public Date getCreatedDate() ;

	public void setCreatedDate(Date createdDate) ;
	
	public String getLastModifiedBy() ;

	public void setLastModifiedBy(String lastModifiedBy) ;
	
	public Date getLastModifiedDate() ;

	public void setLastModifiedDate(Date lastModifiedDate) ;
	
	public String getDataState() ;

	public void setDataState(String dataState) ;

	public Long getEventId() ;

	public void setEventId(Long eventId) ;
		
	public String getEventAdditionalInfo() ;

	public void setEventAdditionalInfo(String eventAdditionalInfo) ;
	
	public String getEventTitle() ;

	public void setEventTitle(String eventTitle) ;
	
	public String getEventCreatorName() ;

	public void setEventCreatorName(String eventCreatorName) ;
	
	public Long getUnreadChat() ;

	public void setUnreadChat(Long unreadChat) ;
	
}
