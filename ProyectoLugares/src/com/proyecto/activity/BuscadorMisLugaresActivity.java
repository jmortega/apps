package com.proyecto.activity;

import java.util.ArrayList;

import com.proyecto.database.LugaresDB;
import com.proyecto.entity.LugarEntity;
import com.proyecto.utility.AsyncLoading;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad que permite la busqueda de lugares registrados por nombre y descripcion
 */
public class BuscadorMisLugaresActivity extends ListActivity{


    //Campo de busqueda
    private EditText busqueda;
    
    private TextView noResultados;
    
    //Contexto
    private Context context = this;
    
    //Array para almacenar los lugares que recogemos de la BD
    private ArrayList<com.proyecto.entity.LugarEntity> places = new ArrayList<com.proyecto.entity.LugarEntity>();
    
  //Array para almacenar los lugares que coinciden con el criterio de busqueda
    private ArrayList<com.proyecto.entity.LugarEntity> placesSearch = new ArrayList<com.proyecto.entity.LugarEntity>();

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        //Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.buscador_lugares);
        
        initControls();
        
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        //Campos a recuperar en la consulta
        final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};
        
        
        //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;

        Cursor cursor = getContentResolver().query(uri,columnas, null, null, null);

        //colocamos el cursor en el primer elemento
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            String path_foto="";
            int id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
            String nombre = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE));
            String descripcion = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
            int categoria = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA));
            if(cursor.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                path_foto = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.FOTO));
            }
            com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(id,nombre,descripcion,path_foto,categoria);
            
            places.add(lugar);
            
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursor.moveToNext();
        }
        
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
        menuInflater.inflate(R.menu.menu_buscar_lugares, menu);
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
                
           // Al pulsar sobre "Volver"
            case R.id.volver:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), PrincipalActivityAux.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.btnBuscarLugaresNombre:

                placesSearch = new ArrayList<com.proyecto.entity.LugarEntity>();
                
                if(busqueda.getText()!=null && !busqueda.getText().toString().equals("")){
                    
                  buscarLugaresPorNombre(busqueda.getText().toString());
                    
                }else{
                    
                    Toast.makeText(getApplicationContext(),getText(R.string.informar_busqueda).toString(),Toast.LENGTH_LONG).show();
                    
                }
                
                return true;
                
            case R.id.btnBuscarLugaresDesc:

                placesSearch = new ArrayList<com.proyecto.entity.LugarEntity>();
                
                if(busqueda.getText()!=null && !busqueda.getText().toString().equals("")){
                    
                  buscarLugaresPorDescripcion(busqueda.getText().toString());
                    
                }else{
                    
                    Toast.makeText(getApplicationContext(),getText(R.string.informar_busqueda).toString(),Toast.LENGTH_LONG).show();
                    
                }
                
                return true;
                
                // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
   /** Metodo llamado al pulsar cualquier elemento de la lista
     * Recoge el Id del elemento pulsado y llama a la siguiente actividad
     * @l ListView Lista de lugares
     * @v View Vista desde la que se ha realizado la llamada
     * @position int Posicion del elemento en la lista
     * @id long Id del elemento en la lista
     */
    protected void onListItemClick(ListView l, View v, int position, long id){
        
        super.onListItemClick(l, v, position, id);
        
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        //Creamos el item que llamara a la activity MostrarLugarActivity
        Intent intent = new Intent(BuscadorMisLugaresActivity.this, MostrarLugarActivityAux.class);
        intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);

        //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
        intent.putExtra(LugaresDB.Lugares._ID, placesSearch.get(position).get_id());
        intent.putExtra("idLugar", placesSearch.get(position).get_id());
        startActivity(intent);
        finish();  
       
    }
    
    
    /** Metodo que realiza la busqueda por nombre
     * @busqueda String criterio de busqueda
     */
    private void buscarLugaresPorNombre(String busqueda){
        
        for (LugarEntity lugar : places) {
        
            if(lugar.getNombre().contains(busqueda.trim())){
                placesSearch.add(lugar);
            }
        }
        
        if(placesSearch.size()>0){
            noResultados.setVisibility(View.INVISIBLE);
        }else{
            noResultados.setVisibility(View.VISIBLE);
        }
        
        com.proyecto.adapter.ListaLugaresAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(BuscadorMisLugaresActivity.this,R.layout.lugares_item,placesSearch);
        
        
        //Fijamos el Adapter de lugares
        setListAdapter(adapterLugares);
        
    }
    
    /** Metodo que realiza la busqueda por descripcion
     * @busqueda String criterio de busqueda
     */
    private void buscarLugaresPorDescripcion(String busqueda){
        
        for (LugarEntity lugar : places) {
        
            if(lugar.getDescripcion().contains(busqueda.trim())){
                placesSearch.add(lugar);
            }
        }
        
        if(placesSearch.size()>0){
            noResultados.setVisibility(View.INVISIBLE);
        }else{
            noResultados.setVisibility(View.VISIBLE);
        }
        
        com.proyecto.adapter.ListaLugaresAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(BuscadorMisLugaresActivity.this,R.layout.lugares_item,placesSearch);
        
        
        //Fijamos el Adapter de lugares
        setListAdapter(adapterLugares);
        
    }
    
    /** Metodo que inicializa los controles de la vista
     */
    private void initControls(){

        busqueda = (EditText)findViewById(R.id.editBuscarLugar);
        noResultados = (TextView) this.findViewById(R.id.noResultados);
        noResultados.setVisibility(View.INVISIBLE);
    
    }
    

}
