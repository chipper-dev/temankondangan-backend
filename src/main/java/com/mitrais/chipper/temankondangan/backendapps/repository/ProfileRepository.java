package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;

@Transactional
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

	@Query("SELECT a from Profile a WHERE a.user.userId = :userId")
	Optional<Profile> findByUserId(@Param("userId") Long userId);

	@Query("SELECT a from Profile a WHERE a.photoProfileFilename = :fileName")
	Optional<Profile> findByPhotoProfileFilename(@Param("fileName") String fileName);

	@Query("SELECT a from Profile a WHERE a.gender IN ('L','P')")
	Optional<List<Profile>> fetchAllProfiles();
}
