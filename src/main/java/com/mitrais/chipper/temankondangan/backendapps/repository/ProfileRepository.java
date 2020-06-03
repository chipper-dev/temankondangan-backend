package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

	@Query("SELECT a from Profile a WHERE a.user.userId = :userId")
	Optional<Profile> findByUserId(@Param("userId") Long userId);

	@Query("SELECT a from Profile a WHERE a.photoProfileFilename = :fileName")
	Optional<Profile> findByPhotoProfileFilename(@Param("fileName") String fileName);

}
