package com.proyecto.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.proyecto.database.LugaresDB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DataImportExport {
    /**
     * Exportar la base de datos
     *
     * @return boolean
     */
    public static boolean exportar(boolean respaldo) {

            if (isSDCARDMounted()) {

                    boolean control = false;

                    // crea directorio en sd
                    File directorio = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.proyecto.database.lugaresDB/backup/");
                    directorio.mkdirs();

                    FileInputStream baseDatos = null;
                    FileOutputStream fileExport = null;

                    try {

                            // fichero de db
                            File fileEx = null;

                            // Copia de respaldo para posible fallo en importacion
                            if (!respaldo) {
                                    fileEx = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.proyecto.database.lugaresDB/backup/", "lugaresDataBaseRespaldo.db");

                            } else {

                                    fileEx = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.proyecto.database.lugaresDB/backup/", "lugaresDataBase.restore.db");

                            }

                            fileEx.createNewFile();

                            fileExport = new FileOutputStream(fileEx);

                            // base de datos
                            baseDatos = new FileInputStream(Environment.getDataDirectory() + "/data/com.proyecto.activity/databases/lugaresDataBase.db");

                            copyFile(baseDatos, fileExport);

                            fileExport.flush();

                            control = true;

                    } catch (IOException e) {
                            control = false;
                    } finally {

                            if (baseDatos != null) {
                                    try {
                                            baseDatos.close();
                                            baseDatos = null;
                                    } catch (IOException e) {

                                    }
                            }
                            if (fileExport != null) {
                                    try {

                                            fileExport.close();

                                            fileExport = null;

                                    } catch (IOException e) {

                                    }
                            }

                    }

                    return control;

            } else {
                    return false;
            }

    }

    /**
     * Sobreescribir la base de datos
     *
     * @return boolean
     */
    public static boolean importar(boolean respaldo) {
            if (isSDCARDMounted()) {

                    // Copia de respaldo para posible fallo
                    exportar(true);

                    FileInputStream fileEXIE = null;
                    FileOutputStream baseDatosE = null;

                    boolean control = false;

                    try {
                            File fileEx = null;

                            // Copia de recuperacion
                            if (!respaldo) {
                                    fileEx = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.proyecto.database.lugaresDB/backup/", "lugaresDataBaseRespaldo.db");
                            } else {
                                    fileEx = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.proyecto.database.lugaresDB/backup/", "lugaresDataBase.restore.db");
                            }

                            if (!fileEx.exists()) {
                                    return false;
                            }

                            if (!verificarArchivoBD(fileEx)) {
                                    return false;
                            }

                            fileEXIE = new FileInputStream(fileEx);

                            File baseDatos = new File(Environment.getDataDirectory() + "/data/com.proyecto.activity/databases/lugaresDataBase.db");

                            baseDatos.createNewFile();

                            baseDatosE = new FileOutputStream(baseDatos);

                            copyFile(fileEXIE, baseDatosE);

                            control = true;
                    } catch (IOException e) {

                            // Recuperar respaldo
                            importar(true);

                            control = false;
                    } finally {
                            if (fileEXIE != null) {
                                    try {
                                            fileEXIE.close();
                                            fileEXIE = null;
                                    } catch (IOException e) {

                                    }
                            }

                            if (baseDatosE != null) {
                                    try {
                                            baseDatosE.close();
                                            baseDatosE = null;
                                    } catch (IOException e) {

                                    }
                            }

                    }

                    return control;

            } else {
                    return false;
            }

    }

    /**
     * Comprobaciones
     *
     * @param db
     * @return boolean
     */
    public static boolean verificarArchivoBD(File db) {

            SQLiteDatabase sqlDb = null;
            Cursor cursor = null;

            try {
                    sqlDb = SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READONLY);

                    cursor = sqlDb.query(true, "lugares", null, null, null, null, null, null, null);

                    //Campos a recuperar en la consulta
                    String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};

                    String s;
                    for (int i = 0; i < columnas.length; i++) {
                            s = columnas[i];
                            cursor.getColumnIndexOrThrow(s);
                    }

            } catch (Exception e) {
                    // No valida
                    return false;
            } finally {
                    sqlDb.close();
                    cursor.close();
            }

            return true;
    }

    /**
     * Copiar archivo
     *
     * @param in
     * @param out
     * @throws IOException
     */
    private static void copyFile(FileInputStream in, FileOutputStream out) throws IOException {

            byte[] buffer = new byte[1024];
            int read;

            while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
            }

    }

    /**
     * tarjeta SD disponible
     *
     * @return boolean
     */
    private static boolean isSDCARDMounted() {
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED))
                    return true;
            return false;
    }



}
