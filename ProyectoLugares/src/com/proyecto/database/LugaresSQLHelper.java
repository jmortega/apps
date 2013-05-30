package com.proyecto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase que permite crear la base de datos mediante SqlLite
 */
public class LugaresSQLHelper extends SQLiteOpenHelper {
    
    /**
     * Constructor por parámetros
     * 
     * @param context Context
     *            Contexto de la aplicación
     */
    public LugaresSQLHelper(Context context) {
        super(context, LugaresDB.DB_NAME, null, LugaresDB.DB_VERSION);
    }

    @Override
    /**
     * Se llama cuando se crea el helper
     * Inicializa la base de datos
     * @param db SQLiteDatabase Objeto con datos pasados de la base de datos
     */
    public void onCreate(SQLiteDatabase db) {
        //Permitimos que se pueda escribir en la BD
        if (db.isReadOnly()) {
            db = getWritableDatabase();
        }

        // Si no existe la tabla, la creamos cuando ejecutamos la aplicacion por primera vez

       
        
        db.execSQL("CREATE TABLE " + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS + "(" +
                LugaresDB.Lugares._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                LugaresDB.Lugares.CATEGORIA + " TEXT, " +
                LugaresDB.Lugares.DESCRIPCION + " TEXT " + ")"
        );
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 0 ,'Restaurantes', 'Restaurantes')");

        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 1 ,'Bares', 'Bares')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 2 ,'Cafe', 'Cafe')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 3 ,'Compras', 'Compras')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 4 ,'Hoteles', 'Hoteles')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 5 ,'Musica', 'Musica')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 6 ,'Parques', 'Parques')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 7 ,'Aeropuertos', 'Aeropuertos')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 8 ,'Parking', 'Parking')");
        
        db.execSQL("insert into "
                + LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS
                + " values( 9 ,'Universidades', 'Universidades')");
        
        db.execSQL("CREATE TABLE " + LugaresDB.Lugares.NOMBRE_TABLA + "(" +
                LugaresDB.Lugares._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                LugaresDB.Lugares.NOMBRE + " TEXT, " +
                LugaresDB.Lugares.DESCRIPCION + " TEXT, " +
                LugaresDB.Lugares.LATITUD + " INTEGER, " +
                LugaresDB.Lugares.LONGITUD + " INTEGER, " +
                LugaresDB.Lugares.RATING + " INTEGER, " +
                LugaresDB.Lugares.FOTO + " TEXT, " +
                LugaresDB.Lugares.CATEGORIA + " INTEGER " +
                "REFERENCES "+LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS+" ("+LugaresDB.Lugares._ID+")"
                +")"
        );

    }


    @Override
    /**
     * Metodo que actualiza la base de datos de la aplicacion con la nueva version
     * @db SQLiteDatabase Datos de la base de datos
     * @oldVersion int Version instalada en el terminal
     * @newVersion int Version nueva de la base de datos
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
    // Cuando haya cambios en la estructura deberemos
    // incluir el código SQL necesario para actualizar la base de datos
        if(newVersion > oldVersion)
        {
            db.execSQL("DROP TABLE IF NOT EXISTS " + LugaresDB.Lugares.NOMBRE_TABLA);
            onCreate(db);
        }
    }
}
