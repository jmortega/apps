package com.proyecto.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Clase que abstrae los nombres de las tablas y sus campos
 * 
 */
public class LugaresDB {
	
	// Nombre de la base de datos
	public static final String DB_NAME = "lugaresDataBase.db";
	
	// Version de la base de datos
	public static final int DB_VERSION = 1;
	
    public static final String PROVIDER_NAME = "com.proyecto.database.lugaresDB";
    public static final Uri CONTENT_URI_LUGARES = Uri.parse("content://" + PROVIDER_NAME + "/lugares");
    public static final Uri CONTENT_URI_CATEGORIAS = Uri.parse("content://" + PROVIDER_NAME + "/categorias");
    
	// Esta clase no debe ser instanciada
	private LugaresDB() {}
	
	// Definicion de la tabla "Lugares"
	public static final class Lugares implements BaseColumns {
	    
		// Esta clase no debe ser instanciada
		private Lugares() {}
		
		// Orden por defecto de forma ascendente por id
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
		
		//Ordenar por nombre
		public static final String NOMBRE_SORT_ORDER = "NOMBRE ASC";
		
		//Ordenar por categoria
        public static final String CATEGORIA_SORT_ORDER = "CATEGORIA ASC";
		
		// Abstraccion de los nombres de campos y tablas
		public static final String NOMBRE_TABLA = "lugares";
		public static final String NOMBRE_TABLA_CATEGORIAS = "categorias";
		public static final String _ID = "_id";
		public static final String NOMBRE = "nombre";
		public static final String DESCRIPCION = "descripcion";
		public static final String LATITUD = "latitud";
		public static final String LONGITUD = "longitud";
		public static final String FOTO = "foto";
		public static final String RATING = "rating";
		public static final String ICONO = "icono";
		public static final String CATEGORIA = "categoria";
		
		//Con estas constantes controlamos si se solicita un registro o varios
        public static final int LUGARES = 1;
        public static final int LUGARES_ID = 2;
        
        public static final int CATEGORIAS = 3;
        public static final int CATEGORIAS_ID = 4;
        
	}

}
