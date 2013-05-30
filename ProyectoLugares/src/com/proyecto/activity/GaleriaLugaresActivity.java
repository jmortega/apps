package com.proyecto.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.map.LugarMapaItemizedOverlay;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ToastPersonalizado;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Actividad que muestra una lista de los lugares almacenados en el sistema
 */
public class GaleriaLugaresActivity extends MapActivity {
	
	//Contexto
	private Context context = this;

	//Layout para mostrar el numero de lugares registrados
	private TextView contadorLugaresView;
	
	//Elementos para el mapa
    private MapView mapa;
    private MapController mapController;
    private Drawable drawable;
    private List<Overlay> mapOverlays;
    private ViewFlipper MyViewFlipper;
    
	//Array para almacenar los lugares que recogemos de la BD
	ArrayList<com.proyecto.entity.LugarEntity> lugares = new ArrayList<com.proyecto.entity.LugarEntity>();
	
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
		setContentView(R.layout.galeria_lista_lugares);

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
		
        //establecemos propiedades del mapa
        mapa.setBuiltInZoomControls(false);//Desactiva controles zoom
        mapa.setSatellite(true);          //Activa vista satélite
        mapa.setStreetView(false);        //Desactiva StreetView
        mapa.setTraffic(false);           //Desactiva información de tráfico
        
        //Obtenemos el controlador del mapa
        mapController = mapa.getController();
        
        //Mostrar el mapa completo
        mapController.setZoom(1);
        
        //Cargamos el recurso que utilizaremos de marca
        drawable = this.getResources().getDrawable(R.drawable.marker2);
        
        //Obtenemos la lista de capas sobre el mapa
        mapOverlays = mapa.getOverlays();
        
		//Campos a recuperar en la consulta
		final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};
		
		//Uri de la tabla a consultar
		Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;
		
		//Obtenemos el cursor que nos permitira recorrer la lista de lugares
		Cursor cursor = managedQuery(uri, columnas, null, null, LugaresDB.Lugares.NOMBRE);
		
		// Queremos enterarnos si cambian los datos para recargar el cursor
        cursor.setNotificationUri(getContentResolver(), uri);
        
        mapOverlays.clear();
        
		//colocamos el cursor en el primer elemento
        cursor.moveToFirst();

        //Creamos una capa donde dibujaremos nuestras marcas
        LugarMapaItemizedOverlay itemizedoverlay = new LugarMapaItemizedOverlay(this, drawable,mapa);
        
        for (int i = 0; i < cursor.getCount(); i++) {
            String path_foto="";
            int id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
            Double latitud = cursor.getDouble(cursor.getColumnIndex(LugaresDB.Lugares.LATITUD));
            Double longitud = cursor.getDouble(cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD));
            String nombre = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE));
            String descripcion = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
            int categoria = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA));
            if(cursor.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                path_foto = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.FOTO));
            }
            com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(id,nombre,descripcion,path_foto,categoria);
            lugares.add(lugar);
            
            itemizedoverlay.addLocalizacion(latitud,longitud, nombre);
            
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursor.moveToNext();
        }
        
        mapOverlays.add(itemizedoverlay);
        
        
        //Si hay un cursor abierto, se cierra
        if (cursor != null) {
            cursor.close();
        }
        
        Gallery gallery = (Gallery)findViewById(android.R.id.list);
        
        com.proyecto.adapter.ListaLugaresGaleryAdapter adapterLugares = new com.proyecto.adapter.ListaLugaresGaleryAdapter(GaleriaLugaresActivity.this,R.layout.lugares_item_gallery,lugares);
        
        if(lugares!=null && lugares.size()>0){
            Toast.makeText(getApplicationContext(),lugares.size()+" "+getText(R.string.lugares_encontrados),Toast.LENGTH_LONG).show();
            
        }
        

        //Mostramos contador de lugares
        if(lugares!=null && lugares.size()>0){
            contadorLugaresView.setText(lugares.size()+" "+getText(R.string.lugares_encontrados));
        }else{
            contadorLugaresView.setText(getText(R.string.no_existen_lugares));
        }
        
		//Fijamos el Adapter de lugares
        gallery.setAdapter(adapterLugares);
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                AsyncLoading asyncLoading = new AsyncLoading(context);
                asyncLoading.execute();
                
                ///Creamos el item que llamara a la activity MostrarLugarActivity
                Intent intent = new Intent(GaleriaLugaresActivity.this, MostrarLugarActivityAux.class);
                intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);
                 
                //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
                intent.putExtra(LugaresDB.Lugares._ID, lugares.get(position).get_id());
                intent.putExtra("idLugar", lugares.get(position).get_id());
                startActivity(intent);
                finish();  
                
            }
            
        });

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
        menuInflater.inflate(R.menu.menu_galeria, menu);
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

            case R.id.delete_lugares:
                Dialog confirmarBorrado;
                confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGARES);
                confirmarBorrado.show();

                return true;
                
            case R.id.vistaLista:
                
                //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                AsyncLoading asyncLoading = new AsyncLoading(context);
                asyncLoading.execute();
                
                Intent intentLista = new Intent();
                intentLista.setClass(getApplicationContext(), TabLugaresActivity.class);
                startActivity(intentLista);
                finish();
                return true;
                
            case R.id.galeriaImagenes:
                
                //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                AsyncLoading asyncLoading2 = new AsyncLoading(context);
                asyncLoading2.execute();
                
                Intent intentGaleria = new Intent();
                intentGaleria.setClass(getApplicationContext(), ImagenesActivity.class);
                startActivity(intentGaleria);
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

        switch (idAux) {
            case Constants.DIALOGO_BORRADO_LUGARES:
                builder.setTitle(R.string.eliminar_lugares);
                builder.setMessage(R.string.confirmar_eliminar_lugares);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int idAux) {

                        int borrado = getContentResolver().delete(LugaresDB.CONTENT_URI_LUGARES, null, null);
                        if (borrado > 0) {

                            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugares_eliminados_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                            
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), GaleriaLugaresActivity.class);
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

        }

        return newDialog;

    }
	    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    private void initControls(){

        MyViewFlipper = (ViewFlipper)findViewById(R.id.viewflipper);
    
        mapa = (MapView) this.findViewById(R.id.mapa);
        
        contadorLugaresView = (TextView) this.findViewById(R.id.contadorLugares);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
}
