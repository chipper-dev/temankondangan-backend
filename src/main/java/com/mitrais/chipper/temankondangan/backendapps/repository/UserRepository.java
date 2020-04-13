package com.mitrais.chipper.temankondangan.backendapps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mitrais.chipper.temankondangan.backendapps.model.Users;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

}
