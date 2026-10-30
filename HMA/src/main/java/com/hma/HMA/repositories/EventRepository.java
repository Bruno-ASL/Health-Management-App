package com.hma.HMA.repositories;

import com.hma.HMA.enums.EventStatus;
import com.hma.HMA.enums.EventType;
import com.hma.HMA.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


/**
 * Repositório responsável pelo acesso e gestão dos dados dos Eventos..
 */
public interface EventRepository extends JpaRepository<Event, Integer> {

    /**
     * Obtém a lista de eventos de um determinado tipo.
     *
     * @param type tipo de evento a procurar (DOCTOR ou GYM).
     * @return a lista de eventos que correspondem ao tipo indicado.
     */
    List<Event> findByType(EventType type);

    /**
     * Obtém a lista de eventos com um determinado estado.
     *
     * @param status estado dos eventos a procurar (SCHEDULED, COMPLETED ou CANCELLED).
     * @return a lista de eventos que correspondem ao estado indicado.
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * Obtém a lista de eventos que correspondem simultaneamente
     * a um determinado tipo e estado.
     *
     * @param type tipo de evento a procurar (DOCTOR ou GYM).
     * @param status estado dos eventos a procurar (SCHEDULED, COMPLETED ou CANCELLED).
     * @return a lista de eventos que correspondem aos dois critérios indicados.
     */
    List<Event> findByTypeAndStatus(EventType type, EventStatus status);

    /**
     * Obtém a lista de eventos agendados para uma determinada data.
     *
     * @param date data dos eventos a procurar.
     * @return a lista de eventos que correspondem à data indicada.
     */
    List<Event> findByDate(LocalDate date);
}
