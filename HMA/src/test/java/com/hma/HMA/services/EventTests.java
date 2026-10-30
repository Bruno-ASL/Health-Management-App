package com.hma.HMA.services;

import com.hma.HMA.enums.EventStatus;
import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import com.hma.HMA.repositories.EventRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventTests {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setName("Consulta Medica");
        testEvent.setType(EventType.DOCTOR);
        testEvent.setDate(LocalDate.of(2026, 12, 1));
        testEvent.setTime(LocalTime.of(9, 30));
        testEvent.setDuration(30);
        testEvent.setStatus(EventStatus.SCHEDULED);
    }

    // ---------- Testes relacionados com criação de Eventos ----------

    @Test
    void createEvent_withValidData_savesAndReturnsEvent() {
        when(eventRepository.save(testEvent)).thenReturn(testEvent);

        Event result = eventService.createEvent(testEvent);

        assertEquals("Consulta Medica", result.getName());
        verify(eventRepository).save(testEvent);
    }

    @Test
    void createEvent_withoutName_throwsBadRequest() {
        testEvent.setName("  ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> eventService.createEvent(testEvent));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_withZeroDuration_throwsBadRequest() {
        testEvent.setDuration(0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> eventService.createEvent(testEvent));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_withCompletedStatus_throwsBadRequest() {
        testEvent.setStatus(EventStatus.COMPLETED);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> eventService.createEvent(testEvent));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(eventRepository, never()).save(any());
    }

    // ---------- Testes relacionados com a consulta de Eventos ----------

    @Test
    void getEventById_whenExists_returnsEvent() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));

        Event result = eventService.getEventById(1);

        assertSame(testEvent, result);
    }

    @Test
    void getEventById_whenMissing_throwsNotFound() {
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> eventService.getEventById(999));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ---------- Teste relacionado com eliminação de Evento ----------

    @Test
    void deleteEvent_whenMissing_throwsNotFoundAndDoesNotDelete() {
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> eventService.deleteEvent(999));

        verify(eventRepository, never()).delete(any());
    }
}
