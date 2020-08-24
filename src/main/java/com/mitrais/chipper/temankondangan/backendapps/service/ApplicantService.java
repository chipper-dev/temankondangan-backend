package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;

public interface ApplicantService {

	Applicant getApplicantById(Long applicantId);
	
	public void accept(Long userId, Long applicantId);

	public void cancelAccepted(Long userId, Long applicantId);

	public void rejectApplicant(Long userId, Long applicantId);
}
