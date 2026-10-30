package com.hma.HMA.models;

import com.hma.HMA.enums.EventStatus;
import com.hma.HMA.enums.EventType;

import jakarta.persistence.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade que representa um Evento agendado (consulta médica ou treino).
 *
 * Corresponde à tabela "event" da base de dados. Cada evento tem um nome,
 * um tipo, uma data e hora de início, uma duração em minutos e um estado.
 * Os getters, setters, equals, hashCode e toString são gerados pelo Lombok (@Data).
 */
@Data
@Entity
@Table(name = "event")
@RequiredArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false, columnDefinition = "event_type")
    private EventType type;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "event_status")
    private EventStatus status = EventStatus.SCHEDULED;
}
