package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.mitrais.chipper.temankondangan.backendapps.model.User;

@Component
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);
}
