package com.hma.HMA.services;

import com.hma.HMA.enums.EventStatus;
import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import com.hma.HMA.repositories.EventRepository;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Data
@Service
public class EventService {

    @Autowired
    private EventRepository evRepo;

    public List<Event> getEvents(EventType type, EventStatus status) {
        if (type != null && status != null) {
            return evRepo.findByTypeAndStatus(type, status);
        }
        if (type != null) {
            return evRepo.findByType(type);
        }
        if (status != null) {
            return evRepo.findByStatus(status);
        }
        return evRepo.findAll();
    }

    public Event getEventById(Integer id) {
        return evRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento com Id " + id + " não existe."));
    }

    public Event createEvent(Event event) {
        validateEvent(event);

        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
            throwBadRequestException("Um Evento não pode ser criado com um estado inicial COMPLETED ou CANCELLED.");
        }
        if (event.getType() != EventType.DOCTOR && event.getType() != EventType.GYM) {
            throwBadRequestException("O tipo de Evento apenas poderá ser DOCTOR ou GYM.");
        }
        if (!evRepo.findByDateAndTime(event.getDate(), event.getTime()).isEmpty()) {
            throwBadRequestException("Conflito de horário com outro evento existente.");

        }

        return evRepo.save(event);
    }

    public Event updateEvent(Integer id, Event updatedEvent){
        Event existingEvent = getEventById(id);

        existingEvent.setName(updatedEvent.getName());
        existingEvent.setType(updatedEvent.getType());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setTime(updatedEvent.getTime());
        existingEvent.setDuration(updatedEvent.getDuration());
        existingEvent.setStatus(updatedEvent.getStatus());

        validateEvent(existingEvent);

        return evRepo.save(existingEvent);
    }

    public void deleteEvent(Integer id) {
        evRepo.delete(getEventById(id));
    }

    private void validateEvent(Event event){

        if (event.getName() == null || event.getName().isBlank()){
            throwBadRequestException("O nome do Evento é obrigatório.");
        }
        if (event.getDate() == null || event.getTime() == null) {
            throwBadRequestException("A data e a hora do Evento são ambas obrigatórias.");
        }
        if (event.getDuration() == null || event.getDuration() <= 0) {
            throwBadRequestException("A duração do Evento deve ser superior a zero.");
        }
        if (event.getType() == null || (event.getType() != EventType.DOCTOR && event.getType() != EventType.GYM)) {
            throwBadRequestException("O tipo de Evento é obrigatório e apenas pode ser do tipo DOCTOR ou GYM.");
        }
    }

    private void throwBadRequestException(String errorMsg){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
    }
}
