package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.TempPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempPasswordRepository extends JpaRepository<TempPassword, Long> {
}
