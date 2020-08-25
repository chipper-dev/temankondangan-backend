package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;

public interface ApplicantService {

	Applicant getApplicantById(Long applicantId);
	
	public void accept(String header, Long userId, Long applicantId);

	public void cancelAccepted(String header, Long userId, Long applicantId);

	public void rejectApplicant(String header, Long userId, Long applicantId);

}
