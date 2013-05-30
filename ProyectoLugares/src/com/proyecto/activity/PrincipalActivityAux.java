package com.proyecto.activity;

import com.proyecto.constants.Constants;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
/**
 * Actividad principal que muestra la pantalla de inicio
 */
public class PrincipalActivityAux extends Activity{
    
    private Dialog dialogo;

    //Contexto
    private Context context = this;
    
    //Botones
    private Button misLugares;
    private Button verMapa;
    private ImageView ImagenEstado;
    
    /**
     * Crea la vista y los componentes
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        //Animacion
        Animation animation  = AnimationUtils.loadAnimation(this, R.anim.flipin);
 
        //Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        initControls();
        
        misLugares.setAnimation(animation);
        misLugares.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View view) {

                misLugares();
                
            }
        });

        verMapa = (Button) findViewById(R.id.VerMapa);
        verMapa.setAnimation(animation);
        verMapa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                
               verMapa();
            }
        });

        
    }
    
    /**
     * Se muestran las opciones de menú de la pantalla principal
     * @param menu Objeto menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal, menu);
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
            case R.id.estadoConexion:
                estadoConexion();
                return true;
            case R.id.realidadAumentada:
                realidadAumentada();
                return true; 
            case R.id.cambiarVista:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), Principal3DActivity.class);
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
                builder.setCancelable(false);
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
    
    /**
    * Metodo que comprueba el estado de la red
    */
    public void estadoRed(View v) {
        
        estadoConexion();
        
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
    
    public void estadoConexion() {
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), TestConexionActivity.class);
        startActivityForResult(intent, Constants.ACTIVIDAD_TEST_CONEXION);
    }
    
    public void realidadAumentada() {
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), RealidadAumentadaActivity.class);
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
             break;
         case Constants.ACTIVIDAD_TEST_CONEXION: 

             if (resultCode == Activity.RESULT_OK) { 
                 
                 Bundle extras = data.getExtras();
                 
                 if(extras != null) {
                    
                     String estado = extras.getString("estado");

                     if(estado!=null && estado.equalsIgnoreCase(getText(R.string.conectado).toString())){
                         ImagenEstado.setImageResource(R.drawable.conectado);
                     }
                     
                     if(estado!=null && estado.equalsIgnoreCase(getText(R.string.desconectado).toString())){
                         ImagenEstado.setImageResource(R.drawable.desconectado);
                     }
                     
                 }
             }
             break;
       }
     }
     
     private void initControls(){

         misLugares = (Button) findViewById(R.id.MisLugares);
         
         verMapa = (Button) findViewById(R.id.VerMapa);
         
         ImagenEstado = (ImageView) findViewById(R.id.imageNetWork);
     }
     
     @Override
     public boolean dispatchKeyEvent(KeyEvent event) {
             return super.dispatchKeyEvent(event);
     }
     
     
}