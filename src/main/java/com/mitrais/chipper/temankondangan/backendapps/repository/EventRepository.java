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
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT a from Event a WHERE a.user.userId = :userId")
	Optional<List<Event>> findByUserId(@Param("userId") Long userId);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy,"
			+ "e.title, e.city , e.startDateTime, e.finishDateTime,"
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, p.photoProfile) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " + "JOIN Profile p ON u.userId = p.user.userId "
			+ "WHERE e.minimumAge <= :age AND e.maximumAge >= :age "
			+ "AND e.companionGender in :companionGender AND e.startDateTime > :now")
	Page<EventFindAllListDBResponseWrapper> findAllByRelevantInfo(@Param("age") Integer age,
			@Param("companionGender") Collection<Gender> companionGender, @Param("now") LocalDateTime now,
			Pageable paging);

}
