package com.example.demo.service;

import com.example.demo.entity.Reporte;
import com.example.demo.model.HorasSemanales;
import com.example.demo.repository.ReporteRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        List<String> fechas = fechasSemana(semana);

        List<Reporte> reportesTecnico = reporteRepository.getHoursByTecnico(tecnicoId, fechas.get(0), fechas.get(1));
        HorasSemanales horasSemanales = new HorasSemanales();
        double horasTrabajadasSemana = 0.0;

        for (int i=0; i< reportesTecnico.size(); i++){
            List<Calendar> fechasCalendar = instanciarCalendar(reportesTecnico.get(i).getFecha_hora_inicio(), reportesTecnico.get(i).getFecha_hora_fin());

        }

        return horasSemanales;
    }

    //ESTA FUNCIÓN PERMITE CALCULAR LA CANTIDAD DE HORAS
    private double calcularCatidadHoras(Date horaInicio, Date horaFin){
        double cantidadHoras = (double)(horaFin.getTime() - horaInicio.getTime())/(double)3600000;
        return cantidadHoras;
    }

    //ESTA FUNCIÓN CALCULA LA FECHA INICIAL Y LA FECHA FINAL DE UNA SEMANA ESPECÍFICA
    private List<String> fechasSemana(int semana){
        List<String> fechas = new ArrayList<>();
        DateTime inicio = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(semana)
                .withDayOfWeek(1)
                .withHourOfDay(00)
                .withMinuteOfHour(00)
                .withSecondOfMinute(00);
        String formatoInicio = inicio.toString("yyyy-MM-dd HH:mm:ss");
        fechas.add(formatoInicio);
        DateTime fin = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(semana)
                .withDayOfWeek(7)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59);
        String formatoFin = fin.toString("yyyy-MM-dd HH:mm:ss");
        fechas.add(formatoFin);
        return fechas;
    }

    //ESTA FUNCIÓN CREA UNA INSTACIA DE TIPO CALENDAR DE LAS FECHAS DE UN REPORTE
    private List<Calendar> instanciarCalendar(Date fechaHoraInicio, Date fechaHoraFinal){
        List<Calendar> calendarList = new ArrayList<>();
        Calendar horaInicio = Calendar.getInstance();
        horaInicio.setTime(fechaHoraInicio);
        calendarList.add(horaInicio);
        Calendar horaFin = Calendar.getInstance();
        horaFin.setTime(fechaHoraFinal);
        calendarList.add(horaFin);
        return calendarList;
    }

    //EN ESTA FUNCIÓN ESTABLECEMOS MEDIA MOCHE
    private Date establecerMedianoche(Calendar fechaHora){
        Calendar mediaNocheCalendar = Calendar.getInstance();
        mediaNocheCalendar.set(Calendar.YEAR, fechaHora.get(Calendar.YEAR));
        mediaNocheCalendar.set(Calendar.MONTH, fechaHora.get(Calendar.MONTH));
        mediaNocheCalendar.set(Calendar.DAY_OF_MONTH, fechaHora.get(Calendar.DAY_OF_MONTH));
        mediaNocheCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mediaNocheCalendar.set(Calendar.MINUTE, 0);
        mediaNocheCalendar.set(Calendar.SECOND, 0);
        Date mediaNoche = mediaNocheCalendar.getTime();
        return mediaNoche;
    }

    //EN ESTA FUNCIÓN ESTABLECEMOS SIETEAM
    private Date establecerSieteAm(Calendar fechaHora){
        Calendar sieteAmCalendar = Calendar.getInstance();
        sieteAmCalendar.set(Calendar.YEAR, fechaHora.get(Calendar.YEAR));
        sieteAmCalendar.set(Calendar.MONTH, fechaHora.get(Calendar.MONTH));
        sieteAmCalendar.set(Calendar.DAY_OF_MONTH, fechaHora.get(Calendar.DAY_OF_MONTH));
        sieteAmCalendar.set(Calendar.HOUR_OF_DAY, 7);
        sieteAmCalendar.set(Calendar.MINUTE, 0);
        sieteAmCalendar.set(Calendar.SECOND, 0);
        Date sieteAm = sieteAmCalendar.getTime();
        return sieteAm;
    }

    //EN ESTA FUNCIÓN ESTABLECEMOS OCHOPM
    private Date establecerOchoPm(Calendar fechaHora){
        Calendar OchoPmCalendar = Calendar.getInstance();
        OchoPmCalendar.set(Calendar.YEAR, fechaHora.get(Calendar.YEAR));
        OchoPmCalendar.set(Calendar.MONTH, fechaHora.get(Calendar.MONTH));
        OchoPmCalendar.set(Calendar.DAY_OF_MONTH, fechaHora.get(Calendar.DAY_OF_MONTH));
        OchoPmCalendar.set(Calendar.HOUR_OF_DAY, 20);
        OchoPmCalendar.set(Calendar.MINUTE, 0);
        OchoPmCalendar.set(Calendar.SECOND, 0);
        Date OchoPm = OchoPmCalendar.getTime();
        return OchoPm;
    }
}
