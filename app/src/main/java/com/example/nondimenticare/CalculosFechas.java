package com.example.nondimenticare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculosFechas {

    public String nombreMes (int mes){
        String nombreMes = "";
        switch (mes){
            case 1:
                nombreMes = "Enero";
                break;
            case 2:
                nombreMes = "Febrero";
                break;
            case 3:
                nombreMes = "Marzo";
                break;
            case 4:
                nombreMes = "Abril";
                break;
            case 5:
                nombreMes = "Mayo";
                break;
            case 6:
                nombreMes = "Junio";
                break;
            case 7:
                nombreMes = "Julio";
                break;
            case 8:
                nombreMes = "Agosto";
                break;
            case 9:
                nombreMes = "Septiembre";
                break;
            case 10:
                nombreMes = "Octubre";
                break;
            case 11:
                nombreMes = "Noviembre";
                break;
            case 12:
                nombreMes = "Diciembre";
                break;
        }
        return nombreMes;
    }

    public int numeroMes (String nombreMes){
        int mes = 0;
        switch (nombreMes){
            case "Enero":
                mes = 1;
                break;
            case "Febrero":
                mes = 2;
                break;
            case "Marzo":
                mes = 3;
                break;
            case "Abril":
                mes = 4;
                break;
            case "Mayo":
                mes = 5;
                break;
            case "Junio":
                mes = 6;
                break;
            case "Julio":
                mes = 7;
                break;
            case "Agosto":
                mes = 8;
                break;
            case "Septiembre":
                mes = 9;
                break;
            case "Octubre":
                mes = 10;
                break;
            case "Noviembre":
                mes = 11;
                break;
            case "Diciembre":
                mes = 12;
                break;
        }
        return mes;
    }

    public int diasRestantes(String sFecha){
        int dias = 0;
        boolean iter = false;
        Calendar calendar;
        Date aux;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fecha = sdf.parse(sFecha);
            do{
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, dias);
                aux = calendar.getTime();
                if(sdf.format(aux).equals(sdf.format(fecha)))
                    iter = true;
                else
                    dias++;
            }while(iter != true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dias;
    }
}
