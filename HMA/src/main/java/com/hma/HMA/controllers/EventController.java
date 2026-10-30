package com.hma.HMA.controllers;

import com.hma.HMA.enums.EventStatus;
import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import com.hma.HMA.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador REST responsável pelos pedidos HTTP relacionados com Eventos.
 *
 * Expõe os endpoints em "/api/events".
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Obtém a lista de eventos, com filtros opcionais por tipo e por estado.
     *
     * GET /api/events
     * GET /api/events?type=GYM
     * GET /api/events?status=COMPLETED
     * GET /api/events?type=GYM&status=SCHEDULED
     *
     * @param type tipo de evento a filtrar (DOCTOR ou GYM), ou null para ignorar o filtro.
     * @param status estado a filtrar (SCHEDULED, COMPLETED ou CANCELLED), ou null para ignorar o filtro.
     * @return a lista de eventos que cumprem os filtros indicados (HTTP 200).
     */
    @GetMapping
    public List<Event> getEvents(
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) EventStatus status) {

        return eventService.getEvents(type, status);
    }

    /**
     * Obtém um evento pelo seu identificador.
     *
     * GET /api/events/{id}
     *
     * @param id identificador do evento.
     * @return o evento correspondente (HTTP 200).
     *
     * @throws ResponseStatusException com estado HttpStatus.NOT_FOUND (404)
     *         se não existir nenhum evento com o identificador indicado
     */
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Integer id) {
        return eventService.getEventById(id);
    }

    /**
     * Cria um novo evento.
     *
     * POST /api/events
     *
     * @param newEvent dados do novo evento.
     * @return o evento criado, com o identificador gerado (HTTP 201).
     *
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     *         se os dados forem inválidos, se o estado inicial for COMPLETED
     *         ou CANCELLED, ou se o horário colidir com outro evento
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Event createEvent(@RequestBody Event newEvent){
        return eventService.createEvent(newEvent);
    }

    /**
     * Atualiza um evento existente.
     *
     * PUT /api/events/{id}
     *
     * O corpo do pedido deve conter o evento completo, porque todos os
     * campos são substituídos.
     *
     * @param id identificador do evento a atualizar.
     * @param updatedEvent novos dados do evento.
     * @return o evento atualizado (HTTP 200).
     *
     * @throws ResponseStatusException com estado HttpStatus.NOT_FOUND (404)
     *         se não existir nenhum evento com o identificador indicado
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     *         se os novos dados forem inválidos ou o horário colidir com outro evento
     */
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Integer id, @RequestBody Event updatedEvent){
        return eventService.updateEvent(id, updatedEvent);
    }

    /**
     * Elimina um evento pelo seu identificador.
     *
     * DELETE /api/events/{id}
     *
     * @param id identificador do evento a eliminar.
     *
     * Responde com 204 quando o evento é eliminado.
     *
     * @throws ResponseStatusException com estado HttpStatus.NOT_FOUND (404)
     *         se não existir nenhum evento com o identificador indicado
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Integer id){
        eventService.deleteEvent(id);
    }
}
