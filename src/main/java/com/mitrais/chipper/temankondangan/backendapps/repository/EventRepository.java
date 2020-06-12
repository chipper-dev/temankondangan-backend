package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT a from Event a WHERE a.user.userId = :userId")
	Optional<List<Event>> findByUserId(@Param("userId") Long userId);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " 
			+ "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "AND a.dataState <> 'DELETED' " 
			+ "WHERE ((e.minimumAge <= :age AND e.maximumAge >= :age "
			+ "AND e.companionGender in :companionGender) " 
			+ "OR (u.userId = :userId)) "
			+ "AND e.startDateTime > :now " 
			+ "AND e.dataState <> 'DELETED'")
	Page<EventFindAllListDBResponseWrapper> findAllByRelevantInfo(@Param("age") Integer age,
			@Param("companionGender") Collection<Gender> companionGender, @Param("userId") Long userId,
			@Param("now") LocalDateTime now, Pageable paging);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " 
			+ "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "WHERE u.userId = :userId "
			+ "AND (((e.startDateTime >= :now AND e.cancelled = FALSE) AND :current = 1) "
			+ "OR ((e.startDateTime < :now OR e.cancelled = TRUE) AND :current = 0))")
	List<EventFindAllListDBResponseWrapper> findAllMyEvent(@Param("userId") Long userId, @Param("now") LocalDateTime now,
														   @Param("current") int current, Sort sort);

	@Query("SELECT e FROM Event e " + "JOIN Applicant a ON a.event.eventId = e.eventId "
			+ "WHERE a.applicantUser.userId = :userId " + "AND a.dataState = 'ACTIVE' "
			+ "AND e.dataState = :dataStateEvent "
			+ "AND ((e.startDateTime >= :now AND :current = 1) OR (e.startDateTime < :now AND :current = 0))")
	List<Event> findAppliedEvent(@Param("userId") Long userId, @Param("dataStateEvent") DataState dataState,
			@Param("now") LocalDateTime now, @Param("current") Integer current, Sort sort);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " 
			+ "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "AND a.dataState <> 'DELETED' " 
			+ "WHERE e.minimumAge <= :userAge AND e.maximumAge >= :userAge "
			+ "AND e.companionGender in :companionGender " 
			+ "AND e.startDateTime BETWEEN :startDateTimeLowerLimit AND :startDateTimeUpperLimit "
			+ "AND e.dataState <> 'DELETED' " 
			+ "AND lower(e.city) LIKE CONCAT('%',:city,'%') "
			+ "AND (extract(year from age (NOW(), p.dob)) between :creatorMinimumAge AND :creatorMaximumAge) "
			+ "AND p.gender IN :creatorGender "
			+ "AND u.userId <> :userId")
	Page<EventFindAllListDBResponseWrapper> searchWithStartDateTime(@Param("userAge") Integer userAge,
			@Param("companionGender") Collection<Gender> companionGender, @Param("userId") Long userId,
			@Param("startDateTimeLowerLimit") LocalDateTime startDateTimeLowerLimit,
			@Param("startDateTimeUpperLimit") LocalDateTime startDateTimeUpperLimit,
			@Param("creatorMaximumAge") Integer creatorMaximumAge,
			@Param("creatorMinimumAge") Integer creatorMinimumAge,
			@Param("creatorGender") Collection<Gender> creatorGender, 
			@Param("city") String city, Pageable paging);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " 
			+ "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "AND a.dataState <> 'DELETED' " 
			+ "WHERE e.minimumAge <= :userAge AND e.maximumAge >= :userAge "
			+ "AND e.companionGender in :companionGender "
			+ "AND e.finishDateTime BETWEEN :finishDateTimeLowerLimit AND :finishDateTimeUpperLimit "
			+ "AND e.dataState <> 'DELETED' " 
			+ "AND lower(e.city) LIKE CONCAT('%',:city,'%') "
			+ "AND (extract(year from age (NOW(), p.dob)) between :creatorMinimumAge AND :creatorMaximumAge) "
			+ "AND p.gender IN :creatorGender "
			+ "AND u.userId <> :userId")
	Page<EventFindAllListDBResponseWrapper> searchWithFinishDateTime(@Param("userAge") Integer userAge,
			@Param("companionGender") Collection<Gender> companionGender, @Param("userId") Long userId,
			@Param("finishDateTimeLowerLimit") LocalDateTime finishDateTimeLowerLimit,
			@Param("finishDateTimeUpperLimit") LocalDateTime finishDateTimeUpperLimit,
			@Param("creatorMaximumAge") Integer creatorMaximumAge,
			@Param("creatorMinimumAge") Integer creatorMinimumAge,
			@Param("creatorGender") Collection<Gender> creatorGender, @Param("city") String city, Pageable paging);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " 
			+ "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "AND a.dataState <> 'DELETED' " 
			+ "WHERE e.minimumAge <= :userAge AND e.maximumAge >= :userAge "
			+ "AND e.companionGender in :companionGender "
			+ "AND (e.startDateTime BETWEEN :startDateTimeLowerLimit AND :startDateTimeUpperLimit "
			+ "OR e.finishDateTime BETWEEN :finishDateTimeLowerLimit AND :finishDateTimeUpperLimit) "
			+ "AND e.dataState <> 'DELETED' " 
			+ "AND lower(e.city) LIKE CONCAT('%',:city,'%') "
			+ "AND (extract(year from age (NOW(), p.dob)) between :creatorMinimumAge AND :creatorMaximumAge) "
			+ "AND p.gender IN :creatorGender "
			+ "AND u.userId <> :userId")
	Page<EventFindAllListDBResponseWrapper> searchWithStartAndFinishDateTime(@Param("userAge") Integer userAge,
			@Param("companionGender") Collection<Gender> companionGender, @Param("userId") Long userId,
			@Param("startDateTimeLowerLimit") LocalDateTime startDateTimeLowerLimit,
			@Param("startDateTimeUpperLimit") LocalDateTime startDateTimeUpperLimit,
			@Param("finishDateTimeLowerLimit") LocalDateTime finishDateTimeLowerLimit,
			@Param("finishDateTimeUpperLimit") LocalDateTime finishDateTimeUpperLimit,
			@Param("creatorMaximumAge") Integer creatorMaximumAge,
			@Param("creatorMinimumAge") Integer creatorMinimumAge,
			@Param("creatorGender") Collection<Gender> creatorGender, @Param("city") String city, Pageable paging);
}
