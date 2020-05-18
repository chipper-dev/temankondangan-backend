package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT a from Event a WHERE a.user.userId = :userId")
	Optional<List<Event>> findByUserId(@Param("userId") Long userId);

	@Query("SELECT a from Event a WHERE a.minimumAge <= :age AND a.maximumAge >= :age AND a.companionGender in :companionGender AND a.startDateTime > :now")
	Page<Event> findAllByRelevantInfo(@Param("age") Integer age,
			@Param("companionGender") Collection<Gender> companionGender, @Param("now") LocalDateTime now,
			Pageable paging);
}
