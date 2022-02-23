package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "REPORTE")
public class Reporte implements Serializable {
    @Id
    @Column(name = "reporte_id")
    @JsonProperty("reporte_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long reporte_id;

    @Column(name = "tecnico_id")
    @JsonProperty("tecnico_id")
    @NonNull
    private long tecnico_id;

    @Column(name = "servicio_id")
    @JsonProperty("servicio_id")
    @NonNull
    private long servicio_id;

    @Column(name = "fecha_hora_inicio")
    @JsonProperty("fecha_hora_inicio")
    @NonNull
    private Date fecha_hora_inicio;

    @Column(name = "fecha_hora_fin")
    @JsonProperty("fecha_hora_fin")
    @NonNull
    private Date fecha_hora_fin;
}
