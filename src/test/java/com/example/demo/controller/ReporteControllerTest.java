package com.example.demo.controller;

import com.example.demo.entity.Reporte;
import com.example.demo.model.DatosPrueba;
import com.example.demo.model.HorasSemanales;
import com.example.demo.service.ReporteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.*;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ReporteService reporteService;

    ObjectMapper objectMapper;
    HorasSemanales horasSemanales = new HorasSemanales(1,2,0,0,0,0);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void create() throws Exception, JsonProcessingException {
        Reporte reporte = new Reporte();
        reporte.setReporte_id(1);
        reporte.setTecnico_id(123456);
        reporte.setServicio_id(123456);
        reporte.setFecha_hora_inicio(new Date(122, 1, 17, 13, 50));
        reporte.setFecha_hora_fin(new Date(122, 1, 17, 15, 50));

        mvc.perform(post("/api/reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporte)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void hoursReport() throws Exception {
        when(reporteService.getHoursReport(123456L, 7)).thenReturn(horasSemanales);

        //When
        mvc.perform(get("/api/reporte/123456/7").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidadHorasNormales").value(1));

        verify(reporteService).getHoursReport(123456L,7);
    }
}