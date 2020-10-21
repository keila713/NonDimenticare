package com.example.nondimenticare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class Actualiza extends AppCompatActivity {

    Button btnActualizar;
    ImageButton ibtnVolver;
    TextInputLayout til_nombre, til_fechaC;
    private int año, mes, dia;
    String nombre = "", cumple="", nombreMes, fechaC, nombre_cump;
    int id_user, id_datos, nmes, ndia;
    private SharedPreferences shPref;
    final CalculosFechas cf = new CalculosFechas();
    final Valida valida = new Valida();
    boolean iter = false, existe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza);

        //REFERENCIA VARIABLE DE PREFERENCIA
        shPref = PreferenceManager.getDefaultSharedPreferences(this);

        //ID_USER
        String sId_user = shPref.getString(getString(R.string.id_user),"");
        id_user = Integer.parseInt(sId_user);

        //OBTENGO VARIABLE DESDE CLASE ADAPTADOR DE MUESTRA CONSULTA
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            nombre = extras.getString("nombre");
            cumple = extras.getString("cumple");

            id_datos = valorIdDatos(id_user, nombre);

            ndia = Integer.parseInt(cumple.split(" ")[1]);
            nombreMes = cumple.split(" ")[2];
            nmes = cf.numeroMes(nombreMes);
            fechaC = ndia + "-" + nmes;
            @SuppressLint("DefaultLocale") String fecha = String.format("%02d-%02d", ndia, nmes);

            //REFERENCIAS TILS Y SETEO DE LOS DATOS
            til_nombre = (TextInputLayout) findViewById(R.id.til_nombre);
            til_nombre.getEditText().setText(nombre);
            til_fechaC = (TextInputLayout) findViewById(R.id.til_fechaC);
            til_fechaC.getEditText().setText(fecha);

            //REFERENCIA BOTÓN ACTUALIZAR
            btnActualizar = (Button) findViewById(R.id.btnActualizar);
            btnActualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nombre_cump = til_nombre.getEditText().getText().toString();
                    fechaC = til_fechaC.getEditText().getText().toString();

                    if(validarDatos()) {
                        ndia = Integer.parseInt(fechaC.split("-")[0]);
                        nmes = Integer.parseInt(fechaC.split("-")[1]);
                        modificaCumple(id_datos, nombre, nmes, ndia);
                    }
                }
            });
        }

        //DATEPICKER
        final Calendar calendar = Calendar.getInstance();
        año = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        til_fechaC.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                @SuppressLint("DefaultLocale") String fecha = String.format("%02d-%02d", day, month + 1);
                                til_fechaC.getEditText().setText(fecha);
                            }
                        },año, mes, dia);
                datePickerDialog.show();
            }
        });

        //REFERENCIA BOTÓN VOLVER
        ibtnVolver = (ImageButton) findViewById(R.id.ibtnVolver);
        ibtnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private int valorIdDatos (int id_user, String nombre_cump){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null){
            Cursor cr = db.rawQuery("SELECT id_datos FROM tbl_datos WHERE id_user=" + id_user + " AND nombre_cump='" + nombre_cump + "'", null);
            if(cr.moveToFirst()){
                id_datos = cr.getInt(0);
            }
        }
        return id_datos;
    }

    private void modificaCumple (int id_datos, String nombre_cump, int mes, int dia){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if(db!= null){
            ContentValues cv = new ContentValues();
            cv.put("nombre_cump", nombre_cump);
            cv.put("dia", dia);
            cv.put("mes", mes);

            int exect = db.update("tbl_datos", cv, "id_datos =" + id_datos, null);
            if(exect > 0){
                Intent intent = new Intent (this, MuestraConsulta.class);
                Toast.makeText(this, "Cumpleaños actualizado", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }else{
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean nombreExiste (String nombre, int id_user){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null) {
            Cursor cr = db.rawQuery("SELECT nombre_cump FROM tbl_datos WHERE nombre_cump='" + nombre + "' AND id_user=" + id_user, null);
            int filas = cr.getCount();
            existe = filas > 0;
        }
        return existe;
    }

    private boolean nombreIgual (int id_datos, String nombre){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null) {
            Cursor cr = db.rawQuery("SELECT nombre_cump FROM tbl_datos WHERE nombre_cump='" + nombre + "' AND id_datos=" + id_datos, null);
            int filas = cr.getCount();
            existe = filas > 0;
        }
        return existe;
    }

    private boolean validarDatos() {
        nombre = til_nombre.getEditText().getText().toString();
        fechaC = til_fechaC.getEditText().getText().toString();

        boolean a1 = valida.esVacio(nombre);
        boolean a2 = valida.validaNombre_Ape(nombre);
        boolean a3 = nombreIgual(id_datos, nombre);
        boolean a4 = nombreExiste(nombre, id_user);

        if (!a1 && !a2) {
            if (a3) {
                iter = true;
            } else if (!a4) {
                til_nombre.setError(null);
                iter = true;
            } else {
                if (a1) {
                    til_nombre.setError("Campo vacío");
                } else if (a2) {
                    til_nombre.setError("Solo se permiten letras");
                } else if (!a3) {
                    if (a4) {
                        til_nombre.setError("Cumpleañero ya existe");
                    }
                } else {
                    til_nombre.setError(null);
                }
                iter = false;
            }
        }
        return iter;

    }
}
