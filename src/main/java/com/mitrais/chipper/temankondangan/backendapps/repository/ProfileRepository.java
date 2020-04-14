package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;

@Component
public interface ProfileRepository extends JpaRepository<Profile, Long> {

	@Query("SELECT a from Profile a WHERE a.user.userId = :userId")
	Optional<Profile> findByUserId(@Param("userId") Long userId);

}