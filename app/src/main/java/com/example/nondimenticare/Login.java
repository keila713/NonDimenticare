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
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    TextInputLayout til_user, til_pass;
    TextView tvRegistro;
    Button btnLogin;
    Switch swRecordarUser;
    boolean iter = false, existe, correcto;
    String msj = "", user, id_user = "";
    private SharedPreferences shPref;
    private SharedPreferences.Editor shEditor;
    final Valida valida = new Valida();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //REFERENCIAS TILS
        til_user = (TextInputLayout) findViewById(R.id.til_user);
        til_pass = (TextInputLayout) findViewById(R.id.til_pass);

        //REFERENCIA SWITCH
        swRecordarUser = (Switch) findViewById(R.id.swRecordarUser);

        //INICIALIZACIÓN DE LAS VARIABLES DE PREFERENCIA
        shPref = PreferenceManager.getDefaultSharedPreferences(this);
        shEditor = shPref.edit();

        //REFERENCIA BOTÓN
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarDatos()) {
                    //ALMACENA ID_USER EN VARIABLE PREFERENCIA
                    shEditor.putString(getString(R.string.id_user),valorIdUser(til_user.getEditText().getText().toString()));

                    if(swRecordarUser.isChecked()){
                        shEditor.putString(getString(R.string.usuario),til_user.getEditText().getText().toString());
                        shEditor.putString(getString(R.string.password),til_pass.getEditText().getText().toString());
                        shEditor.commit();
                    }
                    else{
                        shEditor.putString(getString(R.string.usuario),"");
                        shEditor.putString(getString(R.string.password),"");
                        shEditor.commit();
                    }
                    Intent intent = new Intent(v.getContext(), RegistroCumple.class);
                    user = til_user.getEditText().getText().toString();
                    intent.putExtra("user",user);
                    startActivity(intent);
                }
            }
        });

        //REFERENCIA AL MENSAJE PARA IR A REGISTRARSE
        tvRegistro = (TextView) findViewById(R.id.tvRegistro);
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistroUsuario.class);
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

    private boolean validaCredenciales (String user, String pass){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null) {
            Cursor cr = db.rawQuery("SELECT user, password FROM tbl_user WHERE user='" + user + "' AND password='" + pass + "'" , null);
            int filas = cr.getCount();
            correcto = filas > 0;
        }
        return correcto;
    }

    private String valorIdUser (String user){
        DBhelper dbhelper = new DBhelper(this, "BD_fran", null, 1);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db!= null){
            Cursor cr = db.rawQuery("SELECT id_user FROM tbl_user WHERE user='" + user + "'", null);
            if(cr.moveToFirst()){
                id_user = cr.getString(0);
            }
        }
        return id_user;
    }

    private boolean validarDatos() {
        String user = til_user.getEditText().getText().toString();
        String pass = til_pass.getEditText().getText().toString();

        boolean a1 = valida.esVacio(user);
        boolean a2 = valida.validaUsuario(user);
        boolean a3 = usuarioExiste(user);
        boolean b = valida.esVacio(pass);
        boolean c = validaCredenciales(user, pass);

        if (!a1 && !a2 && a3 && !b && c) {
            til_user.setError(null);
            til_pass.setError(null);
            iter = true;
        } else {
            if (a1) {
                til_user.setError("Usuario vacío");
            }else if (a2) {
                til_user.setError("Solo se permiten letras y números");
            }else if (!a3) {
                til_user.setError("Usuario no existe");
            }else if (!c) {
                til_user.setError(null);
                if (b) {
                    til_pass.setError("Password vacía");
                } else {
                    til_pass.setError("Password incorrecta");
                }
            }
            iter = false;
        }
        return iter;
    }
}
