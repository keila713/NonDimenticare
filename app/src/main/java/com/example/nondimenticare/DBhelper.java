package com.example.nondimenticare;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {
    String tbl_user = "CREATE TABLE tbl_user (id_user integer PRIMARY KEY AUTOINCREMENT,user varchar,password varchar,nombre varchar,apellido varchar,correo varchar,fecha_nac date);";
    String tbl_datos = "CREATE TABLE tbl_datos (id_datos integer PRIMARY KEY AUTOINCREMENT,id_user integer,nombre_cump varchar,mes integer,dia integer);";
    public DBhelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    //METODO PARA CREAR LAS TABLAS
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tbl_user);
        db.execSQL(tbl_datos);
    }
    //METODO PARA ACTUALIZAR LA BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}