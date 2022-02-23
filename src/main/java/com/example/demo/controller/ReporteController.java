package com.example.demo.controller;

import com.example.demo.dto.Consulta;
import com.example.demo.entity.Reporte;
import com.example.demo.service.ReporteService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reporte")
@CrossOrigin(origins = "http://localhost:4200/")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Reporte reporte){
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.save(reporte));
    }

    @GetMapping
    public ResponseEntity<?> hoursReport(@RequestBody Consulta consulta1){
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.getHoursReport(consulta1));
    }
}
