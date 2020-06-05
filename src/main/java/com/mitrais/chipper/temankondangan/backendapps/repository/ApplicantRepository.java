package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    @Query("SELECT a from Applicant a WHERE a.event.eventId = :eventId")
    Optional<List<Applicant>> findByEventId(@Param("eventId")Long eventId);

    @Query("SELECT a from Applicant a WHERE a.applicantUser.userId = :userId AND a.event.eventId = :eventId")
    Optional<Applicant> findByApplicantUserIdAndEventId(@Param("userId") Long applicantUserId, @Param("eventId") Long eventId);

    Boolean existsByApplicantUserAndEvent(User applicantuser, Event event);

    @Query("SELECT a from Applicant a WHERE a.event.eventId = :eventId AND a.status = 'ACCEPTED'" )
    List<Applicant> findByEventIdAccepted(@Param("eventId")Long eventId);
    
    @Query("SELECT a from Applicant a WHERE a.event.eventId = :eventId AND a.status = :applicantStatus")
    Optional<List<Applicant>> findByEventIdAndStatus(@Param("eventId") Long eventId, 
    		@Param("applicantStatus") ApplicantStatus applicantStatus);

    Boolean existsByEventAndStatus(Event event, ApplicantStatus applicantStatus);
}
