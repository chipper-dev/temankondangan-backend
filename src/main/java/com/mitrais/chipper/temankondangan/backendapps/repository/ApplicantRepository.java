package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
	@Query("SELECT a from Applicant a WHERE a.event.eventId = :eventId")
	List<Applicant> findByEventId(@Param("eventId") Long eventId);

	Boolean existsByApplicantUserAndEvent(User applicantuser, Event event);
}
