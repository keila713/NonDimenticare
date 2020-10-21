package com.example.nondimenticare;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Valida {
    public Pattern patron;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public boolean esVacio(String dato){
        return dato.trim().equals("");
    }

    public boolean validaUsuario(String user){
        patron = Pattern.compile("^[A-Za-z0-9ñÑ]+$");
        return !patron.matcher(user).matches();
    }

    public boolean validaNombre_Ape(String nom_ape){
        patron = Pattern.compile("^([a-zA-ZáéíóúÁÉÍÓÚñÑ][a-zA-ZáéíóúÁÉÍÓÚñÑ ]*[a-zA-ZáéíóúÁÉÍÓÚñÑ])$");
        return !patron.matcher(nom_ape).matches();
    }

    public boolean validaFecha(String fecha){
        patron = Pattern.compile("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
        return (!patron.matcher(fecha).matches());
    }

    public boolean fechaPosteriorHoy(String fecha) {
        String hoy = sdf.format(new Date());
        boolean iter =false;
        try {
            Date date1 = sdf.parse(hoy);
            Date date2 = sdf.parse(fecha);
            if(date2.after(date1)){
                iter = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return iter;
    }

    public boolean fechaIgualHoy(String fecha) {
        String hoy = sdf.format(new Date());
        boolean iter =false;
        try {
            Date date1 = sdf.parse(hoy);
            Date date2 = sdf.parse(fecha);
            if(date2.equals(date1)){
                iter = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return iter;
    }

    public boolean validaCorreo (String correo){
        patron = Pattern.compile("^[_A-ZÑña-z0-9-\\+]+(\\.[_A-ZÑña-z0-9-]+)*@+[A-ZÑña-z0-9-]+(\\.[A-ZÑña-z0-9]+)*(\\.[A-ZÑña-z]{2,})$");
        return !patron.matcher(correo).matches();
    }

    public boolean validaPass (String pass){
        patron = Pattern.compile("^([0-9a-zA-ZÑñ])+$");
        return (!patron.matcher(pass).matches()) || pass.length() < 8 || pass.length() > 16;
    }



    public boolean comparaPass (String pass, String pass2){
        return !(pass.equals(pass2));
    }

}
