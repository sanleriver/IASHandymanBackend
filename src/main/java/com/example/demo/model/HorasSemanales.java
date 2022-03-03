package com.example.demo.model;

import lombok.Data;

@Data
public class HorasSemanales {

    private double cantidadHorasNormales;

    private double cantidadHorasNocturnas;

    private double cantidadHorasDominicales;

    private double cantidadHorasNormalesExtra;

    private double cantidadHorasNocturnasExtra;

    private double cantidadHorasDominicalesExtra;

    public HorasSemanales() {
    }

    public HorasSemanales(double cantidadHorasNormales, double cantidadHorasNocturnas, double cantidadHorasDominicales, double cantidadHorasNormalesExtra, double cantidadHorasNocturnasExtra, double cantidadHorasDominicalesExtra) {
        this.cantidadHorasNormales = cantidadHorasNormales;
        this.cantidadHorasNocturnas = cantidadHorasNocturnas;
        this.cantidadHorasDominicales = cantidadHorasDominicales;
        this.cantidadHorasNormalesExtra = cantidadHorasNormalesExtra;
        this.cantidadHorasNocturnasExtra = cantidadHorasNocturnasExtra;
        this.cantidadHorasDominicalesExtra = cantidadHorasDominicalesExtra;
    }
}
