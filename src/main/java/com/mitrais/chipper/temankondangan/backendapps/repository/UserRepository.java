package com.mitrais.chipper.temankondangan.backendapps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.mitrais.chipper.temankondangan.backendapps.model.User;

import java.util.Optional;

@Component
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
