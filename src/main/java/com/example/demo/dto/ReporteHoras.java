package com.example.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReporteHoras {

    private long id;

    private Date inicio;

    private Date fin;
}
