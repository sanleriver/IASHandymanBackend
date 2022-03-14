package com.example.demo.controller;

import com.example.demo.entity.HorasSemanalesTrabajadas;
import com.example.demo.entity.Reporte;
import com.example.demo.service.HorasSemanalesTrabajadasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;

@RestController
@RequestMapping("/api/horas")
@CrossOrigin(origins = "http://localhost:4200/")
public class HorasSemanalesTrabajadasController {

    @Autowired
    private HorasSemanalesTrabajadasService horasSemanalesTrabajadasService;

    @PutMapping
    @Transactional
    public ResponseEntity<HorasSemanalesTrabajadas> registrarHoras(@RequestBody Reporte reporte){
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(horasSemanalesTrabajadasService.actualizarHorasSemana(reporte.getTecnico_id(),
                        reporte.getFecha_hora_inicio(),
                        reporte.getFecha_hora_fin()));
    }
}
