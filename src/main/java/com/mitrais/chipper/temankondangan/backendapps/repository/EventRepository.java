package com.mitrais.chipper.temankondangan.backendapps.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {

	@Query("SELECT a from Event a WHERE a.user.userId = :userId")
	Optional<List<Event>> findByUserId(@Param("userId") Long userId);

	
	List<Event> findAllByMinimumAgeLessThanEqualAndMaximumAgeGreaterThanEqualAndCompanionGenderInAndStartDateTimeAfter(Integer age1, Integer age2, Collection<Gender> companionGender, LocalDateTime now);
}
