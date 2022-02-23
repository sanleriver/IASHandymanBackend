package com.example.demo.repository;

import com.example.demo.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query(value = "SELECT * FROM reporte WHERE tecnico_id = ?1 " +
            "AND fecha_hora_inicio BETWEEN TO_TIMESTAMP (?2, 'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP (?3, 'YYYY-MM-DD HH24:MI:SS') " +
            "AND fecha_hora_fin BETWEEN TO_TIMESTAMP (?2, 'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP (?3, 'YYYY-MM-DD HH24:MI:SS')", nativeQuery = true)
    List<Reporte> getHoursByTecnico(long tecnico_id, String fecha_inicio, String fecha_fin);
}