package com.mitrais.chipper.temankondangan.backendapps.service;

public interface ApplicantService {

	public void accept(String header, Long userId, Long applicantId);

	public void cancelAccepted(String header, Long userId, Long applicantId);

	public void rejectApplicant(String header, Long userId, Long applicantId);

}
