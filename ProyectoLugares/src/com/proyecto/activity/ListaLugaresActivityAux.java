package com.proyecto.activity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.entity.LugarEntity;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ContentViewFlipper;
import com.proyecto.utility.ToastPersonalizado;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad que muestra una lista de los lugares almacenados en el sistema
 */
public class ListaLugaresActivityAux extends ListActivity {
	
    private ListView lugaresView;

	//Contexto
	private Context context = this;

	//id del Lugar
    private Long idLugarAux;
    
    private Long idCategoriaAux;
    
	//Array para almacenar los lugares que recogemos de la BD
	private ArrayList<com.proyecto.entity.LugarEntity> lugares; //= new ArrayList<com.proyecto.entity.LugarEntity>();
	
	private ContentViewFlipper MyViewFlipper;
	
	private LugaresAsyncTask lugaresAsyncTask;
	
	private com.proyecto.adapter.ListaLugaresAdapter adapterLugares;
	
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
        
		//Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		//Cargamos el layout
		setContentView(R.layout.lista_lugares);
		
		initControls();
		 
		//Animation
		Animation animationFlipIn  = AnimationUtils.loadAnimation(this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(this, R.anim.flipout);
        
        MyViewFlipper.setAnimation(animationFlipIn);
        MyViewFlipper.setOutAnimation(animationFlipOut);

        @SuppressWarnings("unchecked")
        final ArrayList<LugarEntity> data = (ArrayList<LugarEntity>) getLastNonConfigurationInstance();
        
        if (data == null) {
            lugares = new ArrayList<com.proyecto.entity.LugarEntity>();
            adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(ListaLugaresActivityAux.this,R.layout.lugares_item,lugares);
            lanzarLugares();
        }else{
            lugares = data;
            adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(ListaLugaresActivityAux.this,R.layout.lugares_item,lugares);
        }

        //Fijamos el Adapter de lugares
        setListAdapter(adapterLugares);

        registerForContextMenu(lugaresView);
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
		Intent intent = new Intent(ListaLugaresActivityAux.this, MostrarLugarActivityAux.class);
		intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);

        //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
		intent.putExtra(LugaresDB.Lugares._ID, lugares.get(position).get_id());
		intent.putExtra("idLugar", lugares.get(position).get_id());
		startActivity(intent);
		finish();  
	   
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
        menuInflater.inflate(R.menu.menu_lista, menu);
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

            case R.id.eliminar_lugares:
                
                if(lugares!=null && lugares.size()>0){
                    
                    Dialog confirmarBorrado;
                    confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGARES);
                    confirmarBorrado.show();
                    
                }else{
                    
                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.no_existen_lugares).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                    
                }
                
                return true;
                
            case R.id.vistaGaleria:
                
                if(lugares!=null && lugares.size()>0){
                    
                  //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                    AsyncLoading asyncLoading = new AsyncLoading(context);
                    asyncLoading.execute();
                    
                    Intent intentGaleria = new Intent();
                    intentGaleria.setClass(getApplicationContext(), GaleriaLugaresActivity.class);
                    startActivity(intentGaleria);
                    finish(); 
                    
                }else{
                    
                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.al_menos_un_lugar).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                }
                
                return true;
               
            case R.id.ordenarNombre:
                
                ordenarLugaresPorNombre();
                
                return true;
                
            case R.id.ordenarCategoria:
                
                ordenarLugaresPorCategoria();
                
                return true;
                
                // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	    
	    
    @Override
    protected Dialog onCreateDialog(int idAux) {
        Dialog newDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.custom_dialog_aux, null);
        
        TextView text = (TextView) textEntryView.findViewById (R.id.dialog_text);         

        text.setText(getText(R.string.info_delete).toString());
        
        switch (idAux) {
            case Constants.DIALOGO_BORRADO_LUGARES:
                builder.setTitle(R.string.eliminar_lugares);
                builder.setView(textEntryView); 
                builder.setIcon(R.drawable.warning);
                builder.setMessage(R.string.confirmar_eliminar_lugares);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int idAux) {

                        int borrado = getContentResolver().delete(LugaresDB.CONTENT_URI_LUGARES, null, null);
                        if (borrado > 0) {

                            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugares_eliminados_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                            
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), TabLugaresActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.lugares_eliminados_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int idAux) {
                        dialog.cancel();
                    }
                });
                newDialog = builder.create();
                break;
                
            case Constants.DIALOGO_BORRADO_LUGAR:
                builder.setTitle(R.string.eliminar_lugar);
                builder.setView(textEntryView);
                builder.setIcon(R.drawable.warning);
                builder.setMessage(getText(R.string.confirmar_eliminar_lugar).toString());
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int idAux) {

                        int borrado = getContentResolver().delete(
                                LugaresDB.CONTENT_URI_LUGARES,
                                LugaresDB.Lugares._ID + "=?", new String[] { "" + idLugarAux });
                        if (borrado > 0) {
                            
                            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugar_eliminado_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
  
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), TabLugaresActivity.class);
                            startActivity(intent);
                            finish();
                            
                        } else {// el elemento no se ha podido borrar
                            Toast.makeText(getApplicationContext(),R.string.lugar_eliminado_error,Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                    public void onClick(DialogInterface dialog, int idAux) {
                        dialog.cancel();
                    }
                });
                newDialog = builder.create();
                break;

        }

        return newDialog;

    }  
    
    private void initControls(){

        lugaresView = (ListView) findViewById(android.R.id.list);
        
        MyViewFlipper = (ContentViewFlipper)findViewById(R.id.viewflipper);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

            menu.setHeaderTitle(R.string.accion);

            menu.add(0, Constants.MENU_ELIMINAR, 0, getResources().getText(R.string.eliminar));

            menu.add(0, Constants.MENU_EDITAR, 1, getResources().getText(R.string.editar));

            menu.add(0, Constants.MENU_MOSTRAR, 2, getResources().getText(R.string.mostrar));
    }
    

    /**
     * Gestionamos la pulsacion de un menu contextual
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

            switch (item.getItemId()) {
            case Constants.MENU_ELIMINAR:
                
                        LugarEntity lugar=getLugar((int) info.id);
                        
                        if(lugar!=null)
                            idLugarAux=lugar.get_id();
                        
                        eliminarLugar();
                        
                        break;

            case Constants.MENU_EDITAR:

                    LugarEntity lugar2=getLugar((int) info.id);
                
                    if(lugar2!=null){
                        idCategoriaAux = lugar2.getCategoria();
                        idLugarAux = lugar2.get_id();
                    }
                    
                    String nombreCategoria=obtenerNombreCategoria(idCategoriaAux);
                    
                    editarLugar(idLugarAux,nombreCategoria);

                    return true;
                    
            case Constants.MENU_MOSTRAR:

                LugarEntity lugarMostrar=getLugar((int) info.id);
            
                if(lugarMostrar!=null){
                    idCategoriaAux = lugarMostrar.getCategoria();
                    idLugarAux = lugarMostrar.get_id();
                }
                
                mostrarLugar(idLugarAux);
               

                return true;

            default:
                    return super.onContextItemSelected(item);
            }
            return false;
    }


    public void eliminarLugar(){

            // Confirmar borrado
           Dialog confirmarBorrado;
           confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGAR);
           confirmarBorrado.show();

    }
    
   public void editarLugar(Long idLugar,String nombreCategoria){
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        intent.putExtra("idLugar", idLugar);
        intent.putExtra("modo", "editarLugar");
        intent.putExtra("nombreCategoria", nombreCategoria);
        intent.setClass(getApplicationContext(), EditarLugarActivityAux.class);
        startActivity(intent);
        finish();
        
    }
   
   public void mostrarLugar(Long idLugar){
       
     //Creamos el item que llamara a la activity MostrarLugarActivity
       Intent intent = new Intent(ListaLugaresActivityAux.this, MostrarLugarActivityAux.class);
       intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);

       //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
       intent.putExtra(LugaresDB.Lugares._ID, idLugar);
       intent.putExtra("idLugar", idLugar);
       startActivity(intent);
       finish();  
   }
 
    
   public String obtenerNombreCategoria(Long idCategoria){
       
       String nombreCategoria="";

       Uri uriCategorias = com.proyecto.database.LugaresProvider.CONTENT_URI_CATEGORIAS;
       final String[] columnasCategorias = {LugaresDB.Lugares._ID,LugaresDB.Lugares.CATEGORIA, LugaresDB.Lugares.DESCRIPCION};
   
       Cursor cursorCategorias = getContentResolver().query(uriCategorias,columnasCategorias,LugaresDB.Lugares._ID+ " = '" + idCategoria + "'" ,null, null);
   
       //colocamos el cursor en el primer elemento
       cursorCategorias.moveToFirst();

       for (int i = 0; i < cursorCategorias.getCount(); i++) {
           nombreCategoria = cursorCategorias.getString(cursorCategorias.getColumnIndex(LugaresDB.Lugares.CATEGORIA));

           //avanzar el cursor al siguiente elemento para siguiente iteracion
           cursorCategorias.moveToNext();
       }
   
    
       return nombreCategoria;
   
   }
   
   public LugarEntity getLugar(int position){
       LugarEntity lugar=new LugarEntity();
       
       if(lugares!=null && lugares.size()>0){
           lugar=lugares.get(position);
       }
       
       return lugar;
   }
   
   public void ordenarLugaresPorNombre(){
       
     //Campos a recuperar en la consulta
       final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};
       
       //Uri de la tabla a consultar
       Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;

       lugares=new ArrayList<LugarEntity>();
       
       Cursor cursor = getContentResolver().query(uri,columnas, null, null, LugaresDB.Lugares.NOMBRE_SORT_ORDER);

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
           lugares.add(lugar);
           //avanzar el cursor al siguiente elemento para siguiente iteracion
           cursor.moveToNext();
       }
       
       //Si hay un cursor abierto, se cierra
       if (cursor!= null) {
           cursor.close();
       }
       
       com.proyecto.adapter.ListaLugaresAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(ListaLugaresActivityAux.this,R.layout.lugares_item,lugares);

       //Fijamos el Adapter de lugares
       setListAdapter(adapterLugares);
       lugaresView = (ListView) findViewById(android.R.id.list);

       registerForContextMenu(lugaresView);
       
   }
   
   public void ordenarLugaresPorCategoria(){
       
       //Campos a recuperar en la consulta
         final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};
         
         //Uri de la tabla a consultar
         Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;

         lugares=new ArrayList<LugarEntity>();
         
         Cursor cursor = getContentResolver().query(uri,columnas, null, null, LugaresDB.Lugares.CATEGORIA_SORT_ORDER);

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
             lugares.add(lugar);
             //avanzar el cursor al siguiente elemento para siguiente iteracion
             cursor.moveToNext();
         }
         
         //Si hay un cursor abierto, se cierra
         if (cursor!= null) {
             cursor.close();
         }
         
         com.proyecto.adapter.ListaLugaresAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(ListaLugaresActivityAux.this,R.layout.lugares_item,lugares);
        
         //Fijamos el Adapter de lugares
         setListAdapter(adapterLugares);
         lugaresView = (ListView) findViewById(android.R.id.list);

         registerForContextMenu(lugaresView);
         
     }
   
   public void lanzarLugares(){

         lugaresAsyncTask = new LugaresAsyncTask();
         lugaresAsyncTask.execute();
     
   }
   
   private class LugaresAsyncTask extends AsyncTask<URL, String, List<LugarEntity>>{
       ArrayList<LugarEntity> lugares;
       ProgressDialog progressDialog;

       @Override
       protected List<LugarEntity> doInBackground(URL... params) {

           lugares = new ArrayList<LugarEntity>();
           
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
               lugares.add(lugar);
               //avanzar el cursor al siguiente elemento para siguiente iteracion
               cursor.moveToNext();
           }
           
           //Si hay un cursor abierto, se cierra
           if (cursor!= null) {
               cursor.close();
           }

           return lugares;
       }
       
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           progressDialog = ProgressDialog.show(ListaLugaresActivityAux.this, getString(R.string.cargando), getString(R.string.cargando_datos),true,true);
           progressDialog.setOnCancelListener(new OnCancelListener() {
               
               @Override
               public void onCancel(DialogInterface dialog) {
                   lugaresAsyncTask.cancel(true);
               }
           });
       }
   
       @Override
       protected void onCancelled() {
           super.onCancelled();
           lugares.clear();
           lugares = new ArrayList<LugarEntity>();
       }
   
       @Override
       protected void onProgressUpdate(String... progreso) {
           super.onProgressUpdate(progreso);
           progressDialog.setMessage(progreso[0]);
           adapterLugares.notifyDataSetChanged();
       }
   
       @Override
       protected void onPostExecute(List<LugarEntity> result) {
           super.onPostExecute(result);
           for(LugarEntity n:lugares){
               adapterLugares.add(n);
           }
           adapterLugares.notifyDataSetChanged();
           progressDialog.dismiss();
           
           if(lugares!=null && lugares.size()>0){
               Toast.makeText(getApplicationContext(),lugares.size()+" "+getText(R.string.lugares_encontrados),Toast.LENGTH_LONG).show();
               
           }else{
               Toast.makeText(getApplicationContext(),getText(R.string.crear_lugar_mapa).toString(),Toast.LENGTH_LONG).show();
           }

       }
      
  }
   
   @Override
   public Object onRetainNonConfigurationInstance() {
       final ArrayList<LugarEntity> data = lugares;
       return data;
   }
   
   
}
