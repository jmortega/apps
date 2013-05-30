package com.proyecto.activity;

import java.util.ArrayList;
import com.proyecto.database.LugaresDB;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ContentViewFlipper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Actividad que muestra una lista de los lugares almacenados en el sistema
 */
public class ListaCategoriasActivity extends ListActivity {
	
    private ContentViewFlipper MyViewFlipper;
    
    //Contexto
    private Context context = this;
    
  //dialogo compartir
    private int selected = 0;
    private int buffKey  = 0;
    private String accion = "";
    
    //Array para almacenar las categorias que recogemos de la BD
    ArrayList<com.proyecto.entity.CategoriaEntity> category = new ArrayList<com.proyecto.entity.CategoriaEntity>();
    
    private ListView categoriasView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        //Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.lista_categorias);

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
        final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.CATEGORIA,LugaresDB.Lugares.DESCRIPCION};
        
        //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_CATEGORIAS;

        Cursor cursor = getContentResolver().query(uri,columnas,null, null, null);

        //colocamos el cursor en el primer elemento
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
            String categoria = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA));
            String descripcion = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
            
            com.proyecto.entity.CategoriaEntity categoriaEntity = new com.proyecto.entity.CategoriaEntity(id,categoria,descripcion);
            
            category.add(categoriaEntity);
            
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursor.moveToNext();
        }
        

        //Si hay un cursor abierto, se cierra
        if (cursor!= null) {
            cursor.close();
        }
        
        if(category!=null && category.size()>0){
            
            com.proyecto.adapter.ListaCategoriasAdapter adapterCategorias = new com.proyecto.adapter.ListaCategoriasAdapter(ListaCategoriasActivity.this,category);
            
            //Fijamos el Adapter de lugares
            setListAdapter(adapterCategorias);

            registerForContextMenu(categoriasView);
            
        }
    }
    
    @Override
    /** Metodo llamado al pulsar cualquier elemento de la lista
      * Recoge el Id del elemento pulsado y llama a la siguiente actividad
      * @l ListView Lista de Categorias
      * @v View Vista desde la que se ha realizado la llamada
      * @position int Posicion del elemento en la lista
      * @id long Id del elemento en la lista
      */
     protected void onListItemClick(ListView l, View v, int position, long id){
         
         super.onListItemClick(l, v, position, id);
         
         if(position>=0){

             Long idCategoria=category.get(position).get_id();
             String nombreCategoria=category.get(position).getNombre().toString();
             
             if(idCategoria>9){
                 
                 CharSequence[] choiceList = { getText(R.string.listar_lugares).toString(),getText(R.string.editar_categoria).toString() };
                 accion=showDialogShareButtonClick(position,choiceList,idCategoria,nombreCategoria);
             
             }else{
               
                 CharSequence[] choiceList = { getText(R.string.listar_lugares).toString()};
                 accion=showDialogShareButtonClick(position,choiceList,idCategoria,nombreCategoria);
             }
             
            
         }
         
        
     }
    
    private String showDialogShareButtonClick(int position,final CharSequence[] choiceList,final Long idCategoria,final String nombreCategoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        builder.setTitle(getText(R.string.accion).toString()+" "+nombreCategoria);
       
        
        builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                buffKey = which;

                // set buff to selected
                selected = buffKey;
                
                if(selected==0){
                   accion=getText(R.string.listar_lugares).toString();
                }
                if(selected==1){
                    accion=getText(R.string.editar_categoria).toString();
                }
                
                
                if(accion!=null && accion.equals(getText(R.string.listar_lugares).toString())){
                    

                    Uri uriLgares = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;
                    final String[] columnasLugares = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};

                    Cursor cursorLugares = getContentResolver().query(uriLgares,columnasLugares,LugaresDB.Lugares.CATEGORIA + " = '" + idCategoria + "'" ,null, null);

                    if(cursorLugares.getCount()>0){
                    
                        AsyncLoading asyncLoading = new AsyncLoading(context);
                        asyncLoading.execute();
                
                        //Creamos el item que llamara a la activity ListaLugaresCategoriaActivity
                        Intent intent = new Intent(ListaCategoriasActivity.this, ListaLugaresCategoriaActivity.class);
             
                        //Le pasamos el id de la categoria seleccionada para poder cargar posteriormente sus datos
                        intent.putExtra("idCategoria", String.valueOf(idCategoria).toString());
                        startActivity(intent);
                        finish(); 
                    }else{
                    
                        Toast.makeText(getApplicationContext(),R.string.no_lugares_categoria,Toast.LENGTH_LONG).show();
                    
                    }
                }
                
                if(accion!=null && accion.equals(getText(R.string.editar_categoria).toString())){
                    
                    Intent intent = new Intent();
                    intent.putExtra("idCategoria", idCategoria);
                    intent.putExtra("modo", "editarCategoria");
                    intent.setClass(getApplicationContext(), CrearCategoriaActivity.class);
                    startActivity(intent);
                    
                }
                
            }
        
        });

        AlertDialog alert = builder.create();
        alert.show();
        
        return accion;
    }
    
    /**
    * Metodo que guarda en BD los datos de una categoria
    */
    public void nuevaCategoria(View v) {
        
        Intent intent = new Intent();
        intent.putExtra("modo", "crearCategoria");
        intent.setClass(getApplicationContext(), CrearCategoriaActivity.class);
        startActivity(intent);

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
        menuInflater.inflate(R.menu.menu_categorias, menu);
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
                
           // Al pulsar sobre "nueva categoria"
            case R.id.nuevaCategoria:
                Intent intent = new Intent();
                intent.putExtra("modo", "crearCategoria");
                intent.setClass(getApplicationContext(), CrearCategoriaActivity.class);
                startActivity(intent);
                return true;

            // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void initControls(){

        categoriasView = (ListView) findViewById(android.R.id.list);
        
        MyViewFlipper = (ContentViewFlipper)findViewById(R.id.viewflipperCategorias);
    }
 
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        selected = 0;
        buffKey  = 0;
        accion = "";
    
    }
    
    
    
}
