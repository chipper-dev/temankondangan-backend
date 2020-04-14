package com.mitrais.chipper.temankondangan.backendapps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.mitrais.chipper.temankondangan.backendapps.model.Users;

@Component
public interface UserRepository extends JpaRepository<Users, Long> {

}
