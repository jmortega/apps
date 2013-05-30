package com.proyecto.activity;

import java.util.ArrayList;

import com.proyecto.database.LugaresDB;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ContentViewFlipper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Actividad que muestra un combo con la lista de lugares registrados
 */
public class ComboLugaresActivity extends Activity {

    private String[] lugares;

    private Spinner  spinner;

    private ContentViewFlipper MyViewFlipper;
    
    //Contexto
    Context context = this;
    
    //Array para almacenar los lugares que recogemos de la BD
    ArrayList<com.proyecto.entity.LugarEntity> places = new ArrayList<com.proyecto.entity.LugarEntity>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        //Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.combo_lugares);

        //inicializar controles
        initControls();
        
        //Animation
        Animation animationFlipIn  = AnimationUtils.loadAnimation(this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(this, R.anim.flipout); 
        MyViewFlipper.setAnimation(animationFlipIn);
        MyViewFlipper.setInAnimation(animationFlipIn);
        MyViewFlipper.setOutAnimation(animationFlipOut);
        
        
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();

        //Campos a recuperar en la consulta
        final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE,LugaresDB.Lugares.DESCRIPCION};
        
        //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;

        Cursor cursor = getContentResolver().query(uri,columnas, null, null, null);

        //colocamos el cursor en el primer elemento
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
            String nombre = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE));
            String descripcion = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
            
            com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(id,nombre,descripcion);
            
            places.add(lugar);
            
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursor.moveToNext();
        }
        
        //inicializar array con tantos lugares como hayamos encontrado
        lugares=new String[places.size()+1];
        
        for(int i=0;i<lugares.length;i++)
            lugares[i]="";
        
        lugares[0]=getText(R.string.seleccione_lugar).toString();
        
        
        for (int i = 0; i < places.size(); i++) {
            lugares[i+1]=places.get(i).getNombre()+"/"+places.get(i).getDescripcion();
        }
        
        //Si hay un cursor abierto, se cierra
        if (cursor!= null) {
            cursor.close();
        }
        
        if(lugares!=null && lugares.length>0){
            
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lugares);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int position = spinner.getSelectedItemPosition();
                
                if(position>0){
                    
                    AsyncLoading asyncLoading = new AsyncLoading(context);
                    asyncLoading.execute();
                    
                    //Creamos el item que llamara a la activity MostrarLugarActivity
                    Intent intent = new Intent(ComboLugaresActivity.this, MostrarLugarActivityAux.class);
                    intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);
                 
                    //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
                    intent.putExtra(LugaresDB.Lugares._ID, places.get(position-1).get_id());
                    intent.putExtra("idLugar", places.get(position-1).get_id());
                    startActivity(intent);
                    finish(); 
                }
                
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }

            });
        }
    }
    
    private void initControls(){

        spinner = (Spinner) findViewById(R.id.SpinnerLugares);
    
        MyViewFlipper = (ContentViewFlipper)findViewById(R.id.viewflipperLugares);
    }
 
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
}
