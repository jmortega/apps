package com.proyecto.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Clase que permite consultar,crear,actualizar y eliminar un lugar de la BD
 */
public class LugaresProvider extends ContentProvider {
	
	public static final Uri CONTENT_URI_LUGARES = LugaresDB.CONTENT_URI_LUGARES;
	public static final Uri CONTENT_URI_CATEGORIAS = LugaresDB.CONTENT_URI_CATEGORIAS;
	private static final UriMatcher uriMatcher;
	

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(LugaresDB.PROVIDER_NAME, "lugares", LugaresDB.Lugares.LUGARES);
        uriMatcher.addURI(LugaresDB.PROVIDER_NAME, "lugares/#", LugaresDB.Lugares.LUGARES_ID);
        uriMatcher.addURI(LugaresDB.PROVIDER_NAME, "categorias", LugaresDB.Lugares.CATEGORIAS);
        uriMatcher.addURI(LugaresDB.PROVIDER_NAME, "categorias/#", LugaresDB.Lugares.CATEGORIAS_ID);
    }
	
	private SQLiteDatabase lugaresDB;
	
	@Override
	/**
     * Se llama cuando se crea el provider
     * inicializa la conexion con la BBDD
     */
	public boolean onCreate() {
		Context context = getContext();
		LugaresSQLHelper dbHelper = new LugaresSQLHelper(context);
		lugaresDB = dbHelper.getWritableDatabase();
		   
		return (lugaresDB == null) ? false : true;
		
	}

	@Override
	 /**
     * Devuelve el tipo de consulta que se requiere segun la Uri definida
     * @uri Uri Direccion que se ha definido
     */
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case LugaresDB.Lugares.LUGARES:
			    //Para un solo lugar
				return "vnd.android.cursor.item/vnd.proyecto.lugares";
			case LugaresDB.Lugares.LUGARES_ID:
				// Para conjunto de lugares
				return "vnd.android.cursor.dir/vnd.proyecto.lugares";
			case LugaresDB.Lugares.CATEGORIAS:
                //Para un solo lugar
                return "vnd.android.cursor.item/vnd.proyecto.categorias";
            case LugaresDB.Lugares.CATEGORIAS_ID:
                // Para conjunto de lugares
                return "vnd.android.cursor.dir/vnd.proyecto.categorias";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	
	@Override
	 /**
     * Inserta un conjunto de valores en la base de datos,devuelve el id del registro insertado
     * @uri Uri Direccion de la base de datos
     * @values ContenValues Valores a insertar en el registro
     */
	public Uri insert(Uri uri, ContentValues values) {
	    String nombreTabla="";
	    
	    //añade un nuevo lugar / categoria
	    if(uri!=null && uri.equals(LugaresDB.CONTENT_URI_CATEGORIAS)){
	        
	        nombreTabla=LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS;
	        
        }else if(uri!=null && uri.equals(LugaresDB.CONTENT_URI_LUGARES)){
            
            nombreTabla=LugaresDB.Lugares.NOMBRE_TABLA;
        }
	    
		long rowID = lugaresDB.insert(nombreTabla, "", values);
		// Si todo ha ido ok devolvemos su Uri
		if (rowID > 0) {
		    Log.e("OK", "registro insertado OK");
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI_LUGARES, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}else{
		    Log.e("ERROR", "Error al insertar el registro");

		}
		
		throw new SQLException("Error al insertar el registro " + uri);
	}

	@Override
	 /**
     * Elimina un registro de la base de datos,devuelve el id del registro eliminado
     * @uri Uri Direccion de la base de datos sobre la que borrar
     * @where String Consulta para acotar la busqueda
     * @whereArgs String[] Lista de argumentos para la consulta
     */
	public int delete(Uri uri, String where, String[] whereargs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
			case LugaresDB.Lugares.LUGARES:
				count = lugaresDB.delete(LugaresDB.Lugares.NOMBRE_TABLA, where, whereargs);
				break;
			case LugaresDB.Lugares.LUGARES_ID:
				String id = uri.getPathSegments().get(1);
				count = lugaresDB.delete(LugaresDB.Lugares.NOMBRE_TABLA, LugaresDB.Lugares._ID + " = " + id + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereargs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		// Notificamos el cambio al sistema y devolvemos el numero de filas afectadas
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
    /**
     * Actualiza un registro de la base de datos,devuelve el id del registro actualizado
     * @uri Uri Direccion de la base de datos sobre la que actualizar
     * @where String Consulta para acotar la busqueda
     * @whereArgs String[] Lista de argumentos para la consulta
     */
	public int update(Uri uri, ContentValues values, String where, String[] whereargs) {
		int count = 0;
		
		switch (uriMatcher.match(uri)) {
			case LugaresDB.Lugares.LUGARES:
				count = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA, values, where, whereargs);
				break;
			case LugaresDB.Lugares.LUGARES_ID:
				count = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA, 
										 values, 
										 LugaresDB.Lugares._ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereargs);
				break;
			case LugaresDB.Lugares.CATEGORIAS:
                count = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS, values, where, whereargs);
                break;
			case LugaresDB.Lugares.CATEGORIAS_ID:
                count = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS, 
                                         values, 
                                         LugaresDB.Lugares._ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereargs);
                break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		//Si todo ha ido bien devuelve el número de filas afectadas
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	/**
     * Realiza una consulta sobre la base de datos y devuelve un objeto Cursor con los datos obtenidos
     * @uri Uri Direccion de la base de datos
     * @projection String[] Lista de las columnas que se quieren obtener
     * @selection String Query que se define para la consulta
     * @selectionArgs String[] Valores utilizados en la query para acotar la consulta
     * @sorOrder String Indica la ordenacion y los campos de la misma
     */
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		String uriAux=uri.toString();
		if(uriAux!=null && uriAux.contains("categorias")){
		    sqlBuilder.setTables(LugaresDB.Lugares.NOMBRE_TABLA_CATEGORIAS);
		}else{
		    sqlBuilder.setTables(LugaresDB.Lugares.NOMBRE_TABLA);
		}
		
		if (sortOrder == null || sortOrder == "")
            sortOrder = LugaresDB.Lugares.DEFAULT_SORT_ORDER;
		
		if ((uriMatcher.match(uri) == LugaresDB.Lugares.LUGARES_ID) || (uriMatcher.match(uri) == LugaresDB.Lugares.CATEGORIAS_ID))
			sqlBuilder.appendWhere(LugaresDB.Lugares._ID + " = " + uri.getPathSegments().get(1));
		
		
		Cursor c = sqlBuilder.query(lugaresDB, projection, selection, selectionArgs, null, null, sortOrder);
		
		// Registramos los cambios para que se enteren nuestros observers
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

}
