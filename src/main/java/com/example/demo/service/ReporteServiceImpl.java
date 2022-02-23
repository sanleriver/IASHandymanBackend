package com.example.demo.service;

import com.example.demo.dto.Consulta;
import com.example.demo.entity.Reporte;
import com.example.demo.model.HorasSemanales;
import com.example.demo.repository.ReporteRepository;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public HorasSemanales getHoursReport(Consulta consulta) {
        DateTime inicio = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(consulta.getSemana())
                .withDayOfWeek(1)
                .withHourOfDay(00)
                .withMinuteOfHour(00)
                .withSecondOfMinute(00);
        DateTime fin = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(consulta.getSemana())
                .withDayOfWeek(7)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59);

        String formatoInicio = inicio.toString("yyyy-MM-dd HH:mm:ss");
        String formatoFin = fin.toString("yyyy-MM-dd HH:mm:ss");

        List<Reporte> repor = reporteRepository.getHoursByTecnico(consulta.getTecnico_id(), formatoInicio, formatoFin);
        HorasSemanales horasSemanales = new HorasSemanales();
        double horasTrabajadasSemana = 0;
        double horasCantidad = 0.0;

        for (int i=0; i< repor.size(); i++){
            horasCantidad = (double)(repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;

            Calendar horaInicio = Calendar.getInstance();
            horaInicio.setTime(repor.get(i).getFecha_hora_inicio());
            Calendar horaFin = Calendar.getInstance();
            horaInicio.setTime(repor.get(i).getFecha_hora_fin());

            int prueba = horaInicio.get(Calendar.HOUR_OF_DAY);
            int prueba2 = horaFin.get(Calendar.HOUR_OF_DAY);
            //HORAS NORMALES
            if (horaInicio.get(Calendar.HOUR_OF_DAY)>7 && horaFin.get(Calendar.HOUR_OF_DAY)< 20 && horasTrabajadasSemana <= 48 && horaInicio.get(Calendar.DAY_OF_WEEK) != 7){
                horasSemanales.setCantidadHorasNormales(horasCantidad);
                horasTrabajadasSemana += horasCantidad;
            }

            //HORAS NOCTURNAS
            if (horaInicio.get(Calendar.HOUR_OF_DAY)>20 && horaFin.get(Calendar.HOUR_OF_DAY)< 7 && horasTrabajadasSemana <= 48 && horaInicio.get(Calendar.DAY_OF_WEEK) != 7){
                horasSemanales.setCantidadHorasNocturnas(horasCantidad);
                horasTrabajadasSemana += horasCantidad;
            }

            //HORAS DOMINICALES
            if (horasTrabajadasSemana <= 48 && horaInicio.get(Calendar.DAY_OF_WEEK) == 7){
                horasSemanales.setCantidadHorasDominicales(horasCantidad);
                horasTrabajadasSemana += horasCantidad;
            }

            //HORAS NORMALES EXTRA
            if (horaInicio.get(Calendar.HOUR_OF_DAY)>7 && horaFin.get(Calendar.HOUR_OF_DAY)< 20 && horasTrabajadasSemana > 48 && horaInicio.get(Calendar.DAY_OF_WEEK) != 7){
                horasSemanales.setCantidadHorasNormalesExtra(horasCantidad);
                horasTrabajadasSemana += horasCantidad;
            }

            //HORAS NOCTURNAS EXTRA
            if (horaInicio.get(Calendar.HOUR_OF_DAY)>20 && horaFin.get(Calendar.HOUR_OF_DAY)< 7 && horasTrabajadasSemana > 48 && horaInicio.get(Calendar.DAY_OF_WEEK) != 7){
                horasSemanales.setCantidadHorasNocturnasExtra(horasCantidad);
                horasTrabajadasSemana += horasCantidad;
            }

            //HORAS DOMINICALES EXTRA
            if (horasTrabajadasSemana > 48 && horaInicio.get(Calendar.DAY_OF_WEEK) == 7){
                horasSemanales.setCantidadHorasDominicalesExtra(horasCantidad);
                horasTrabajadasSemana += horasCantidad;
            }
        }

        return horasSemanales;
    }
}
