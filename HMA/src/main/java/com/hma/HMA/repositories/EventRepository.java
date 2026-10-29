package com.hma.HMA.repositories;

import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByType(EventType type);
}
