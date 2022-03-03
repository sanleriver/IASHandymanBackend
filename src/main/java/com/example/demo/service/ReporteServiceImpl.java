package com.example.demo.service;

import com.example.demo.entity.Reporte;
import com.example.demo.model.HorasSemanales;
import com.example.demo.repository.ReporteRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public HorasSemanales getHoursReport(long tecnicoId, int semana) {
        DateTime inicio = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(semana)
                .withDayOfWeek(1)
                .withHourOfDay(00)
                .withMinuteOfHour(00)
                .withSecondOfMinute(00);
        DateTime fin = new DateTime()
                .withWeekyear(2022)
                .withWeekOfWeekyear(semana)
                .withDayOfWeek(7)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59);

        String formatoInicio = inicio.toString("yyyy-MM-dd HH:mm:ss");
        String formatoFin = fin.toString("yyyy-MM-dd HH:mm:ss");

        List<Reporte> repor = reporteRepository.getHoursByTecnico(tecnicoId, formatoInicio, formatoFin);
        HorasSemanales horasSemanales = new HorasSemanales();
        double horasTrabajadasSemana = 0.0;

        for (int i=0; i< repor.size(); i++){
            //EXTRAEMOS FECHA Y HORA INICIAL Y FINAL EN VARIABLE CALENDAR PARA EL TRATAMIENTO DE FECHAS EN LAS CONDICIONES
            Calendar horaInicio = Calendar.getInstance();
            horaInicio.setTime(repor.get(i).getFecha_hora_inicio());
            Calendar horaFin = Calendar.getInstance();
            horaFin.setTime(repor.get(i).getFecha_hora_fin());


            //ESTABLECIENDO LOS FORMATOS FIJO DE HORA 00:00:00, 07:00:00 Y 20:00:00
            Calendar mediaNocheCalendar = Calendar.getInstance();
            mediaNocheCalendar.set(Calendar.YEAR, horaInicio.get(Calendar.YEAR));
            mediaNocheCalendar.set(Calendar.MONTH, horaInicio.get(Calendar.MONTH));
            mediaNocheCalendar.set(Calendar.DAY_OF_MONTH, horaInicio.get(Calendar.DAY_OF_MONTH));
            mediaNocheCalendar.set(Calendar.HOUR_OF_DAY, 0);
            mediaNocheCalendar.set(Calendar.MINUTE, 0);
            mediaNocheCalendar.set(Calendar.SECOND, 0);
            Date mediaNoche = mediaNocheCalendar.getTime();

            Calendar sieteAMCalendar = Calendar.getInstance();
            sieteAMCalendar.set(Calendar.YEAR, horaInicio.get(Calendar.YEAR));
            sieteAMCalendar.set(Calendar.MONTH, horaInicio.get(Calendar.MONTH));
            sieteAMCalendar.set(Calendar.DAY_OF_MONTH, horaInicio.get(Calendar.DAY_OF_MONTH));
            sieteAMCalendar.set(Calendar.HOUR_OF_DAY, 7);
            sieteAMCalendar.set(Calendar.MINUTE, 0);
            sieteAMCalendar.set(Calendar.SECOND, 0);
            Date sieteAM = sieteAMCalendar.getTime();

            Calendar ochoPMCalendar = Calendar.getInstance();
            ochoPMCalendar.set(Calendar.YEAR, horaInicio.get(Calendar.YEAR));
            ochoPMCalendar.set(Calendar.MONTH, horaInicio.get(Calendar.MONTH));
            ochoPMCalendar.set(Calendar.DAY_OF_MONTH, horaInicio.get(Calendar.DAY_OF_MONTH));
            ochoPMCalendar.set(Calendar.HOUR_OF_DAY, 20);
            ochoPMCalendar.set(Calendar.MINUTE, 0);
            ochoPMCalendar.set(Calendar.SECOND, 0);
            Date ochoPM = ochoPMCalendar.getTime();

            /*VALIDACIÓN DE LAS HORAS PARA ASIGNARLAS SEGÚN SEA EL CASO
            VALIDAMOS SI SON HORAS EXTRA*/
            if (horasTrabajadasSemana <= 48){
                //VALIDAMOS SI ALGUNA DE LAS DOS FECHAS ES DOMINGO
                if (horaInicio.get(Calendar.DAY_OF_WEEK) == 1 || horaFin.get(Calendar.DAY_OF_WEEK) == 1){
                    //VALIDAMOS SI AMBAS FECHAS ES DOMINGO
                    if (horaInicio.get(Calendar.DAY_OF_WEEK) == 1 && horaFin.get(Calendar.DAY_OF_WEEK) == 1){
                        //CALCULAMOS LA CANTIDAD DE HORAS
                        double horasCantidad = (double)(repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS DOMINICALES
                        horasSemanales.setCantidadHorasDominicales(Math.round((horasSemanales.getCantidadHorasDominicales()+horasCantidad)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasCantidad;
                        //VALIDAMOS EL PRIMER DÍA ES SÁBADO
                    }else if (horaInicio.get(Calendar.DAY_OF_WEEK) == 7){
                        //CALCULAMOS LAS HORAS DOMINICALES CORRESPONDIENTES
                        double horasDominicales = (double)(repor.get(i).getFecha_hora_fin().getTime() - mediaNoche.getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS DOMINICALES
                        horasSemanales.setCantidadHorasDominicales(Math.round((horasSemanales.getCantidadHorasDominicales()+horasDominicales)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasDominicales;
                        //VALIDAMOS SI TODAS LAS HORAS NO DOMINICALES SON NOCTURNAS
                        if (repor.get(i).getFecha_hora_inicio().getTime() > ochoPM.getTime()){
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = (double)(mediaNoche.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                        } else {
                            //DECLARAMOS LAS 4 HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = 4;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNormales = (double)(ochoPM.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNormales(Math.round((horasSemanales.getCantidadHorasNormales()+horasNormales)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas + horasNormales;
                        }
                        //EN CASO DE QUE EL PRIMER DÍA ES DOMINGO Y EL SEGUNDO ES LUNES
                    }else{
                        //ESTABLECEMOS LAS 7 AM DEL SEGUNDO DIA
                        mediaNocheCalendar.set(Calendar.YEAR, horaFin.get(Calendar.YEAR));
                        mediaNocheCalendar.set(Calendar.MONTH, horaFin.get(Calendar.MONTH));
                        mediaNocheCalendar.set(Calendar.DAY_OF_MONTH, horaFin.get(Calendar.DAY_OF_MONTH));
                        mediaNocheCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        mediaNocheCalendar.set(Calendar.MINUTE, 0);
                        mediaNocheCalendar.set(Calendar.SECOND, 0);
                        mediaNoche = mediaNocheCalendar.getTime();

                        sieteAMCalendar.set(Calendar.YEAR, horaFin.get(Calendar.YEAR));
                        sieteAMCalendar.set(Calendar.MONTH, horaFin.get(Calendar.MONTH));
                        sieteAMCalendar.set(Calendar.DAY_OF_MONTH, horaFin.get(Calendar.DAY_OF_MONTH));
                        sieteAMCalendar.set(Calendar.HOUR_OF_DAY, 7);
                        sieteAMCalendar.set(Calendar.MINUTE, 0);
                        sieteAMCalendar.set(Calendar.SECOND, 0);
                        sieteAM = sieteAMCalendar.getTime();
                        //CALCULAMOS LAS HORAS DOMINICALES CORRESPONDIENTES
                        double horasDominicales = (double)(mediaNoche.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS DOMINICALES
                        horasSemanales.setCantidadHorasDominicales(Math.round((horasSemanales.getCantidadHorasDominicales()+horasDominicales)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasDominicales;
                        //VALIDAMOS SI TODAS LAS HORAS NO DOMINICALES SON NOCTURNAS
                        if (repor.get(i).getFecha_hora_inicio().getTime() < sieteAM.getTime()){
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = (double)(repor.get(i).getFecha_hora_fin().getTime() - mediaNoche.getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                        } else {
                            //DECLARAMOS LAS 7 HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = 7;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNormales = (double)(repor.get(i).getFecha_hora_inicio().getTime() - sieteAM.getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNormales(Math.round((horasSemanales.getCantidadHorasNormales()+horasNormales)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas + horasNormales;
                        }
                    }
                    //CUANDO NINGUNA DE LAS DOS FECHAS ES DOMINGO
                } else {
                    //VALIDAMOS SI LA HORA INICIAL ES ANTES DE LAS 7 AM
                    if (repor.get(i).getFecha_hora_inicio().getTime() < sieteAM.getTime()){
                        //VALIDAMOS SI LA HORA FINAL ES ANTES DE LAS 7 AM
                        if (repor.get(i).getFecha_hora_fin().getTime() < sieteAM.getTime()){
                            //CALCULAMOS LAS HORAS NOCTURNAS
                            double horasNocturnas = (double)(repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                        } else {
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = (double)(sieteAM.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                            //VALIDAMOS SI LA HORA FINAL ES DESPUÉS DE LAS 8 PM
                            if (repor.get(i).getFecha_hora_fin().getTime() > ochoPM.getTime()){
                                //DECLARAMOS LAS 13 HORAS NORMALES CORRESPONDIENTES
                                double horasNormales = 13;
                                //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NORMALES
                                horasSemanales.setCantidadHorasNormales(Math.round((horasSemanales.getCantidadHorasNormales()+horasNormales)*100)/100d);
                                //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                                double horasNocturnas2 = (double)(repor.get(i).getFecha_hora_fin().getTime() - ochoPM.getTime())/(double)3600000;
                                //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                                horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas2)*100)/100d);
                                //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                                horasTrabajadasSemana += horasNocturnas2 + horasNormales;
                            } else {
                                //CALCULAMOS LAS HORAS NORMALES CORRESPONDIENTES
                                double horasNormales = (double)(repor.get(i).getFecha_hora_fin().getTime() - sieteAM.getTime())/(double)3600000;
                                //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                                horasSemanales.setCantidadHorasNormales(Math.round((horasSemanales.getCantidadHorasNormales()+horasNormales)*100)/100d);
                                //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                                horasTrabajadasSemana += horasNormales;
                            }
                        }
                        //VALIDAMOS SI LA HORA INICIAL ES DESPUÉS DE LAS 8 PM
                    } else if (repor.get(i).getFecha_hora_inicio().getTime() > ochoPM.getTime()){
                        //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                        double horasNocturnas = (double)(repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                        horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasNocturnas;
                        //VALIDAMOS SI LA HORA FINAL ES DESPUÉS DE LAS 8 PM
                    } else if (repor.get(i).getFecha_hora_inicio().getTime() > ochoPM.getTime()){
                        //CALCULAMOS LAS HORAS NORMALES CORRESPONDIENTES
                        double horasNormales = (double)(ochoPM.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NORMALES
                        horasSemanales.setCantidadHorasNormales(Math.round((horasSemanales.getCantidadHorasNormales()+horasNormales)*100)/100d);
                        //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                        double horasNocturnas = (double)(repor.get(i).getFecha_hora_fin().getTime() - ochoPM.getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                        horasSemanales.setCantidadHorasNocturnas(Math.round((horasSemanales.getCantidadHorasNocturnas()+horasNocturnas)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasNocturnas + horasNormales;
                    } else {
                        //CALCULAMOS LAS HORAS NORMALES
                        double horasNormales = (double)(repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NORMALES
                        horasSemanales.setCantidadHorasNormales(Math.round((horasSemanales.getCantidadHorasNormales()+horasNormales)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasNormales;
                    }
                }
            //AQUÍ TERMINA CUANDO LAS HORAS NO SON EXTRA
            } else {
                //VALIDAMOS SI ALGUNA DE LAS DOS FECHAS ES DOMINGO
                if (horaInicio.get(Calendar.DAY_OF_WEEK) == 1 || horaFin.get(Calendar.DAY_OF_WEEK) == 1){
                    //VALIDAMOS SI AMBAS FECHAS ES DOMINGO
                    if (horaInicio.get(Calendar.DAY_OF_WEEK) == 1 && horaFin.get(Calendar.DAY_OF_WEEK) == 1){
                        //CALCULAMOS LA CANTIDAD DE HORAS
                        double horasCantidad = (double)(repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS DOMINICALES
                        horasSemanales.setCantidadHorasDominicalesExtra(Math.round((horasSemanales.getCantidadHorasDominicalesExtra()+horasCantidad)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasCantidad;
                        //VALIDAMOS EL PRIMER DÍA ES SÁBADO
                    }else if (horaInicio.get(Calendar.DAY_OF_WEEK) == 7){
                        //CALCULAMOS LAS HORAS DOMINICALES CORRESPONDIENTES
                        double horasDominicales = (double)(repor.get(i).getFecha_hora_fin().getTime() - mediaNoche.getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS DOMINICALES
                        horasSemanales.setCantidadHorasDominicalesExtra(Math.round((horasSemanales.getCantidadHorasDominicalesExtra()+horasDominicales)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasDominicales;
                        //VALIDAMOS SI TODAS LAS HORAS NO DOMINICALES SON NOCTURNAS
                        if (repor.get(i).getFecha_hora_inicio().getTime() > ochoPM.getTime()){
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = (double)(mediaNoche.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra()+horasNocturnas)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                        } else {
                            //DECLARAMOS LAS 4 HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = 4;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra()+horasNocturnas)*100)/100d);
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNormales = (double)(ochoPM.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNormalesExtra(Math.round((horasSemanales.getCantidadHorasNormalesExtra()+horasNormales)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas + horasNormales;
                        }
                        //EN CASO DE QUE EL PRIMER DÍA ES DOMINGO Y EL SEGUNDO ES LUNES
                    }else{
                        //ESTABLECEMOS LAS 7 AM DEL SEGUNDO DIA
                        mediaNocheCalendar.set(Calendar.YEAR, horaFin.get(Calendar.YEAR));
                        mediaNocheCalendar.set(Calendar.MONTH, horaFin.get(Calendar.MONTH));
                        mediaNocheCalendar.set(Calendar.DAY_OF_MONTH, horaFin.get(Calendar.DAY_OF_MONTH));
                        mediaNocheCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        mediaNocheCalendar.set(Calendar.MINUTE, 0);
                        mediaNocheCalendar.set(Calendar.SECOND, 0);
                        mediaNoche = mediaNocheCalendar.getTime();

                        sieteAMCalendar.set(Calendar.YEAR, horaFin.get(Calendar.YEAR));
                        sieteAMCalendar.set(Calendar.MONTH, horaFin.get(Calendar.MONTH));
                        sieteAMCalendar.set(Calendar.DAY_OF_MONTH, horaFin.get(Calendar.DAY_OF_MONTH));
                        sieteAMCalendar.set(Calendar.HOUR_OF_DAY, 7);
                        sieteAMCalendar.set(Calendar.MINUTE, 0);
                        sieteAMCalendar.set(Calendar.SECOND, 0);
                        sieteAM = sieteAMCalendar.getTime();
                        //CALCULAMOS LAS HORAS DOMINICALES CORRESPONDIENTES
                        double horasDominicales = (double)(mediaNoche.getTime() - repor.get(i).getFecha_hora_inicio().getTime())/(double)3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS DOMINICALES
                        horasSemanales.setCantidadHorasDominicalesExtra(Math.round((horasSemanales.getCantidadHorasDominicalesExtra()+horasDominicales)*100)/100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasDominicales;
                        //VALIDAMOS SI TODAS LAS HORAS NO DOMINICALES SON NOCTURNAS
                        if (repor.get(i).getFecha_hora_inicio().getTime() < sieteAM.getTime()){
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = (double)(repor.get(i).getFecha_hora_fin().getTime() - mediaNoche.getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra()+horasNocturnas)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                        } else {
                            //DECLARAMOS LAS 7 HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = 7;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra()+horasNocturnas)*100)/100d);
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNormales = (double)(repor.get(i).getFecha_hora_inicio().getTime() - sieteAM.getTime())/(double)3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNormalesExtra(Math.round((horasSemanales.getCantidadHorasNormalesExtra()+horasNormales)*100)/100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas + horasNormales;
                        }
                    }
                    //CUANDO NINGUNA DE LAS DOS FECHAS ES DOMINGO
                } else {
                    //VALIDAMOS SI LA HORA INICIAL ES ANTES DE LAS 7 AM
                    if (repor.get(i).getFecha_hora_inicio().getTime() < sieteAM.getTime()) {
                        //VALIDAMOS SI LA HORA FINAL ES ANTES DE LAS 7 AM
                        if (repor.get(i).getFecha_hora_fin().getTime() < sieteAM.getTime()) {
                            //CALCULAMOS LAS HORAS NOCTURNAS
                            double horasNocturnas = (double) (repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime()) / (double) 3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra() + horasNocturnas) * 100) / 100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                        } else {
                            //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                            double horasNocturnas = (double) (sieteAM.getTime() - repor.get(i).getFecha_hora_inicio().getTime()) / (double) 3600000;
                            //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                            horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra() + horasNocturnas) * 100) / 100d);
                            //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                            horasTrabajadasSemana += horasNocturnas;
                            //VALIDAMOS SI LA HORA FINAL ES DESPUÉS DE LAS 8 PM
                            if (repor.get(i).getFecha_hora_fin().getTime() > ochoPM.getTime()) {
                                //DECLARAMOS LAS 13 HORAS NORMALES CORRESPONDIENTES
                                double horasNormales = 13;
                                //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NORMALES
                                horasSemanales.setCantidadHorasNormalesExtra(Math.round((horasSemanales.getCantidadHorasNormalesExtra() + horasNormales) * 100) / 100d);
                                //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                                double horasNocturnas2 = (double) (repor.get(i).getFecha_hora_fin().getTime() - ochoPM.getTime()) / (double) 3600000;
                                //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                                horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra() + horasNocturnas2) * 100) / 100d);
                                //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                                horasTrabajadasSemana += horasNocturnas2 + horasNormales;
                            } else {
                                //CALCULAMOS LAS HORAS NORMALES CORRESPONDIENTES
                                double horasNormales = (double) (repor.get(i).getFecha_hora_fin().getTime() - sieteAM.getTime()) / (double) 3600000;
                                //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                                horasSemanales.setCantidadHorasNormalesExtra(Math.round((horasSemanales.getCantidadHorasNormalesExtra() + horasNormales) * 100) / 100d);
                                //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                                horasTrabajadasSemana += horasNormales;
                            }
                        }
                        //VALIDAMOS SI LA HORA INICIAL ES DESPUÉS DE LAS 8 PM
                    } else if (repor.get(i).getFecha_hora_inicio().getTime() > ochoPM.getTime()) {
                        //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                        double horasNocturnas = (double) (repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime()) / (double) 3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                        horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra() + horasNocturnas) * 100) / 100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasNocturnas;
                        //VALIDAMOS SI LA HORA FINAL ES DESPUÉS DE LAS 8 PM
                    } else if (repor.get(i).getFecha_hora_inicio().getTime() > ochoPM.getTime()) {
                        //CALCULAMOS LAS HORAS NORMALES CORRESPONDIENTES
                        double horasNormales = (double) (ochoPM.getTime() - repor.get(i).getFecha_hora_inicio().getTime()) / (double) 3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NORMALES
                        horasSemanales.setCantidadHorasNormalesExtra(Math.round((horasSemanales.getCantidadHorasNormalesExtra() + horasNormales) * 100) / 100d);
                        //CALCULAMOS LAS HORAS NOCTURNAS CORRESPONDIENTES
                        double horasNocturnas = (double) (repor.get(i).getFecha_hora_fin().getTime() - ochoPM.getTime()) / (double) 3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NOCTURNAS
                        horasSemanales.setCantidadHorasNocturnasExtra(Math.round((horasSemanales.getCantidadHorasNocturnasExtra() + horasNocturnas) * 100) / 100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasNocturnas + horasNormales;
                    } else {
                        //CALCULAMOS LAS HORAS NORMALES
                        double horasNormales = (double) (repor.get(i).getFecha_hora_fin().getTime() - repor.get(i).getFecha_hora_inicio().getTime()) / (double) 3600000;
                        //ASIGNAMOS LA CANTIDAD DE HORAS A HORAS NORMALES
                        horasSemanales.setCantidadHorasNormalesExtra(Math.round((horasSemanales.getCantidadHorasNormalesExtra() + horasNormales) * 100) / 100d);
                        //SUMAMOS LA CANTIDAD A LAS HORAS TRABAJADAS EN LA SEMANA
                        horasTrabajadasSemana += horasNormales;
                    }
                    //AQUÍ TERMINA CUANDO LAS HORAS SON EXTRA
                }
            }
        }

        return horasSemanales;
    }
}
