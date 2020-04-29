package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
}
