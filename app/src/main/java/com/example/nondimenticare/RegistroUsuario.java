package com.example.nondimenticare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegistroUsuario extends AppCompatActivity {

    TextInputLayout til_user, til_name, til_ape, til_fechaN, til_correo, til_pass, til_pass2;
    TextView tvIngreso;
    Button btnRegistro;
    boolean iter = false, existe;
    private int año, mes, dia;
    final Valida valida = new Valida();
    String user, name, ape, fechaN, correo, pass, pass2, msj = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        //REFERENCIAS TILS
        til_user = (TextInputLayout) findViewById(R.id.til_user);
        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_ape = (TextInputLayout) findViewById(R.id.til_ape);
        til_fechaN = (TextInputLayout) findViewById(R.id.til_fechaN);
        til_correo = (TextInputLayout) findViewById(R.id.til_correo);
        til_pass = (TextInputLayout) findViewById(R.id.til_pass);
        til_pass2 = (TextInputLayout) findViewById(R.id.til_pass2);

        //DATEPICKER
        final Calendar calendar = Calendar.getInstance();
        año = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        til_fechaN.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                @SuppressLint("DefaultLocale") String fecha = String.format("%d-%02d-%02d", year, month + 1, day);
                                til_fechaN.getEditText().setText(fecha);
                            }
                        }, año, mes, dia);
                datePickerDialog.show();
            }
        });

        //REFERENCIA BOTÓN
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = til_user.getEditText().getText().toString();
                name = til_name.getEditText().getText().toString();
                ape = til_ape.getEditText().getText().toString();
                correo = til_correo.getEditText().getText().toString();
                fechaN = til_fechaN.getEditText().getText().toString();
                pass = til_pass.getEditText().getText().toString();
                if(validarDatos()) {
                    insertarUsuario(user, pass, name, ape, correo, fechaN);
                }
            }
        });

        //REFERENCIA AL MENSAJE PARA IR AL LOGIN
        tvIngreso = (TextView) findViewById(R.id.tvIngreso);
        tvIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Login.class);
                startActivity(intent);
            }
        });
    }

    private boolean usuarioExiste (String user){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null) {
            Cursor cr = db.rawQuery("SELECT user FROM tbl_user WHERE user='" + user + "'", null);
            int filas = cr.getCount();
            existe = filas > 0;
        }
        return existe;
    }

    public void insertarUsuario(String user, String pass, String name, String ape, String correo, String fechaN){
        DBhelper dbhelper = new DBhelper(this,"BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if(db!= null){
            ContentValues cv = new ContentValues();
            cv.put("user", user);
            cv.put("password", pass);
            cv.put("nombre", name);
            cv.put("apellido", ape);
            cv.put("correo", correo);
            cv.put("fecha_nac", fechaN);

            long nFilas = db.insert("tbl_user", null, cv);

            if (nFilas > 0){
                Intent intent = new Intent(this, Login.class);
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }else{
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validarDatos() {
        user = til_user.getEditText().getText().toString();
        name = til_name.getEditText().getText().toString();
        ape = til_ape.getEditText().getText().toString();
        correo = til_correo.getEditText().getText().toString();
        fechaN = til_fechaN.getEditText().getText().toString();
        pass = til_pass.getEditText().getText().toString();
        pass2 = til_pass2.getEditText().getText().toString();

        boolean a1 = valida.esVacio(user);
        boolean a2 = valida.validaUsuario(user);
        boolean a3 = usuarioExiste(user);
        boolean b1 = valida.esVacio(name);
        boolean b2 = valida.validaNombre_Ape(name);
        boolean c1 = valida.esVacio(ape);
        boolean c2 = valida.validaNombre_Ape(ape);
        boolean d1 = valida.esVacio(correo);
        boolean d2 = valida.validaCorreo(correo);
        boolean e1 = valida.esVacio(fechaN);
        boolean e2 = valida.validaFecha(fechaN);
        boolean e3 = valida.fechaPosteriorHoy(fechaN);
        boolean f1 = valida.esVacio(pass);
        boolean f2 = valida.validaPass(pass);
        boolean g1 = valida.esVacio(pass2);
        boolean g2 = valida.comparaPass(pass, pass2);

        if (!a1 && !a2 && !a3 && !b1 && !b2
                && !c1 && !c2 && !d1 && !d2
                && !e1 && !e2 && !e3 && !f1 && !f2
                && !g1 && !g2) {
            til_user.setError(null);
            til_name.setError(null);
            til_ape.setError(null);
            til_fechaN.setError(null);
            til_correo.setError(null);
            til_pass.setError(null);
            til_pass2.setError(null);
            iter = true;
        } else {
            if (a1) {
                til_user.setError("Usuario vacío");
            }else if (a2) {
                til_user.setError("Solo se permiten letras y números");
            }else if (a3){
                til_user.setError("Usuario ya existe");
            }else{
                til_user.setError(null);
            }
            if (b1) {
                til_name.setError("Nombre vacío");
            }else if(b2) {
                til_name.setError("Solo letras sin espacios inicio o final");
            }else{
                til_name.setError(null);
            }
            if (c1) {
                til_ape.setError("Apellido vacío");
            }else if(c2) {
                til_ape.setError("Solo letras sin espacios inicio o final");
            }else{
                til_ape.setError(null);
            }
            if(d1) {
                til_correo.setError("Correo vacío");
            }else if(d2) {
                til_correo.setError("Siga formato: correo@ejemplo.com");
            }else{
                til_correo.setError(null);
            }
            if (e1) {
                til_fechaN.setError("Fecha vacía");
            }else if(e2) {
                til_fechaN.setError("Fecha inválida");
            }else if(e3){
                til_fechaN.setError("Fecha posterior a hoy");
            }else{
                til_fechaN.setError(null);
            }
            if (f1) {
                til_pass.setError("Password vacía");
            }else if (f2) {
                til_pass.setError("Solo letras y números (mín. 8 y máx. 16)");
            }else{
                til_pass.setError(null);
            }
            if (g1) {
                til_pass2.setError("Confirmación Password vacía");
            }else if (g2) {
                til_pass2.setError("Password no coincide");
            }else{
                til_pass2.setError(null);
            }
            iter = false;
        }
        return iter;
    }
}