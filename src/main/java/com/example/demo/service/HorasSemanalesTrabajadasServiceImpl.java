package com.example.demo.service;

import com.example.demo.entity.HorasSemanalesTrabajadas;
import com.example.demo.repository.HorasSemanalesTrabajadasRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sun.jdi.PrimitiveValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class HorasSemanalesTrabajadasServiceImpl implements HorasSemanalesTrabajadasService {

    @Autowired
    private HorasSemanalesTrabajadasRepository horasSemanalesTrabajadasRepository;

    @Override
    public HorasSemanalesTrabajadas save(HorasSemanalesTrabajadas horasSemanalesTrabajas) {
        return horasSemanalesTrabajadasRepository.save(horasSemanalesTrabajas);
    }

    @Override
    public Optional<HorasSemanalesTrabajadas> findById(long id) {
        return horasSemanalesTrabajadasRepository.findById(id);
    }

    @Override
    public HorasSemanalesTrabajadas actualizarHorasSemana(long tecnico_id, LocalDateTime fechaHoraInicial, LocalDateTime fechaHoraFinal) {
        List<HorasSemanalesTrabajadas> horasSemanalesTrabajadas = new ArrayList<>();
        List<Double> totalHorasTrabajadasSem = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semanaInicial = fechaHoraInicial.get(weekFields.weekOfWeekBasedYear());
        int semanaFinal = fechaHoraFinal.get(weekFields.weekOfWeekBasedYear());
        for (int i = semanaInicial; i<= semanaFinal; i++){
            HorasSemanalesTrabajadas horasST = horasSemanalesTrabajadasRepository.obtenerRegistroHoras(tecnico_id, i);
            double totalHorasTrabajadas = horasST.getCantidadHorasNormales() + horasST.getCantidadHorasNocturnas()
                    + horasST.getCantidadHorasDominicales() + horasST.getCantidadHorasNormalesExtra()
                    + horasST.getCantidadHorasNocturnasExtra() + horasST.getCantidadHorasDominicalesExtra();
            horasSemanalesTrabajadas.add(horasST);
            totalHorasTrabajadasSem.add(totalHorasTrabajadas);
        }

        //VALIDAMOS SI ALGUNA DE LAS FECHAS ES DOMINGO
        if (fechaHoraInicial.getDayOfWeek() == DayOfWeek.SUNDAY || fechaHoraFinal.getDayOfWeek() == DayOfWeek.SUNDAY) {

            //EN CASO DE QUE NINGUNA DE LAS FECHAS SEA DOMINGO
        } else {
            //VALIDAMOS SI LAS FECHAS SON EL MISMO DÍA
            if (ChronoUnit.DAYS.between(fechaHoraInicial, fechaHoraFinal) < 1) {

                //EN CASO DE QUE LAS FECHAS SEAN EN DÍAS DIFERENTES
            } else {
                List<Double> horasDiaInicial = horasDiaInicial(fechaHoraInicial);
                horasSemanalesTrabajadas.set(0, guardarCantidadHoras(horasSemanalesTrabajadas.get(0), totalHorasTrabajadasSem.get(0), horasDiaInicial));
                totalHorasTrabajadasSem.set(0, totalHorasTrabajadasSem.get(0) + horasDiaInicial.get(0) + horasDiaInicial.get(1));
                //AQUI VA LA VALIDACION DEL DOMINGO
                //VALIDAMOS SI LA FECHA FINAL ES UN DÍA DESPUÉS DE LA FECHA INICIAL
                if (fechaHoraFinal.isBefore(fechaHoraInicial.plusDays(1))) {
                    List<Double> horasDiaFinal = horasDiaFinal(fechaHoraFinal);
                    horasSemanalesTrabajadas.set(0, guardarCantidadHoras(horasSemanalesTrabajadas.get(0), totalHorasTrabajadasSem.get(0), horasDiaFinal));
                    totalHorasTrabajadasSem.set(0, totalHorasTrabajadasSem.get(0) + horasDiaFinal.get(0) + horasDiaFinal.get(1) + horasDiaFinal.get(2));
                //SI NO ES UN DÍA DESPUÉS VALIDAMOS SI HAY UN DOMINGO EN MEDIO
                } else if (horasSemanalesTrabajadas.size() > 1){
                    LocalDate domingoSemana1 = LocalDate.now()
                            .withYear(fechaHoraInicial.getYear())
                            .with(weekFields.weekOfYear(), semanaInicial)
                            .with(weekFields.dayOfWeek(), 7);
                    LocalTime medianoche = LocalTime.MIDNIGHT;
                    LocalDateTime domingoConHora = LocalDateTime.of(domingoSemana1, medianoche);
                    int cantidadDiasSemana1 = (int) ChronoUnit.DAYS.between(fechaHoraInicial, domingoConHora);
                    int cantidadDiasSemana2 = (int) ChronoUnit.DAYS.between(domingoConHora.plusDays(1), fechaHoraFinal);
                    for (int i = 0; i < cantidadDiasSemana1; i++){
                        List<Double> horasDiaIntermedio = horasDiaIntermedio(fechaHoraInicial.plusDays(i));
                        horasSemanalesTrabajadas.set(0, guardarCantidadHoras(horasSemanalesTrabajadas.get(0), totalHorasTrabajadasSem.get(0), horasDiaIntermedio));
                        totalHorasTrabajadasSem.set(0, totalHorasTrabajadasSem.get(0) + horasDiaIntermedio.get(0) + horasDiaIntermedio.get(1) + horasDiaIntermedio.get(2));
                    }
                    horasSemanalesTrabajadas.set(0, guardarCantidadHorasdDominicales(horasSemanalesTrabajadas.get(0), totalHorasTrabajadasSem.get(0), 24.00));
                    totalHorasTrabajadasSem.set(0, totalHorasTrabajadasSem.get(0) + 24.00);
                    for (int i = 0; i < cantidadDiasSemana2; i++){
                        List<Double> horasDiaIntermedio = horasDiaIntermedio(domingoConHora.plusDays(1).plusDays(i));
                        horasSemanalesTrabajadas.set(1, guardarCantidadHoras(horasSemanalesTrabajadas.get(1), totalHorasTrabajadasSem.get(1), horasDiaIntermedio));
                        totalHorasTrabajadasSem.set(1, totalHorasTrabajadasSem.get(1) + horasDiaIntermedio.get(0) + horasDiaIntermedio.get(1) + horasDiaIntermedio.get(2));
                    }
                    //SI NO HAY UN DOMINGO ENTRE LAS DOS FECHAS
                } else {

                }
            }
        }

        return horasSemanalesTrabajadas.get(0);
    }
    //ESTA FUNCIÓN PERMITE GUARDAR VALIABLES CALCULADAS DE HORAS NORCTURNAS y NORMALES
    private HorasSemanalesTrabajadas guardarCantidadHoras(HorasSemanalesTrabajadas horasSemanalesTrabajadas,
                                                          double totalHorasTrabajadasSem,
                                                          List<Double> horasDia){
        List<Double> horasExtrayNoExtra = new ArrayList<>();
        horasExtrayNoExtra = definirHorasExtra(totalHorasTrabajadasSem, horasDia.get(0));
        horasSemanalesTrabajadas.setCantidadHorasNocturnas(Math.round((horasSemanalesTrabajadas.getCantidadHorasNocturnas() + horasExtrayNoExtra.get(1)) * 100) / 100d);
        horasSemanalesTrabajadas.setCantidadHorasNocturnasExtra(Math.round((horasSemanalesTrabajadas.getCantidadHorasNocturnasExtra() + horasExtrayNoExtra.get(0)) * 100) / 100d);
        horasExtrayNoExtra = definirHorasExtra(totalHorasTrabajadasSem+horasDia.get(0), horasDia.get(1));
        horasSemanalesTrabajadas.setCantidadHorasNormales(Math.round((horasSemanalesTrabajadas.getCantidadHorasNormales() + horasExtrayNoExtra.get(1)) * 100) / 100d);
        horasSemanalesTrabajadas.setCantidadHorasNormalesExtra(Math.round((horasSemanalesTrabajadas.getCantidadHorasNormalesExtra() + horasExtrayNoExtra.get(0)) * 100) / 100d);
        horasExtrayNoExtra = definirHorasExtra(totalHorasTrabajadasSem+horasDia.get(0)+horasDia.get(1), horasDia.get(2));
        horasSemanalesTrabajadas.setCantidadHorasNocturnas(Math.round((horasSemanalesTrabajadas.getCantidadHorasNocturnas() + horasExtrayNoExtra.get(1)) * 100) / 100d);
        horasSemanalesTrabajadas.setCantidadHorasNocturnasExtra(Math.round((horasSemanalesTrabajadas.getCantidadHorasNocturnasExtra() + horasExtrayNoExtra.get(0)) * 100) / 100d);
        return horasSemanalesTrabajadas;
    }

    //ESTA FUNCIÓN PERMITE GUARDAR VALIABLES CALCULADAS DE HORAS DOMINICALES
    private HorasSemanalesTrabajadas guardarCantidadHorasdDominicales(HorasSemanalesTrabajadas horasSemanalesTrabajadas,
                                                          double totalHorasTrabajadasSem,
                                                          double horasDominicales){
        List<Double> horasExtrayNoExtra = definirHorasExtra(totalHorasTrabajadasSem, horasDominicales);
        horasSemanalesTrabajadas.setCantidadHorasDominicales(Math.round((horasSemanalesTrabajadas.getCantidadHorasDominicales() + horasExtrayNoExtra.get(1)) * 100) / 100d);
        horasSemanalesTrabajadas.setCantidadHorasDominicalesExtra(Math.round((horasSemanalesTrabajadas.getCantidadHorasDominicalesExtra() + horasExtrayNoExtra.get(0)) * 100) / 100d);
        return horasSemanalesTrabajadas;
    }

    //EN ESTA FUNCIÓN SE INCREMENTAN LAS HORAS TRABAJADAS EN TOTAL
    private double incrementarHorasTrabajadas(double actual, double nuevo){
        return 2.0;
    }

    //ESTA FUNCIÓN PERMITE CALCULAR LA CANTIDAD DE HORAS
    private double calcularCatidadHoras(LocalDateTime horaInicio, LocalDateTime horaFin){
        double cantidadHoras = (double)ChronoUnit.SECONDS.between(horaInicio, horaFin)/(double)3600;
        return cantidadHoras;
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
    private LocalDateTime establecerMedianoche(LocalDateTime fechaHora){
        LocalTime medianoche = LocalTime.MIDNIGHT;
        LocalDate fecha = fechaHora.toLocalDate();
        LocalDateTime fechaMedianoche = LocalDateTime.of(fecha,medianoche);
        return fechaMedianoche;
    }

    //EN ESTA FUNCIÓN ESTABLECEMOS SIETEAM
    private LocalDateTime establecerSieteAm(LocalDateTime fechaHora){
        LocalTime sieteAm = LocalTime.of(07,00,00);
        LocalDate fecha = fechaHora.toLocalDate();
        LocalDateTime fechaSieteAm = LocalDateTime.of(fecha,sieteAm);
        return fechaSieteAm;
    }

    //EN ESTA FUNCIÓN ESTABLECEMOS OCHOPM
    private LocalDateTime establecerOchoPm(LocalDateTime fechaHora){
        LocalTime ochoPm = LocalTime.of(20,00,00);
        LocalDate fecha = fechaHora.toLocalDate();
        LocalDateTime fechaOchoPm = LocalDateTime.of(fecha,ochoPm);
        return fechaOchoPm;
    }

    //EN ESTA FUNCIÓN DEFINIMOS SI SON HORAS EXTRA O NO
    private List<Double> definirHorasExtra(double horasTrabajadas, double cantidadHorasActuales){
        List<Double> horas = new ArrayList<>();
        double horasExtra = 0.0;
        double horasNoExtra = 0.0;
        //VALIDAMOS SI HASTA EL MOMENTO LAS HORAS TRABAJADAS SON EXTRA
        if (horasTrabajadas > 48){
            horasExtra = cantidadHorasActuales;
            //SI NO SON EXTRA, VALIDAMOS SI AL SUMAR LAS ACTUALES LLEGA A EXTRA
        } else if (horasTrabajadas+cantidadHorasActuales > 48){
            double diferencia = 48 - horasTrabajadas;
            double restante = cantidadHorasActuales - diferencia;
            horasNoExtra = diferencia;
            horasExtra = restante;
        } else {
            horasNoExtra = cantidadHorasActuales;
        }
        horas.add(horasExtra);
        horas.add(horasNoExtra);
        return horas;
    }

    //EN ESTA FUNCIÓN SE CALCULAN LAS HORAS DE UN DÍA CUANDO ES FINAL
    private List<Double> horasDiaFinal(LocalDateTime fecha){
        List<Double> horas = new ArrayList<>();
        double horasNocturnasAntes = 0.0;
        double horasNormales = 0.0;
        double horasNocturnasDespues = 0.0;
        if (fecha.isBefore(establecerSieteAm(fecha))){
            horasNocturnasAntes = calcularCatidadHoras(establecerMedianoche(fecha), fecha);
        } else if (fecha.isBefore(establecerOchoPm(fecha))){
            horasNocturnasAntes = calcularCatidadHoras(establecerMedianoche(fecha), establecerSieteAm(fecha));
            horasNormales = calcularCatidadHoras(establecerSieteAm(fecha), fecha);
        } else {
            horasNocturnasAntes = calcularCatidadHoras(establecerMedianoche(fecha), establecerSieteAm(fecha));
            horasNormales = calcularCatidadHoras(establecerSieteAm(fecha), establecerOchoPm(fecha));
            horasNocturnasDespues = calcularCatidadHoras(establecerOchoPm(fecha), fecha);
        }
        horas.add(horasNocturnasAntes);
        horas.add(horasNormales);
        horas.add(horasNocturnasDespues);
        return horas;
    }

    //EN ESTA FUNCIÓN SE CALCULAN LAS HORAS DE UN DÍA CUANDO ES INICIAL
    private List<Double> horasDiaInicial(LocalDateTime fecha){
        List<Double> horas = new ArrayList<>();
        double horasNocturnasAntes = 0.0;
        double horasNormales = 0.0;
        double horasNocturnasDespues = 0.0;
        if (fecha.isBefore(establecerSieteAm(fecha))){
            horasNocturnasAntes = calcularCatidadHoras(fecha, establecerSieteAm(fecha));
            horasNormales = calcularCatidadHoras(establecerSieteAm(fecha), establecerOchoPm(fecha));
            horasNocturnasDespues = calcularCatidadHoras(establecerOchoPm(fecha), establecerMedianoche(fecha.plusDays(1)));
        } else if (fecha.isBefore(establecerOchoPm(fecha))){
            horasNormales = calcularCatidadHoras(fecha, establecerOchoPm(fecha));
            horasNocturnasDespues = calcularCatidadHoras(establecerOchoPm(fecha), establecerMedianoche(fecha.plusDays(1)));
        } else {
            horasNocturnasDespues = calcularCatidadHoras(fecha, establecerMedianoche(fecha.plusDays(1)));
        }
        horas.add(horasNocturnasAntes);
        horas.add(horasNormales);
        horas.add(horasNocturnasDespues);
        return horas;
    }

    //EN ESTA FUNCIÓN SE CALCULAN TODAS LAS HORAS DE UN DÍA COMPLETA
    private List<Double> horasDiaIntermedio(LocalDateTime fecha){
        List<Double> horas = new ArrayList<>();
        double horasNocturnasAntes = 0.0;
        double horasNormales = 0.0;
        double horasNocturnasDespues = 0.0;
        horasNocturnasAntes = calcularCatidadHoras(establecerMedianoche(fecha), establecerSieteAm(fecha));
        horasNormales = calcularCatidadHoras(establecerSieteAm(fecha), establecerOchoPm(fecha));
        horasNocturnasDespues = calcularCatidadHoras(establecerOchoPm(fecha), establecerMedianoche(fecha.plusDays(1)));
        horas.add(horasNocturnasAntes);
        horas.add(horasNormales);
        horas.add(horasNocturnasDespues);
        return horas;
    }
}
