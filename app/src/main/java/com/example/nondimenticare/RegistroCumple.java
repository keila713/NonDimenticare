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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class RegistroCumple extends AppCompatActivity {

    TextInputLayout til_nombre, til_fechaC;
    TextView tvMuestra, tvCerrar;
    Button btnGuardar;
    private int año, mes, dia;
    String msj = "", name_c, fechaC, diaC, mesC, user, fechaN, msjFaltan = "";
    int id_user, nmes, ndia, dfaltan = 0;
    boolean iter = false, existe;
    final Valida valida = new Valida();
    final CalculosFechas cf = new CalculosFechas();
    final Calendar calendar = Calendar.getInstance();
    private SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cumple);

        //REFERENCIA VARIABLE DE PREFERENCIA
        shPref = PreferenceManager.getDefaultSharedPreferences(this);

        //ID_USER
        String sId_user = shPref.getString(getString(R.string.id_user),"");
        id_user = Integer.parseInt(sId_user);

        //OBTENGO VARIABLE DE ACTIVIDAD LOGIN PARA MENSAJE
        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            user = getIntent().getStringExtra("user");
            //MOSTRARÁ CUÁNTOS DIAS FALTAN PARA EL CUMPLEAÑOS DEL USUARIO LOGEADO
            int año2 = calendar.get(Calendar.YEAR);
            fechaN = valorFechaN(id_user);
            nmes = Integer.parseInt(fechaN.split("-")[1]);
            ndia = Integer.parseInt(fechaN.split("-")[2]);

            @SuppressLint("DefaultLocale") String sFecha = String.format("%d-%02d-%02d", año2, nmes, ndia);
            if (valida.fechaIgualHoy(sFecha)){
                dfaltan = 0;
            }else if(valida.fechaPosteriorHoy(sFecha)){
                dfaltan = cf.diasRestantes(sFecha);
            }else{
                sFecha = String.format("%d-%02d-%02d", (año2 + 1), nmes, ndia);
                dfaltan = cf.diasRestantes(sFecha);
            }

            if(dfaltan == 0){
                msjFaltan = "¡Feliz Cumpleaños!";
            }else {
                if (dfaltan == 1) {
                    msjFaltan = "Falta " + dfaltan + " día para tu cumpleaños.";
                }else {
                    msjFaltan = "Faltan: " + dfaltan + " días para tu cumpleaños.";
                }
            }
            Toast.makeText(this, "Bienvenid@ " + user + "\n\n" + msjFaltan, Toast.LENGTH_LONG).show();
        }

        //REFERENCIAS TILS
        til_nombre = (TextInputLayout) findViewById(R.id.til_nombre);
        til_fechaC = (TextInputLayout) findViewById(R.id.til_fechaC);

        //DATEPICKER
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

        //REFERENCIA BOTÓN
        btnGuardar = (Button) findViewById(R.id.btnActualizar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_c = til_nombre.getEditText().getText().toString();
                fechaC = til_fechaC.getEditText().getText().toString();

                if(validarDatos()) {
                    diaC = fechaC.split("-")[0];
                    mesC = fechaC.split("-")[1];
                    msj = insertarCumple(id_user, name_c, diaC, mesC);
                    if(msj.equals("ok")){
                        Toast.makeText(v.getContext(), "Datos guardados: " +
                                name_c + ", Cumpleaños: " + fechaC, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(v.getContext(), msj, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //REFERENCIA AL MENSAJE PARA IR A VER LOS CUMPLEAÑOS
        tvMuestra = (TextView) findViewById(R.id.tvMuestra);
        tvMuestra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MuestraConsulta.class);
                startActivity(intent);
            }
        });

        //REFERENCIA A CERRAR SESIÓN
        tvCerrar = (TextView) findViewById(R.id.tvCerrar);
        tvCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarShPref();
                cerrarSesion();
            }
        });

    }

    private String valorFechaN (int id_user){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null){
            Cursor cr = db.rawQuery("SELECT fecha_nac FROM tbl_user WHERE id_user=" + id_user, null);
            if(cr.moveToFirst()){
                fechaN = cr.getString(0);
            }
        }
        return fechaN;
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

    public String insertarCumple(int id_user, String name_c, String dia, String mes){
        DBhelper dbhelper = new DBhelper(this,"BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if(db!= null){
            ContentValues cv = new ContentValues();
            cv.put("id_user", id_user);
            cv.put("nombre_cump", name_c);
            cv.put("dia", dia);
            cv.put("mes", mes);

            long nFilas = db.insert("tbl_datos", null, cv);

            if (nFilas > 0){
                msj = "ok";
            }
        }
        return msj;
    }

    private boolean validarDatos() {
        name_c = til_nombre.getEditText().getText().toString();
        fechaC = til_fechaC.getEditText().getText().toString();

        boolean a1 = valida.esVacio(name_c);
        boolean a2 = valida.validaNombre_Ape(name_c);
        boolean a3 = nombreExiste(name_c, id_user);
        boolean b = valida.esVacio(fechaC);

        if (!a1 && !a2 && !a3 && !b) {
            til_nombre.setError(null);
            til_fechaC.setError(null);
            iter = true;
        } else {
            if (a1) {
                til_nombre.setError("Campo vacío");
            }else if (a2) {
                til_nombre.setError("Solo se permiten letras");
            }else if(a3) {
                til_nombre.setError("Cumpleañero ya existe");
            }else{
                til_nombre.setError(null);
            }
            if (b) {
                til_fechaC.setError("No seleccionó fecha");
            }else{
                til_fechaC.setError(null);
            }
            iter = false;
        }
        return iter;
    }

    private void cerrarSesion(){
        Intent intent = new Intent (this, Login.class);
        startActivity(intent);
    }

    private void borrarShPref (){
        shPref.edit().clear().apply();
    }
}
