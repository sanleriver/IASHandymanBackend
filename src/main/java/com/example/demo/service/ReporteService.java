package com.example.demo.service;

import com.example.demo.dto.Consulta;
import com.example.demo.entity.Reporte;
import com.example.demo.model.HorasSemanales;

import java.util.List;

public interface ReporteService {

    public Reporte save(Reporte reporte);

    public HorasSemanales getHoursReport(Consulta consulta);
}
