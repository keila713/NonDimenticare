package com.example.nondimenticare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity {

    Button btnInicio, btnRegistro;
    private SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //REFERENCIA E INICIALIZACIÓN DE LAS VARIABLES DE PREFERENCIA
        shPref = PreferenceManager.getDefaultSharedPreferences(this);
        String existeUsuario = shPref.getString(getString(R.string.usuario),"");
        String existePass = shPref.getString(getString(R.string.password),"");

        //EXISTE O NO EL USUARIO EN NUESTRAS VARIABLES DE PREFERENCIAS
        if(!existeUsuario.equals("") && !existePass.equals("")){

            //SE SALTA LOGIN EN CASO DE HABER RECORDADO EL USUARIO
            Intent intent = new Intent(this, RegistroCumple.class);
            intent.putExtra("user",existeUsuario);
            startActivity(intent);
        }

        //REFERENCIA BOTÓN INICIO SESIÓN
        btnInicio = (Button) findViewById(R.id.btnInicio);
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Login.class);
                startActivity(intent);
            }
        });

        //REFERENCIA BOTÓN REGISTRO
        btnRegistro = (Button) findViewById(R.id.btnActualizar);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistroUsuario.class);
                startActivity(intent);
            }
        });

    }
}
