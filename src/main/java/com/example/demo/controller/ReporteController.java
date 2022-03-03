package com.example.demo.controller;

import com.example.demo.dto.Consulta;
import com.example.demo.entity.Reporte;
import com.example.demo.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(reporteService.save(reporte));
    }

    @GetMapping("/{tecnicoId}/{semana}")
    public ResponseEntity<?> hoursReport(@PathVariable long tecnicoId, @PathVariable int semana){
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.getHoursReport(tecnicoId, semana));
    }
}
