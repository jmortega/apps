package com.proyecto.activity;

import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Actividad principal que muestra la pantalla de busqueda de un lugar
 */
public class BuscarLugarActivity extends Activity {
    
    private EditText busquedaTextView;
    private Button buscar;
    
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
        
        setContentView(R.layout.buscar_lugar);
        
        //inicializar controles
        initControls();

        buscar.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View view) {

                buscarLugar();
                
            }
        });

    }
    
    
    private void initControls(){

        busquedaTextView = (EditText) this.findViewById(R.id.busqueda);
        
        buscar = (Button) findViewById(R.id.botonBuscar);
    
    }
    
    /**
    * Metodo que valida los datos del formulario de busqueda
    */
    public boolean validarFormularioBusqueda() {
        
        String busquedaText = busquedaTextView.getText().toString();

        boolean result=false;
        
        //si el campo busqueda está vacio mostramos mensaje 
        if(busquedaText==null || busquedaText.equals("")){

            ToastPersonalizado.getInstance(getApplicationContext()).mostrarToastPersonalizado(getText(R.string.informar_busqueda).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

            busquedaTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
            
        }else{
            
            busquedaTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
        }
        

        if(busquedaText!=null && !busquedaText.equals("")){
            result=true;
        }
        
        return result;
    }
    
    
    public void buscarLugar(){
        
      //Comprobamos que los datos introducidos en el formulario son validos
        if (validarFormularioBusqueda()) {
            
            String busqueda = busquedaTextView.getText().toString();
            
            Intent intent = new Intent();
            
            intent.putExtra("busqueda", busqueda);
            
            intent.setClass(getApplicationContext(), MapaLugaresActivityAux.class);
            
            setResult(Activity.RESULT_OK, intent);
            
            finish();
        }
    }
    

    /**
     * Se muestran las opciones de menú de la pantalla buscar
     * @param menu Objeto menu 
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_buscar, menu);
        return true;
    }
    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * @param item MenuItem 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar sobre "Buscar lugar" 
            case R.id.btnBuscarLugar:

                buscarLugar();
                
                return true;
            // Al pulsar sobre "Volver" 
            case R.id.volver:
                finish();
                return true;
            //Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
}
