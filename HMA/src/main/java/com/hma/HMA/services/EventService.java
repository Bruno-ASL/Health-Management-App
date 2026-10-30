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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pela lógica de negócio dos Eventos.
 *
 * Valida os dados recebidos e aplica as regras de negócio.
 */
@Service
public class EventService {

    @Autowired
    private EventRepository evRepo;

    /**
     * Obtém a lista de eventos, com filtros opcionais por tipo e por estado.
     *
     * Os filtros podem ser combinados. Se nenhum for indicado, devolve todos os eventos.
     *
     * @param type tipo de evento a filtrar (DOCTOR ou GYM), ou null para ignorar o filtro.
     * @param status estado a filtrar (SCHEDULED, COMPLETED ou CANCELLED), ou null para ignorar o filtro.
     * @return a lista de eventos que cumprem os filtros indicados.
     */
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

    /**
     * Obtém um evento pelo seu identificador único.
     *
     * Procura o evento na base de dados e devolve-o se existir.
     * Caso contrário, lança uma ResponseStatusException com o estado HTTP 404.
     *
     * @param id identificador do evento a procurar.
     * @return o evento correspondente ao identificador indicado.
     *
     * @throws ResponseStatusException com estado HttpStatus.NOT_FOUND (404)
     *         se não existir nenhum evento com o identificador indicado
     */
    public Event getEventById(Integer id) {
        return evRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento com Id " + id + " não existe."));
    }

    /**
     * Cria um novo evento.
     *
     * Valida os dados do evento, garante que o estado inicial não é
     * COMPLETED nem CANCELLED e verifica que não existe sobreposição
     * com outros eventos antes de o gravar.
     *
     * @param event evento a criar.
     * @return o evento gravado, já com o identificador gerado.
     *
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     *         se os dados forem inválidos, se o estado inicial for COMPLETED
     *         ou CANCELLED, ou se o horário colidir com outro evento
     */
    public Event createEvent(Event event) {
        validateEvent(event);

        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
            throwBadRequestException("Um Evento não pode ser criado com um estado inicial COMPLETED ou CANCELLED.");
        }

        validateNoConflict(event, null);

        return evRepo.save(event);
    }

    /**
     * Atualiza um evento existente.
     *
     * Valida os novos dados e, se o evento não for cancelado, verifica que
     * o novo horário não colide com outros eventos.
     * Só altera o evento existente depois de todas as validações passarem.
     *
     * @param id identificador do evento a atualizar.
     * @param updatedEvent novos dados do evento.
     * @return o evento atualizado.
     *
     * @throws ResponseStatusException com estado HttpStatus.NOT_FOUND (404)
     *         se não existir nenhum evento com o identificador indicado
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     *         se os novos dados forem inválidos ou o horário colidir com outro evento
     */
    public Event updateEvent(Integer id, Event updatedEvent){
        Event existingEvent = getEventById(id);

        validateEvent(updatedEvent);

        if (updatedEvent.getStatus() != EventStatus.CANCELLED) {
            validateNoConflict(updatedEvent, id);
        }

        existingEvent.setName(updatedEvent.getName());
        existingEvent.setType(updatedEvent.getType());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setTime(updatedEvent.getTime());
        existingEvent.setDuration(updatedEvent.getDuration());
        existingEvent.setStatus(updatedEvent.getStatus());

        return evRepo.save(existingEvent);
    }

    /**
     * Elimina um evento pelo seu identificador.
     *
     * @param id identificador do evento a eliminar.
     */
    public void deleteEvent(Integer id) {
        evRepo.delete(getEventById(id));
    }

    /**
     * Valida os dados obrigatórios de um evento.
     *
     * Verifica que o nome não está vazio, que a data e a hora existem,
     * que a duração é superior a zero e que o tipo é DOCTOR ou GYM.
     *
     * @param event evento a validar.
     *
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     *         na primeira regra que não for cumprida.
     */
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

    /**
     * Verifica se o horário de um evento colide com o de outros eventos.
     *
     * Dois eventos colidem quando cada um começa antes de o outro terminar.
     * Eventos seguidos (um termina quando o outro começa) não colidem.
     * Eventos cancelados são ignorados.
     *
     * @param event evento cujo horário se quer verificar.
     * @param eventId identificador do evento a ignorar na comparação (usado
     *        na atualização, para o evento não colidir consigo próprio),
     *        ou null na criação.
     *
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     *         se existir sobreposição com outro evento
     */
    private void validateNoConflict(Event event, Integer eventId) {
        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getTime());
        LocalDateTime eventEnd = eventStart.plusMinutes(event.getDuration());

        boolean hasConflict = evRepo
                .findByDate(event.getDate())
                .stream()
                .filter(e -> (eventId == null || !e.getId().equals(eventId))
                                 && e.getStatus() != EventStatus.CANCELLED)
                .anyMatch(e -> {
                    LocalDateTime eStart = LocalDateTime.of(e.getDate(), e.getTime());
                    LocalDateTime eEnd = eStart.plusMinutes(e.getDuration());
                    return eventStart.isBefore(eEnd) && eStart.isBefore(eventEnd);
                });

        if (hasConflict) {
            throwBadRequestException("Conflito de horário com outro evento existente.");
        }
    }

    /**
     * Lança uma exceção HTTP 400 com a mensagem indicada.
     *
     * @param errorMsg mensagem de erro devolvida ao cliente.
     *
     * @throws ResponseStatusException com estado HttpStatus.BAD_REQUEST (400)
     */
    private void throwBadRequestException(String errorMsg){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
    }
}
