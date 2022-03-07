package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HORAS_TRABAJADAS")
public class HorasSemanalesTrabajadas implements Serializable {

    @Id
    @Column(name = "id_horas_trabajadas")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id_horastrabajadas;

    @Column(name = "tecnico_id")
    @JsonProperty("tecnico_id")
    @NonNull
    private long tecnico_id;

    @Column(name = "numero_semana")
    @JsonProperty("numero_semana")
    @NonNull
    private int numero_semana;

    @Column(name = "cant_horas_normales")
    @JsonProperty("cantidadHorasNormales")
    @NonNull
    private double cantidadHorasNormales;

    @Column(name = "cant_horas_nocturnas")
    @JsonProperty("cantidadHorasNocturnas")
    @NonNull
    private double cantidadHorasNocturnas;

    @Column(name = "cant_horas_dominicales")
    @JsonProperty("cantidadHorasDominicales")
    @NonNull
    private double cantidadHorasDominicales;

    @Column(name = "cant_horas_normales_extra")
    @JsonProperty("cantidadHorasNormalesExtra")
    @NonNull
    private double cantidadHorasNormalesExtra;

    @Column(name = "cant_horas_nocturnas_extra")
    @JsonProperty("cantidadHorasNocturnasExtra")
    @NonNull
    private double cantidadHorasNocturnasExtra;

    @Column(name = "cant_horas_dominicales_extra")
    @JsonProperty("cantidadHorasDominicalesExtra")
    @NonNull
    private double cantidadHorasDominicalesExtra;
}
