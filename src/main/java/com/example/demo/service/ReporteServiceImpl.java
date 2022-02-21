package com.example.demo.service;

import com.example.demo.dto.Consulta;
import com.example.demo.dto.ReporteHoras;
import com.example.demo.entity.Reporte;
import com.example.demo.repository.ReporteRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService{

    @Autowired
    private ReporteRepository reporteRepository;

    @Override
    @Transactional
    public Reporte save(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    @Override
    public List<ReporteHoras> getHoursReport(Consulta consulta) {
        DateTime inicio = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(consulta.getSemana())
                .withDayOfWeek(1);
        DateTime fin = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(consulta.getSemana())
                .withDayOfWeek(7);

        String formatoInicio = inicio.toString("yyyy-MM-dd HH:MM:SS");
        String formatoFin = fin.toString("yyyy-MM-dd HH:MM:SS");

        return reporteRepository.getHoursByTecnico(consulta.getTecnico_id(), formatoInicio, formatoFin);
    }
}
