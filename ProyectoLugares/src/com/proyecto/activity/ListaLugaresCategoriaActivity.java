package com.proyecto.activity;

import java.util.ArrayList;
import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.entity.LugarEntity;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ContentViewFlipper;
import com.proyecto.utility.ToastPersonalizado;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Actividad que muestra una lista de los lugares almacenados en el sistema
 */
public class ListaLugaresCategoriaActivity extends ListActivity {
	
	//Contexto
	private Context context = this;

	private String idCategoria="";
	private String nombreCategoria="";
	
	//Array para almacenar los lugares que recogemos de la BD
	private ArrayList<com.proyecto.entity.LugarEntity> lugares = new ArrayList<com.proyecto.entity.LugarEntity>();
	
	private ContentViewFlipper MyViewFlipper;
	
	//Datos categoria
	private TextView textCategoria;
    private ImageView iconCategory;
    
    private ListView lugaresView;
    
    private Long idLugarAux;
	
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
		setContentView(R.layout.lista_lugares_categoria);
		
		initControls();
		 
		//Animation
		Animation animationFlipIn  = AnimationUtils.loadAnimation(this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(this, R.anim.flipout);
        
        MyViewFlipper.setAnimation(animationFlipIn);
        MyViewFlipper.setOutAnimation(animationFlipOut);
        
		//Campos a recuperar en la consulta
		final String[] columnasLugares = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};
		final String[] columnasCategorias = {LugaresDB.Lugares._ID,LugaresDB.Lugares.CATEGORIA, LugaresDB.Lugares.DESCRIPCION};
        
		//Uri de la tabla a consultar
		Uri uriLgares = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;
		Uri uriCategorias = com.proyecto.database.LugaresProvider.CONTENT_URI_CATEGORIAS;

		//Comprobamos en que modo debemos cargar la activity
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            idCategoria = extras.getString("idCategoria");
            
        }

        Cursor cursorCategorias = getContentResolver().query(uriCategorias,columnasCategorias,LugaresDB.Lugares._ID+ " = '" + idCategoria + "'" ,null, null);
        
        //colocamos el cursor en el primer elemento
        cursorCategorias.moveToFirst();

        for (int i = 0; i < cursorCategorias.getCount(); i++) {
            //int idCategoria = cursorCategorias.getInt(cursorCategorias.getColumnIndex(LugaresDB.Lugares._ID));
            nombreCategoria = cursorCategorias.getString(cursorCategorias.getColumnIndex(LugaresDB.Lugares.CATEGORIA));

            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursorCategorias.moveToNext();
        }
        
        if(nombreCategoria!=null && !nombreCategoria.equals("")){
            textCategoria.setText(nombreCategoria);
        }
        
        //obtenemos icono de la categoria
        iconoCategoria(nombreCategoria);
        
        //Obtenemos lugares para la categoria seleccionada
        
		Cursor cursorLugares = getContentResolver().query(uriLgares,columnasLugares,LugaresDB.Lugares.CATEGORIA + " = '" + idCategoria + "'" ,null, null);

		//colocamos el cursor en el primer elemento
		cursorLugares.moveToFirst();

        for (int i = 0; i < cursorLugares.getCount(); i++) {
            String path_foto="";
            int id = cursorLugares.getInt(cursorLugares.getColumnIndex(LugaresDB.Lugares._ID));
            String nombre = cursorLugares.getString(cursorLugares.getColumnIndex(LugaresDB.Lugares.NOMBRE));
            String descripcion = cursorLugares.getString(cursorLugares.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
            if(cursorLugares.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                path_foto = cursorLugares.getString(cursorLugares.getColumnIndex(LugaresDB.Lugares.FOTO));
            }
            com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(id,nombre,descripcion,path_foto,new Long(idCategoria));
            lugares.add(lugar);
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursorLugares.moveToNext();
        }
        
        //Si hay un cursor abierto, se cierra
        if (cursorLugares!= null) {
            cursorLugares.close();
        }
        
        if (cursorCategorias!= null) {
            cursorCategorias.close();
        }
        
        com.proyecto.adapter.ListaLugaresAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(ListaLugaresCategoriaActivity.this,R.layout.lugares_item,lugares);

        
       //Fijamos el Adapter de lugares
        setListAdapter(adapterLugares);
        lugaresView = (ListView) findViewById(android.R.id.list);

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
		Intent intent = new Intent(ListaLugaresCategoriaActivity.this, MostrarLugarActivityAux.class);
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
            
           //Ventana que se muestra de forma asíncrona mientras se cargan los datos
            AsyncLoading asyncLoading = new AsyncLoading(context);
            asyncLoading.execute();
            
            Intent intent = new Intent();
            intent.putExtra("modo","categorias");
            intent.setClass(getApplicationContext(), TabLugaresActivity.class);
            startActivity(intent);
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
        menuInflater.inflate(R.menu.menu_lista_categoria, menu);
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
                
            case R.id.eliminar_lugares:
                
                if(lugares!=null && lugares.size()>0){
                    
                    Dialog confirmarBorrado;
                    confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGARES);
                    confirmarBorrado.show();
                    
                }else{
                    
                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.no_existen_lugares).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                    
                }
                
                return true;
                
            case R.id.ordenarNombre:
                
                ordenarLugaresPorNombre();
                
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
                builder.setMessage(getText(R.string.confirmar_eliminar_lugares_categoria).toString()+" "+ nombreCategoria+" ?");
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int idAux) {

                        int borrado = getContentResolver().delete(LugaresDB.CONTENT_URI_LUGARES, LugaresDB.Lugares.CATEGORIA + " = '" + idCategoria + "'", null);
                        if (borrado > 0) {

                            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugares_eliminados_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                            
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), ListaLugaresCategoriaActivity.class);
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
                            intent.putExtra("idCategoria", idCategoria);
                            intent.setClass(getApplicationContext(), ListaLugaresCategoriaActivity.class);
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

        MyViewFlipper = (ContentViewFlipper)findViewById(R.id.viewflipper);
        textCategoria = (TextView)findViewById(R.id.textCategoria);
        iconCategory = (ImageView)findViewById(R.id.iconCategory);
    }
    
    private void iconoCategoria(String nombreCategoria){

        if(nombreCategoria.equals(getText(R.string.restaurantes).toString())){
            iconCategory.setImageResource(R.drawable.restaurant);
        }
        
        if(nombreCategoria.equals(getText(R.string.bares).toString())){
            iconCategory.setImageResource(R.drawable.bar);
        }
        
        if(nombreCategoria.equals(getText(R.string.cafe).toString())){
            iconCategory.setImageResource(R.drawable.cafe);
        }
        
        if(nombreCategoria.equals(getText(R.string.compras).toString())){
            iconCategory.setImageResource(R.drawable.compras);
        }
        
        if(nombreCategoria.equals(getText(R.string.hoteles).toString())){
            iconCategory.setImageResource(R.drawable.hoteles);
        }
        
        if(nombreCategoria.equals(getText(R.string.musica).toString())){
            iconCategory.setImageResource(R.drawable.musica);
        }
        
        if(nombreCategoria.equals(getText(R.string.parques).toString())){
            iconCategory.setImageResource(R.drawable.parques);
        }
        
        if(nombreCategoria.equals(getText(R.string.aeropuertos).toString())){
            iconCategory.setImageResource(R.drawable.aeropuertos);
        }
        
        if(nombreCategoria.equals(getText(R.string.parking).toString())){
            iconCategory.setImageResource(R.drawable.parking);
        }
        
        if(nombreCategoria.equals(getText(R.string.universidades).toString())){
            iconCategory.setImageResource(R.drawable.university);
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
    public void ordenarLugaresPorNombre(){
        
       //Campos a recuperar en la consulta
        final String[] columnasLugares = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};

        //Uri de la tabla a consultar
        Uri uriLgares = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;
        
        lugares=new ArrayList<LugarEntity>();
        
        //Obtenemos lugares para la categoria seleccionada
        
        Cursor cursorLugares = getContentResolver().query(uriLgares,columnasLugares,LugaresDB.Lugares.CATEGORIA + " = '" + idCategoria + "'" ,null, LugaresDB.Lugares.NOMBRE_SORT_ORDER);

        //colocamos el cursor en el primer elemento
        cursorLugares.moveToFirst();

        for (int i = 0; i < cursorLugares.getCount(); i++) {
            String path_foto="";
            int id = cursorLugares.getInt(cursorLugares.getColumnIndex(LugaresDB.Lugares._ID));
            String nombre = cursorLugares.getString(cursorLugares.getColumnIndex(LugaresDB.Lugares.NOMBRE));
            String descripcion = cursorLugares.getString(cursorLugares.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
            if(cursorLugares.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                path_foto = cursorLugares.getString(cursorLugares.getColumnIndex(LugaresDB.Lugares.FOTO));
            }
            com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(id,nombre,descripcion,path_foto,new Long(idCategoria));
            lugares.add(lugar);
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursorLugares.moveToNext();
        }
        
        //Si hay un cursor abierto, se cierra
        if (cursorLugares!= null) {
            cursorLugares.close();
        }
        
        
        com.proyecto.adapter.ListaLugaresAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresAdapter(ListaLugaresCategoriaActivity.this,R.layout.lugares_item,lugares);
        
       //Fijamos el Adapter de lugares
        setListAdapter(adapterLugares);
        lugaresView = (ListView) findViewById(android.R.id.list);

        registerForContextMenu(lugaresView);
          
      }
    
    public void eliminarLugarCategoria() {

        // Confirmar borrado
        Dialog confirmarBorrado;
        confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGAR);
        confirmarBorrado.show();

    }

    public void editarLugarCategoria(Long idLugar, String nombreCategoria) {

        // Ventana que se muestra de forma asíncrona mientras se cargan los
        // datos
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

                LugarEntity lugar = getLugar((int) info.id);

                if (lugar != null) idLugarAux = lugar.get_id();

                eliminarLugarCategoria();

                break;

            case Constants.MENU_EDITAR:

                LugarEntity lugar2 = getLugar((int) info.id);

                if (lugar2 != null) {
                    idLugarAux = lugar2.get_id();
                }

                editarLugarCategoria(idLugarAux, nombreCategoria);

                return true;
                
            case Constants.MENU_MOSTRAR:

                LugarEntity lugarMostrar=getLugar((int) info.id);
            
                if(lugarMostrar!=null){
                    idLugarAux = lugarMostrar.get_id();
                }
                
                mostrarLugar(idLugarAux);
               

                return true;

            default:
                return super.onContextItemSelected(item);
        }
        return false;
    }

    public LugarEntity getLugar(int position) {
        LugarEntity lugar = new LugarEntity();

        if (lugares != null && lugares.size() > 0) {
            lugar = lugares.get(position);
        }

        return lugar;
    }
    
    public void mostrarLugar(Long idLugar){
        
        //Creamos el item que llamara a la activity MostrarLugarActivity
          Intent intent = new Intent(ListaLugaresCategoriaActivity.this, MostrarLugarActivityAux.class);
          intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);

          //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
          intent.putExtra(LugaresDB.Lugares._ID, idLugar);
          intent.putExtra("idLugar", idLugar);
          startActivity(intent);
          finish();  
    }
}
