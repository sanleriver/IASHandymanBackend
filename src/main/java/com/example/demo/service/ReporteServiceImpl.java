package com.example.demo.service;

import com.example.demo.entity.Reporte;
import com.example.demo.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReporteServiceImpl implements ReporteService{

    @Autowired
    private ReporteRepository reporteRepository;

    @Override
    @Transactional
    public Reporte save(Reporte reporte) {
        return reporteRepository.save(reporte);
    }
}
