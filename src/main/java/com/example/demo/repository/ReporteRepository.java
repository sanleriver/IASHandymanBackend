package com.example.demo.repository;

import com.example.demo.dto.ReporteHoras;
import com.example.demo.entity.Reporte;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query(value = "SELECT reporte_id, fecha_hora_inicio, fecha_hora_fin FROM reporte WHERE tecnico_id = ?1 AND fecha_hora_inicio BETWEEN TO_TIMESTAMP ('?2' AND '?3' AND fecha_hora_fin BETWEEN '?2' AND '?3'", nativeQuery = true)
    List<ReporteHoras> getHoursByTecnico(long tecnico_id, String fecha_inicio, String fecha_fin);
}
