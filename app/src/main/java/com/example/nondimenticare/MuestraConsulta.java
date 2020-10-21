package com.example.nondimenticare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MuestraConsulta extends AppCompatActivity {

    Switch swOpcion;
    Spinner spMes;
    RecyclerView rcvLista;
    TextView tvRegistrar, tvCerrar;
    private SharedPreferences shPref;
    int id_user, mes = 0;
    ArrayList<String> items;
    String nombreMes;
    final CalculosFechas cf = new CalculosFechas();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_consulta);

        //REFERENCIA VARIABLE DE PREFERENCIA
        shPref = PreferenceManager.getDefaultSharedPreferences(this);

        //ID_USER
        String sId_user = shPref.getString(getString(R.string.id_user),"");
        id_user = Integer.parseInt(sId_user);

        //REFERENCIA RECYCLER VIEW
        rcvLista = (RecyclerView) findViewById(R.id.rcvLista);

        //PROPIEDAD DE LISTADO VERTICAL
        rcvLista.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //LLAMADA A LISTAR DATOS
        listarDatos(id_user);

        //REFERENCIA SPINNER Y CREACION DEL ADAPTADOR
        spMes = (Spinner) findViewById(R.id.spMes);
        ArrayAdapter<CharSequence> adaptadorMes;
        adaptadorMes = ArrayAdapter.createFromResource(this, R.array.meses, android.R.layout.simple_list_item_1);

        //SETEO DEL ADAPTADOR
        spMes.setAdapter(adaptadorMes);

        //REFERENCIA SWITCH Y FUNCIÓN CLICK
        swOpcion = (Switch) findViewById(R.id.swOpcion);
        swOpcion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView errorTextview = (TextView) spMes.getSelectedView();
                if(isChecked){
                    spMes.setVisibility(View.VISIBLE);
                    spMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            nombreMes = spMes.getSelectedItem().toString();
                            if (nombreMes.equals("Todos")) {
                                listarDatos(id_user);
                            } else {
                                mes = cf.numeroMes(nombreMes);
                                listarDatosMes(id_user, mes);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            listarDatos(id_user);
                        }
                    });

                }else{
                    listarDatos(id_user);
                    spMes.setVisibility(View.GONE);
                    spMes.setSelection(0);
                    errorTextview.setError(null);
                }
            }
        });

        //REFERENCIA AL MENSAJE PARA IR A REGISTRAR CUMPLEAÑOS
        tvRegistrar = (TextView) findViewById(R.id.tvRegistrar);
        tvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistroCumple.class);
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

    public void listarDatos (int id_user){
        DBhelper dbhelper = new DBhelper(this,"BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        Valida valida = new Valida();
        int año = calendar.get(Calendar.YEAR);
        int dfaltan;

        if(db!= null){
            Cursor cr = db.rawQuery("SELECT CASE WHEN mes >= strftime('%m','now', 'localtime') AND dia >= strftime('%d','now', 'localtime') THEN 1 ELSE 2 END as segmento, nombre_cump, mes, dia FROM tbl_datos WHERE id_user=" + id_user + " ORDER BY CASE WHEN mes >= strftime('%m','now', 'localtime') AND dia >= strftime('%d','now', 'localtime') THEN 1 ELSE 2 END, mes ASC, dia ASC", null);
            items = new ArrayList<String>();
            if(cr.moveToFirst()){
                do{
                    String nombre_cump = cr.getString(1);
                    int nmes = cr.getInt(2);
                    int ndia = cr.getInt(3);

                    @SuppressLint("DefaultLocale") String sFecha = String.format("%d-%02d-%02d", año, nmes, ndia);

                    if (valida.fechaIgualHoy(sFecha)){
                        dfaltan = 0;
                    }else if(valida.fechaPosteriorHoy(sFecha)){
                        dfaltan = cf.diasRestantes(sFecha);
                    }else{
                        sFecha = String.format("%d-%02d-%02d", (año + 1), nmes, ndia);
                        dfaltan = cf.diasRestantes(sFecha);
                    }
                    String fila = nombre_cump + ";" + nmes + ";" + ndia + ";" + dfaltan;
                    items.add(fila);
                }while(cr.moveToNext());
            }
            AdaptadorC adaptadorC = new AdaptadorC(items);
            rcvLista.setAdapter(adaptadorC);
        }
    }

    public void listarDatosMes (int id_user, int mes){
        DBhelper dbhelper = new DBhelper(this,"BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        Valida valida = new Valida();
        int año = calendar.get(Calendar.YEAR);
        int dfaltan;

        if(db!= null){
            Cursor cr = db.rawQuery("SELECT CASE WHEN mes >= strftime('%m','now', 'localtime') AND dia >= strftime('%d','now', 'localtime') THEN 1 ELSE 2 END as segmento, nombre_cump, mes, dia FROM tbl_datos WHERE id_user=" + id_user + " AND mes=" + mes + " ORDER BY CASE WHEN mes >= strftime('%m','now', 'localtime') AND dia >= strftime('%d','now', 'localtime') THEN 1 ELSE 2 END, mes ASC, dia ASC", null);
            items = new ArrayList<String>();
            if(cr.moveToFirst()){
                do{
                    String nombre_cump = cr.getString(1);
                    int nmes = cr.getInt(2);
                    int ndia = cr.getInt(3);

                    @SuppressLint("DefaultLocale") String sFecha = String.format("%d-%02d-%02d", año, nmes, ndia);

                    if (valida.fechaIgualHoy(sFecha)){
                        dfaltan = 0;

                    }else if(valida.fechaPosteriorHoy(sFecha)){
                        dfaltan = cf.diasRestantes(sFecha);
                    }else{
                        sFecha = String.format("%d-%02d-%02d", (año + 1), nmes, ndia);
                        dfaltan = cf.diasRestantes(sFecha);
                    }
                    String fila = nombre_cump + ";" + nmes + ";" + ndia + ";" + dfaltan;
                    items.add(fila);
                }while(cr.moveToNext());
            }
            AdaptadorC adaptadorC = new AdaptadorC(items);
            rcvLista.setAdapter(adaptadorC);
        }
    }

    private void cerrarSesion(){
        Intent intent = new Intent (this, Login.class);
        startActivity(intent);
    }

    private void borrarShPref (){
        shPref.edit().clear().apply();
    }

    public void sendOnChannel1(View v){

        DBhelper dbhelper = new DBhelper(this,"BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        Valida valida = new Valida();
        int año = calendar.get(Calendar.YEAR);
        int dfaltan;

        if(db!= null){
            Cursor cr = db.rawQuery("SELECT CASE WHEN mes >= strftime('%m','now', 'localtime') AND dia >= strftime('%d','now', 'localtime') THEN 1 ELSE 2 END as segmento, nombre_cump, mes, dia FROM tbl_datos WHERE id_user=" + id_user + " ORDER BY CASE WHEN mes >= strftime('%m','now', 'localtime') AND dia >= strftime('%d','now', 'localtime') THEN 1 ELSE 2 END, mes ASC, dia ASC", null);
            items = new ArrayList<String>();
            if(cr.moveToFirst()){
                do{
                    String nombre_cump = cr.getString(1);
                    int nmes = cr.getInt(2);
                    int ndia = cr.getInt(3);

                    @SuppressLint("DefaultLocale") String sFecha = String.format("%d-%02d-%02d", año, nmes, ndia);

                    if (valida.fechaIgualHoy(sFecha)){
                        dfaltan = 0;
                    }else if(valida.fechaPosteriorHoy(sFecha)){
                        dfaltan = cf.diasRestantes(sFecha);
                    }else{
                        sFecha = String.format("%d-%02d-%02d", (año + 1), nmes, ndia);
                        dfaltan = cf.diasRestantes(sFecha);
                    }
                    String fila = nombre_cump + ";" + nmes + ";" + ndia + ";" + dfaltan;
                    items.add(fila);
                }while(cr.moveToNext());
            }
            }

    }

}
