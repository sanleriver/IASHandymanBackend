package com.example.demo.service;

import com.example.demo.dto.Consulta;
import com.example.demo.dto.ReporteHoras;
import com.example.demo.entity.Reporte;

import java.util.Date;
import java.util.List;

public interface ReporteService {

    public Reporte save(Reporte reporte);

    public List<ReporteHoras> getHoursReport(Consulta consulta);
}
