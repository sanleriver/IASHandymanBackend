package com.example.demo;

import com.example.demo.entity.Reporte;
import com.example.demo.model.DatosPrueba;
import com.example.demo.model.HorasSemanales;
import com.example.demo.repository.ReporteRepository;
import com.example.demo.service.ReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	@MockBean
	ReporteRepository reporteRepository;

	List<Reporte> primero = new ArrayList<>();
	List<Reporte> segundo = new ArrayList<>();

	@Autowired
	ReporteService reporteService;

	@DisplayName(value = "Validando el calculo de horas trabajadas")
	@Test
	void contextLoads() {
		primero.add(DatosPrueba.REPORTE_001);
		primero.add(DatosPrueba.REPORTE_002);
		when(reporteRepository.getHoursByTecnico(123456L, "2022-02-14 00:00:00", "2022-02-20 23:59:59")).thenReturn(primero);
		segundo.add(DatosPrueba.REPORTE_003);
		segundo.add(DatosPrueba.REPORTE_004);
		when(reporteRepository.getHoursByTecnico(456789L, "2022-02-21 00:00:00", "2022-02-27 23:59:59")).thenReturn(segundo);

		HorasSemanales horasSemanales123456 = reporteService.obtenerReporteHoras(123456L, 7);
		HorasSemanales horasSemanales456789 = reporteService.obtenerReporteHoras(456789L, 8);

		assertEquals(new HorasSemanales(1,2,0,0,0,0), horasSemanales123456);
		assertEquals(new HorasSemanales(1.87,1.37,0.8,0,0,0), horasSemanales456789);
	}

}
