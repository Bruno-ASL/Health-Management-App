package com.hma.HMA.repositories;

import com.hma.HMA.enums.EventStatus;
import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByType(EventType type);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByTypeAndStatus(EventType type, EventStatus status);

    List<Event> findByDateAndTime(LocalDate date, LocalTime time);
}
