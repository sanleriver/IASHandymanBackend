package com.example.demo.controller;

import com.example.demo.entity.HorasSemanalesTrabajadas;
import com.example.demo.service.HorasSemanalesTrabajadasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horas")
@CrossOrigin(origins = "http://localhost:4200/")
public class HorasSemanalesTrabajadasController {

    @Autowired
    private HorasSemanalesTrabajadasService horasSemanalesTrabajadasService;

    @PutMapping("/{id_tecnico}/{fecha1}/{fecha2}")
    public ResponseEntity<HorasSemanalesTrabajadas> registrarHoras(@PathVariable(value = "id_tecnico") long tecnico_id, @PathVariable(value = "fecha1") String fechaHoraInicial, @PathVariable(value = "fecha2") String fechaHoraFinal){
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(horasSemanalesTrabajadasService.actualizarHorasSemana(tecnico_id, fechaHoraInicial, fechaHoraFinal));
    }
}
