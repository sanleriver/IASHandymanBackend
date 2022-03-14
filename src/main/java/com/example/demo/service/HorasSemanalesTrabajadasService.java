package com.example.demo.service;

import com.example.demo.entity.HorasSemanalesTrabajadas;
import java.time.LocalDateTime;

import java.util.Optional;

public interface HorasSemanalesTrabajadasService {

    public HorasSemanalesTrabajadas save(HorasSemanalesTrabajadas horasSemanalesTrabajas);

    public Optional<HorasSemanalesTrabajadas> findById(long id);

    public HorasSemanalesTrabajadas actualizarHorasSemana(long tecnico_id, LocalDateTime fechaHoraInicial, LocalDateTime fechaHoraFinal);
}
