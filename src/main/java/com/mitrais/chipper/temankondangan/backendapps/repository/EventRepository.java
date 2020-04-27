package com.mitrais.chipper.temankondangan.backendapps.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

}
