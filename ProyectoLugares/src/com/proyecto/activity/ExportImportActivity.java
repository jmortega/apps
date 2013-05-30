package com.proyecto.activity;


import com.proyecto.utility.ImportExportAsyncTask;
import com.proyecto.utility.ImportExportAsyncTask.ImportExportAsyncTaskResponder;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Actividad que muestra un combo con la lista de lugares registrados
 */
public class ExportImportActivity extends Activity {

    //Contexto
    Context context = this;
    
    @SuppressWarnings("unused")
    private Button exportar;
    @SuppressWarnings("unused")
    private Button importar;
    
    private ProgressDialog dialog;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        //Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.export_import);

        //inicializar controles
        initControls();

        
    }
    
    private void initControls(){

        exportar = (Button) findViewById(R.id.botonExportar);
        importar = (Button) findViewById(R.id.botonImportar);
    }
 
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
    /**
     * Comportamiento de la tecla ATRAS del terminal
     * 
     * @keyCode int Codigo de la tecla pulsada
     * @event KeyEvent Evento que ha provocado la llamada al metodo
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
     
    /**
     * Se muestran las opciones de menú de la lista de lugares
     * 
     * @param menu
     *            Objeto menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_export_import, menu);
        return true;
    }
    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * 
     * @param item
     *            MenuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                
               
            case R.id.btnExportar:
                
                exportar();
                
                return true;
                
            case R.id.btnImportar:
                
                importar();
                
                return true;
                
            case R.id.btnInfo:
                
                ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.exportar_importar_info).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                
                
                return true;
                
            case R.id.btnRecargarLugares:
    
                
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), TabLugaresActivity.class);
                startActivity(intent);
                finish();
                
                return true;
                
                // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Metodo que exporta la BD
     */
     public void exportarDB(View v) {
             exportar();
             
     }
     
     /**
      * Metodo que exporta la BD
      */
      public void importarDB(View v) {
              importar();
              
      }
      
      /**
       * Metodo que recarga los lugares
       */
       public void recargarLugares(View v) {
           
           Intent intent = new Intent();
           intent.setClass(getApplicationContext(), TabLugaresActivity.class);
           startActivity(intent);
           finish();
               
       }
     
      /**
       * Metodo que muestra la info sobre export/import
       */
       public void info(View v) {
           
           AlertDialog.Builder ab=new AlertDialog.Builder(ExportImportActivity.this);
           ab.setTitle(R.string.acerca_de_export_import);
           ab.setIcon(getResources().getDrawable(R.drawable.icon));
           ab.setMessage(R.string.exportar_importar_info);
           ab.setPositiveButton(R.string.Aceptar,null);
           ab.show();
       }
       
    /**
     * Exportar la base de datos
     */
    private void exportar() {

            dialog = ProgressDialog.show(ExportImportActivity.this, "", getString(R.string.procesando), true);

            ImportExportAsyncTaskResponder imporExportAsyncTaskResponder = new ImportExportAsyncTaskResponder() {
                    public void backupLoaded(Boolean resultado) {

                            if (resultado != null && resultado) {

                                    dialog.dismiss();
                                    Toast.makeText(ExportImportActivity.this, getString(R.string.exportar_ok), Toast.LENGTH_LONG).show();

                            } else {
                                    dialog.dismiss();
                                    Toast.makeText(ExportImportActivity.this, getString(R.string.exportar_error), Toast.LENGTH_LONG).show();

                            }
                    }
            };

            new ImportExportAsyncTask(imporExportAsyncTaskResponder).execute("exportar");

    }

    /**
     * Importar la base de datos
     */
    private void importar() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.importar_pregunta)).setCancelable(false).setPositiveButton(getString(R.string.Aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            procederImportacion();

                    }
            }).setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                    }
            });
            AlertDialog alert = builder.create();

            alert.show();

    }
    
    /**
     * Recuperar
     */
    private void procederImportacion() {

            dialog = ProgressDialog.show(ExportImportActivity.this, "", getString(R.string.procesando), true);

            ImportExportAsyncTaskResponder importExportAsyncTaskResponder = new ImportExportAsyncTaskResponder() {
                    public void backupLoaded(Boolean resultado) {

                            if (resultado != null && resultado) {

                                    dialog.dismiss();
                                    Toast.makeText(ExportImportActivity.this, getString(R.string.importar_ok), Toast.LENGTH_LONG).show();

                            } else {
                                    // Error al recuperar datos

                                    dialog.dismiss();

                                    Toast.makeText(ExportImportActivity.this, getString(R.string.importar_error), Toast.LENGTH_LONG).show();

                            }
                    }
            };

            new ImportExportAsyncTask(importExportAsyncTaskResponder).execute("importar");

    }


    
    
}
