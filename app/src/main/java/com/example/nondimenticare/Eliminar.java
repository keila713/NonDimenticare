package com.example.nondimenticare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Eliminar extends AppCompatActivity {

    Button btnEliminar;
    ImageButton ibtnVolver;
    String nombre = "", cumple="", faltan="";
    TextView tvNombre, tvFecha, tvFaltan, tvCerrar;;
    private SharedPreferences shPref;
    int id_datos, id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar);

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
            faltan = extras.getString("faltan");

            id_datos = valorIdDatos(id_user, nombre);

            //REFERENCIA BOTÓN ELIMINAR
            btnEliminar = (Button) findViewById(R.id.btnEliminar);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminaRegistro(id_datos);
                }
            });
        }

        //REFERENCIA TEXWVIEW Y SETEO DEL DATO SELECCIONADO
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvNombre.setText(nombre);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        tvFecha.setText(cumple);
        tvFaltan = (TextView) findViewById(R.id.tvFaltan);
        tvFaltan.setText(faltan);

        //REFERENCIA BOTÓN VOLVER
        ibtnVolver = (ImageButton) findViewById(R.id.ibtnVolver);
        ibtnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void eliminaRegistro(int id_datos){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if(db!= null){
            int exect = db.delete("tbl_datos", "id_datos =" + id_datos, null);
            if(exect > 0){
                Intent intent = new Intent(this, MuestraConsulta.class);
                Toast.makeText(this, "Registro ha sido eliminado", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }

    private void cerrarSesion(){
        Intent intent = new Intent (this, Login.class);
        startActivity(intent);
    }

    private void borrarShPref (){
        shPref.edit().clear().apply();
    }
}
