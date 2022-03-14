package com.example.demo.repository;

import com.example.demo.entity.HorasSemanalesTrabajadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HorasSemanalesTrabajadasRepository extends JpaRepository<HorasSemanalesTrabajadas, Long> {

    @Query(value = "SELECT * FROM HORAS_TRABAJADAS WHERE TECNICO_ID = ?1 AND NUMERO_SEMANA = ?2", nativeQuery = true)
    HorasSemanalesTrabajadas obtenerRegistroHoras(
            long tecnico_id, int numero_semana);

    @Modifying
    @Query(value = "UPDATE HORAS_TRABAJADAS SET CANT_HORAS_NORMALES = ?3,CANT_HORAS_NOCTURNAS =?4," +
            "CANT_HORAS_DOMINICALES =?5,CANT_HORAS_NORMALES_EXTRA =?6,CANT_HORAS_NOCTURNAS_EXTRA= ?7," +
            "CANT_HORAS_DOMINICALES_EXTRA =?8 WHERE tecnico_id =?1 AND numero_semana = ?2", nativeQuery = true)
    void actualizarRegistroHoras(
            long tecnico_id, int numero_semana, double cantidadHorasNormales,
            double cantidadHorasNocturnas, double cantidadHorasDominicales,
            double cantidadHorasNormalesExtra, double cantidadHorasNocturnasExtra,
            double cantidadHorasDominicalesExtra);
    }
