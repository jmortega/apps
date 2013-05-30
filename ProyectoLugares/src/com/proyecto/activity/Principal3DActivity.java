package com.proyecto.activity;

import java.util.ArrayList;

import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.opengl.MyGLSurfaceView;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Principal3DActivity extends Activity {
    
    private Dialog dialogo;
    
    private MyGLSurfaceView glView;
    
    private ArrayList<String> lugares = new ArrayList<String>(6);
    private String lugares2[] = new String[6];
    
    //Contexto
    private Context context = this;
    
    /**
     * Crea la vista y los componentes
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
      //Campos a recuperar en la consulta
        final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.FOTO};
        
        //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;
        
        Cursor cursor = getContentResolver().query(uri,columnas, null, null, null);

        //colocamos el cursor en el primer elemento
        cursor.moveToFirst();


        
        for (int i = 0; i < cursor.getCount(); i++) {
            String path_foto="";
            if(cursor.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                path_foto = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.FOTO));
            }
            
            com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(path_foto);
            
            if(lugar!=null && lugar.getFoto()!=null &&!lugar.getFoto().equals(""))
                lugares.add(lugar.getFoto());
            
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursor.moveToNext();
        }
        
        int j=0;
        for (int i = 0; i < lugares.size() && j<6; i++, j++) {
            
            if((lugares.get(i)!=null) && !lugares.get(i).equals(""))
                lugares2[j]=lugares.get(i);
        }
        
        //Si hay un cursor abierto, se cierra
        if (cursor!= null) {
            cursor.close();
        }
        
        glView = new MyGLSurfaceView(this,lugares2);
 
        setContentView(glView);
        
    }
    
    
    /**
     * Se muestran las opciones de menú de la pantalla principal
     * @param menu Objeto menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal_3d, menu);
        return true;
    }
    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * @param item MenuItem 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar sobre "Acerca de" levantamos el popUp con la información sobre la aplicación
            case R.id.acerca:
                dialogo = onCreateDialog(Constants.DIALOGO_INFO);
                dialogo.show();
                return true;
            // Al pulsar sobre "Salir" salimos de la aplicación
            case R.id.salir:
                dialogo = onCreateDialog(Constants.DIALOGO_SALIR);
                dialogo.show();
                return true;
            case R.id.cambiarIdioma:
                cambiarIdioma();
                return true;
            case R.id.btnMisLugares:
                misLugares();
                return true;
            case R.id.btnVerMapa:
                verMapa();
                return true;
                
            case R.id.cambiarVista:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), PrincipalActivityAux.class);
                startActivity(intent);
                finish();
                
                return true;
            //Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    

    /**
     * Comportamiento de la tecla ATRAS del terminal
     * @keyCode int Codigo de la tecla pulsada
     * @event KeyEvent Evento que ha provocado la llamada al metodo
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogo = onCreateDialog(Constants.DIALOGO_SALIR);
            dialogo.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog newDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Animation animShow = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        final Animation animHide = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        switch (id) {
            case Constants.DIALOGO_SALIR:
                builder.setMessage(R.string.salir);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                         finish();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                newDialog = builder.create();
                break;

            case Constants.DIALOGO_INFO:
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.info, (ViewGroup) findViewById(R.id.layout_root));
                layout.startAnimation(animShow);

                builder.setNeutralButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        layout.startAnimation(animHide);
                    }
                });
                builder.setView(layout);
                newDialog = builder.create();

                break;
        }

        return newDialog;

    }
    
    public void misLugares() {
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), TabLugaresActivity.class);
        startActivity(intent);
    }
    
    public void verMapa() {
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MapaLugaresActivityAux.class);
        startActivity(intent);
    }
    
  
    
    public void cambiarIdioma() {
        
        ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.idiomas_soportados).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
        
        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS),Constants.ACTIVIDAD_IDIOMA);

    }
    
    /**
     * Metodo que gestiona las respuestas de las actividades
     */
     @Override 
     public void onActivityResult(int requestCode, int resultCode, Intent data) {     
       super.onActivityResult(requestCode, resultCode, data);
       
       switch(requestCode) { 
         case Constants.ACTIVIDAD_IDIOMA: 

             if (resultCode != RESULT_CANCELED) {
                 finish();
                 Intent intent = new Intent();
                 intent.setClass(getApplicationContext(), PrincipalActivityAux.class);
                 startActivity(intent);
             }
       }
       
     }
     
     @Override
     public boolean dispatchKeyEvent(KeyEvent event) {
             return super.dispatchKeyEvent(event);
     }
     
}
