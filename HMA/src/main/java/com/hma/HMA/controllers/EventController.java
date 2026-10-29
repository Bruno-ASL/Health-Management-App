package com.hma.HMA.controllers;

import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import com.hma.HMA.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    EventRepository evRepo;

    @GetMapping("/api/events")
    public List<Event> getAllEvents(){
        return evRepo.findAll();
    }

    @GetMapping("/api/events?type={type}")
    public List<Event> getTypeEvents(@RequestParam(required = false) EventType type) {
        if (type != null) {
            return evRepo.findByType(type);
        }
        return evRepo.findAll();
    }

    @GetMapping("/events/{id}")
    public Event getEventById(@PathVariable Integer id) {
        return evRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento com Id " + id + " não existe."));
    }

    @PostMapping("/api/events/")
    public void createEvent(@RequestBody Event event){
        evRepo.save(event);
    }

    @PutMapping("/api/events/{id}")
    public void updateEvent(@PathVariable Integer id, @RequestBody Event updatedEvent){
        Event event = evRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento com Id " + id + " não existe."));

        event.setName(updatedEvent.getName());
        event.setType(updatedEvent.getType());
        event.setDate(updatedEvent.getDate());
        event.setTime(updatedEvent.getTime());
        event.setDuration(updatedEvent.getDuration());
        event.setStatus(updatedEvent.getStatus());

        evRepo.save(event);
    }

    @DeleteMapping("/api/events/{id}")
    public void deleteEvent(@PathVariable Integer id){
        Event event = evRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento com Id " + id + " não existe."));

        evRepo.delete(event);
    }
}
