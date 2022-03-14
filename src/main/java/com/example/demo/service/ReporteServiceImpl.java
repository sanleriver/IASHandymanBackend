package com.example.demo.service;

import com.example.demo.entity.Reporte;
import com.example.demo.model.HorasSemanales;
import com.example.demo.repository.ReporteRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReporteServiceImpl implements ReporteService{

    @Autowired
    private ReporteRepository reporteRepository;

    public ReporteServiceImpl(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @Override
    @Transactional
    public Reporte save(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    @Override
    public HorasSemanales obtenerReporteHoras(long tecnicoId, int semana) {
        //List<String> fechas = fechasSemana(semana);

        //List<Reporte> reportesTecnico = reporteRepository.getHoursByTecnico(tecnicoId, fechas.get(0), fechas.get(1));
        HorasSemanales horasSemanales = new HorasSemanales();
        double horasTrabajadasSemana = 0.0;

        return horasSemanales;
    }

}
