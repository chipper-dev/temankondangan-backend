package com.mitrais.chipper.temankondangan.backendapps.service;

public interface ApplicantService {

	public void accept(Long userId, Long applicantId);

	public void cancelAccepted(Long userId, Long applicantId);

	public void rejectApplicant(Long userId, Long applicantId);
}
