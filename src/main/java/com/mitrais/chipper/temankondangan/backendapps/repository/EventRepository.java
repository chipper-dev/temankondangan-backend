package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT a from Event a WHERE a.user.userId = :userId")
	Optional<List<Event>> findByUserId(@Param("userId") Long userId);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled, e.createdDate) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " + "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "AND a.dataState <> 'DELETED' " + "WHERE ((e.minimumAge <= :age AND e.maximumAge >= :age "
			+ "AND e.companionGender in :companionGender) " + "OR (u.userId = :userId)) "
			+ "AND e.startDateTime > :now " + "AND e.dataState <> 'DELETED' " + "AND e.cancelled = FALSE")
	Page<EventFindAllListDBResponseWrapper> findAllByRelevantInfo(@Param("age") Integer age,
			@Param("companionGender") Collection<Gender> companionGender, @Param("userId") Long userId,
			@Param("now") LocalDateTime now, Pageable paging);

	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper"
			+ "(e.eventId, p.profileId, p.fullName, e.createdBy, "
			+ "e.title, e.city , e.startDateTime, e.finishDateTime, "
			+ "e.minimumAge, e.maximumAge, p.gender, e.companionGender, a.status, e.cancelled, e.createdDate) from Event e "
			+ "JOIN User u ON e.user.userId = u.userId " + "JOIN Profile p ON u.userId = p.user.userId "
			+ "LEFT JOIN Applicant a ON a.applicantUser.userId = :userId AND a.event.eventId = e.eventId "
			+ "WHERE u.userId = :userId " + "AND (((e.startDateTime >= :now AND e.cancelled = FALSE) AND :current = 1) "
			+ "OR ((e.startDateTime < :now OR e.cancelled = TRUE) AND :current = 0))")
	List<EventFindAllListDBResponseWrapper> findAllMyEvent(@Param("userId") Long userId,
			@Param("now") LocalDateTime now, @Param("current") int current, Sort sort);

	@Query("SELECT e FROM Event e JOIN Applicant a ON a.event.eventId = e.eventId "
			+ "WHERE a.applicantUser.userId = :userId AND a.dataState = 'ACTIVE' "
			+ "AND e.startDateTime >= NOW() AND e.cancelled = FALSE "
			+ "AND (a.status = :applicantStatus OR true = :allStatus)")
	List<Event> findActiveAppliedEvent(@Param("userId") Long userId,
			@Param("applicantStatus") ApplicantStatus applicantStatus, @Param("allStatus") boolean allStatus,
			Sort sort);

	@Query("SELECT e FROM Event e JOIN Applicant a ON a.event.eventId = e.eventId "
			+ "WHERE a.applicantUser.userId = :userId AND a.dataState = 'ACTIVE' "
			+ "AND ((e.startDateTime < now() AND e.cancelled = false AND true=:pastTimeOnly) "
			+ "AND (a.status = :applicantStatus OR true = :allStatus) "
			+ "OR (e.cancelled = true AND true = :isCanceled))")
	List<Event> findPastAppliedEvent(@Param("userId") Long userId,
			@Param("applicantStatus") ApplicantStatus applicantStatus, @Param("allStatus") boolean allStatus,
			@Param("pastTimeOnly") boolean pastTimeOnly, @Param("isCanceled") boolean isCanceled, Sort sort);

	@Query(nativeQuery = true, value = "SELECT e.event_id, p.profile_id, p.full_name, e.created_by, "
			+ "e.title, e.city , e.start_date_time, e.finish_date_time, e.minimum_age, e.maximum_age, "
			+ "p.gender, e.companion_gender, a.status, e.cancelled, e.created_date FROM Event e "
			+ "JOIN Users u ON e.user_id = u.user_id " + "JOIN Profile p ON u.user_id = p.user_id "
			+ "LEFT JOIN Applicants a ON a.user_id = :userId AND a.event_id = e.event_id AND a.data_state <> 'DELETED' "
			+ "WHERE e.minimum_age <= :userAge AND e.maximum_age >= :userAge "
			+ "AND e.companion_gender IN (:companionGender) "
			+ "AND e.start_date_time BETWEEN :startDate AND :finishDate "
			+ "AND (start_date_time\\:\\:time BETWEEN :startHourLowerRange AND :startHourUpperRange "
			+ "OR start_date_time\\:\\:time BETWEEN :secondStartHourLowerRange AND :secondStartHourUpperRange "
			+ "OR finish_date_time\\:\\:time BETWEEN :finishHourLowerRange AND :finishHourUpperRange "
			+ "OR finish_date_time\\:\\:time BETWEEN :secondFinishHourLowerRange AND :secondFinishHourUpperRange) "
			+ "AND e.data_state <> 'DELETED' " + "AND e.cancelled = FALSE "
			+ "AND lower(e.city) SIMILAR TO CONCAT('(',:city,')') "
			+ "AND (extract(year from age (NOW(), p.dob)) between :creatorMinimumAge AND :creatorMaximumAge) "
			+ "AND p.gender IN (:creatorGender) " + "AND u.user_id <> :userId")
	Page<Map<String, Object>> search(@Param("userAge") Integer userAge,
			@Param("companionGender") List<String> companionGender, @Param("userId") Long userId,
			@Param("startDate") LocalDateTime startDate, @Param("finishDate") LocalDateTime finishDate,
			@Param("startHourLowerRange") LocalTime startHourLowerRange,
			@Param("startHourUpperRange") LocalTime startHourUpperRange,
			@Param("finishHourLowerRange") LocalTime finishHourLowerRange,
			@Param("finishHourUpperRange") LocalTime finishHourUpperRange,
			@Param("secondStartHourLowerRange") LocalTime secondStartHourLowerRange,
			@Param("secondStartHourUpperRange") LocalTime secondStartHourUpperRange,
			@Param("secondFinishHourLowerRange") LocalTime secondFinishHourLowerRange,
			@Param("secondFinishHourUpperRange") LocalTime secondFinishHourUpperRange,
			@Param("creatorMaximumAge") Integer creatorMaximumAge,
			@Param("creatorMinimumAge") Integer creatorMinimumAge, @Param("creatorGender") List<String> creatorGender,
			@Param("city") String city, Pageable paging);
}