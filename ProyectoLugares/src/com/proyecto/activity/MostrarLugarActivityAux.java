package com.proyecto.activity;

import java.io.File;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.map.LugarMapaItemizedOverlay;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.StatusBarNotify;
import com.proyecto.utility.ToastPersonalizado;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad que muestra los datos en detalle del lugar seleccionado
 */
public class MostrarLugarActivityAux extends MapActivity {

	//Elementos Layout para mostrar los datos
	private TextView nombreTextView;
	private TextView descripcionTextView;
	private TextView latitudTextView;
    private TextView longitudTextView;
	private ImageView imgFoto;
	private RatingBar ratingbar;
	private Button zoomImagen;
	private MapView mapa;
	private MapController mapController;
	private LugarMapaItemizedOverlay posicionMapa;
	private List<Overlay> mapOverlays;
	
	//datos de un lugar
	private Integer id;
	private Integer idCategoria;
	private String nombreCategoria="";
	private String nombre, descripcion;
	private Double latitud, longitud;
    private Float rating;
    private String foto;
    
    //Datos categoria
    private TextView textCategoria;
    private ImageView iconCategory;
    
	//id del Lugar a mostrar
	private Long idLugar;
	
    //variables de control
    private boolean control = false;
    private boolean desdeMapa = false;
    
    //Contexto
    private Context context = this;
  
    //dialogo compartir
    private int selected = 0;
    private int buffKey  = 0;
    
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

        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
		//Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		//Cargamos el layout
		setContentView(R.layout.mostrar_lugar);

		//inicializar controles
		initControls();

        //Datos para el mapa
        mapa.setBuiltInZoomControls(true);
        
        mapController = mapa.getController();
        mapController.setZoom(10);
        mapa.setSatellite(true);   //Activa vista satélite
        mapa.setStreetView(false);  //Desactiva StreetView
        mapa.setTraffic(false);     //Desactiva información de tráfico
        mapa.setClickable(false);   //no clickable
        
        mapOverlays = mapa.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
        posicionMapa = new LugarMapaItemizedOverlay(getApplicationContext(), drawable,mapa);
        
		//Recogemos el id del lugar que se nos proporcionar a traves del intent de llamada
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			idLugar = extras.getLong("idLugar");
			desdeMapa= extras.getBoolean("mostrarDesdeMapa");
		}
		
        
		//Realizamos la consulta a base de datos para obtener los datos del lugar
		recuperarDatos();
	}
	
    /**
     * Se llama al hacer click en el boton "Editar Lugar"
     * 
     * @param v
     *            View
     */
    public void editar(View v) {
        
        editarLugar();

    }

    /**
     * Se llama al hacer click en el boton "Eliminar Lugar"
     * 
     * @param v
     *            View
     */
    public void eliminar(View v) {
        
        eliminarLugar();

    }
    
    /**
     * Se llama al hacer click en el boton "Compartir"
     * 
     * @param v
     *            View
     */
    public void compartir(View v) {
        
        showDialogShareButtonClick();

    }
    
    /**
     * Se llama al hacer click en el boton "Ruta Google"
     * 
     * @param v
     *            View
     */
    public void rutaGoogle(View v) {
        
        rutaGoogleNavigation();

    }
    
    /**
     * Se llama al hacer click en el boton "Zoom imagen"
     * 
     * @param v
     *            View
     */
    public void zoomImagen(View v) {
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ZoomImagenActivity.class);
        intent.putExtra("rutaImagen",foto);
        startActivity(intent);

    }
	  
    /**
     * Se ejecuta al volver de la pantalla editar lugar
     * 
     */
	public void onResume(){
		super.onResume();

		  if (control) {
	            //setContentView(R.layout.mostrar_lugar);
	            nombreTextView = (TextView) this.findViewById(R.id.lugarNombre);
	            descripcionTextView = (TextView) this.findViewById(R.id.lugarDescripcion);
	            latitudTextView = (TextView) this.findViewById(R.id.lugarLatitud);
	            longitudTextView = (TextView) this.findViewById(R.id.lugarLongitud);
	            imgFoto = (ImageView) findViewById(R.id.imagenFoto);
	            ratingbar = (RatingBar)findViewById(R.id.idRatingbar);
	            zoomImagen = (Button) findViewById(R.id.botonZoomImagen);
	            textCategoria = (TextView)findViewById(R.id.textCategoria);
	            iconCategory = (ImageView)findViewById(R.id.iconCategory);
	            
	            //para recargar los datos
	            recuperarDatos();
	        }
	        
	      control = true;
		
	}
	
	
	 /**
     * Permite recuperar los datos del lugar seleccionado mediante consulta a BD
     * 
     */   
	public void recuperarDatos(){
		//Realizamos la consulta a base de datos para obtener los datos del lugar
		
		//Uri del elemento a recuperar
        Uri uri = ContentUris.withAppendedId(Uri.parse(com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES + "/#"), idLugar);
        
		//Obtenemos el cursor con los datos solicitados
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		
		File file = null;
		
		//Obtenemos los indices de las columnas que queremos consultar
		int indexId = cursor.getColumnIndex(LugaresDB.Lugares._ID);
		int indexNombre = cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE);
		int indexDescripcion = cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION);
		int indexFoto = cursor.getColumnIndex(LugaresDB.Lugares.FOTO);
		int indexLon = cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD);
		int indexLat = cursor.getColumnIndex(LugaresDB.Lugares.LATITUD);
		int indexRating = cursor.getColumnIndex(LugaresDB.Lugares.RATING);
		int indexCategoria = cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA);
		
		//Nos aseguramos de que existe al menos un registro y vamos recorriendo el cursor
		if ((cursor != null) && cursor.moveToFirst()) {
			//Accedemos a las columnas para obtener los datos
		    id =  cursor.getInt(indexId);
			nombre =  cursor.getString(indexNombre);
			descripcion = cursor.getString(indexDescripcion);
			foto = cursor.getString(indexFoto);
			
			longitud = new Double(cursor.getString(indexLon));
		    latitud = new Double(cursor.getString(indexLat));
		    if(cursor.getString(indexRating)!=null){
		        rating = new Float(cursor.getString(indexRating));
		    }
		    
			//Cargamos en pantalla los datos
		    if(nombre!=null){
		        nombreTextView.setText(nombre);
		    }
		    if(descripcion!=null){
		        descripcionTextView.setText(descripcion);
		    }
		    if(latitud!=null){
		        latitudTextView.setText(String.valueOf(latitud));
		    }
		    if(longitud!=null){
		        longitudTextView.setText(String.valueOf(longitud));
		    }
		    
		    if(rating!=null){
                ratingbar.setRating(rating);
            }
		    
	          //Parseamos la uri de la foto
            if(foto!=null){
                Uri uriFoto = Uri.parse(foto);
                file = new File(foto);
                if(file.exists()){ //si existe la imagen
                    imgFoto.setImageURI(uriFoto);
                    zoomImagen.setEnabled(true);
                }else{
                    imgFoto.setImageResource(R.drawable.no_imagen);
                    zoomImagen.setEnabled(false);
                }
            }
            
            //tratamiento categoria
            idCategoria =  cursor.getInt(indexCategoria);
            
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
            
            if(nombreCategoria!=null && !nombreCategoria.equals("")){
                textCategoria.setText(nombreCategoria);
            }
            
            //obtenemos icono de la categoria
            iconoCategoria(nombreCategoria);
            
		    posicionMapa.addLocalizacion(latitud, longitud, nombre,descripcion,id.toString(),rating);
		    
		    mapOverlays.clear();
	        mapOverlays.add(posicionMapa);
	        GeoPoint punto = new GeoPoint((int) (latitud * 1E6),(int) (longitud * 1E6));
	        mapController.animateTo(punto);
	        mapController.setZoom(15);
			
		} else {
		    
			//En caso de no existir el registro indicamos que ha habido un error al recuperar de BD
			nombreTextView.setText(R.string.errorRegistroBD);
			descripcionTextView.setText(R.string.errorRegistroBD);
		    imgFoto.setImageResource(R.drawable.no_imagen);
		    zoomImagen.setVisibility(View.GONE);
		}
		
		//Si hay un cursor abierto, se cierra
        if (cursor != null) {
            cursor.close();
        }
	}
	
	 /**
     * Comportamiento de la tecla ATRAS del terminal
     * @keyCode int Codigo de la tecla pulsada
     * @event KeyEvent Evento que ha provocado la llamada al metodo
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          
            volver();
            
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            case Constants.DIALOGO_BORRADO_LUGAR:
                builder.setTitle(R.string.eliminar_lugar);
                builder.setMessage(R.string.confirmar_eliminar_lugar);
                builder.setView(textEntryView); 
                builder.setIcon(R.drawable.warning);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int idAux) {

                        int borrado = getContentResolver().delete(
                                LugaresDB.CONTENT_URI_LUGARES,
                                LugaresDB.Lugares._ID + "=?", new String[] { "" + idLugar });
                        if (borrado > 0) {
                            
                            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugar_eliminado_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
  
                            StatusBarNotify.getInstance(context).statusBarNotify("eliminar",nombre,descripcion);
                            
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
    
    /**
     * Se muestran las opciones de menú de la pantalla mostrar
     * @param menu Objeto menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mostrar, menu);
        return true;
    }
    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * @param item MenuItem 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar sobre "Editar"
            case R.id.btnEditarLugar:
                
                editarLugar();
                
                return true;
            // Al pulsar sobre "Eliminar"
            case R.id.btnEliminarLugar:
                
                eliminarLugar();
                
                return true;
            // Al pulsar sobre "Volver"
            case R.id.volver:
                
                volver();

                return true;
                
             // Al pulsar sobre "Compartir"
            case R.id.btnCompartirLugar:
                
                showDialogShareButtonClick();

                return true;
            
            // Al pulsar sobre "Ruta google"
            case R.id.rutaGoogle:
                
                rutaGoogleNavigation();

                return true;
            //Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void editarLugar(){
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        intent.putExtra("idLugar", idLugar);
        intent.putExtra("modo", "editarLugar");
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("nombreCategoria", nombreCategoria);
        intent.setClass(getApplicationContext(), EditarLugarActivityAux.class);
        startActivity(intent);
        finish();
        
    }
    
    public void eliminarLugar(){
        
     // Confirmar borrado
        Dialog confirmarBorrado;
        confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGAR);
        confirmarBorrado.show();

    }
    
    public void rutaGoogleNavigation(){
        
        //lanzar la aplicacion "navigation" pasando como parametros las coordenadas del punto al que deseas ir

        Intent intentNavigate = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +latitud+","+longitud));

        intentNavigate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        startActivity(intentNavigate);

    }
    
    public void enviarSMS(){
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        String datos=getText(R.string.lugar_favorito).toString()+"\n\n"+
                     getText(R.string.nombre).toString()+": "+nombre+"\n"+
                     getText(R.string.descripcion).toString()+": "+descripcion+"\n"+
                     getText(R.string.latitud).toString()+": "+latitud+"\n"+
                     getText(R.string.longitud).toString()+": "+longitud;
                
        intent.putExtra("datos",datos);
        intent.setClass(getApplicationContext(), SendSMSActivity.class);
        startActivity(intent);
        
    }
    
    public void enviarMAIL(){
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        Intent intent = new Intent();
        String datos=
                     getText(R.string.nombre).toString()+": "+nombre+"\n"+
                     getText(R.string.descripcion).toString()+": "+descripcion+"\n"+
                     getText(R.string.latitud).toString()+": "+latitud+"\n"+
                     getText(R.string.longitud).toString()+": "+longitud;
                
        intent.putExtra("datos",datos);
        intent.putExtra("foto",foto);
        intent.setClass(getApplicationContext(), SendMAILActivity.class);
        startActivity(intent);
        
    }
    
    public void volver(){
        
        if(desdeMapa){
            finish();
        }else{
            
            //Ventana que se muestra de forma asíncrona mientras se cargan los datos
            AsyncLoading asyncLoading = new AsyncLoading(context);
            asyncLoading.execute();
            
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), TabLugaresActivity.class);
            startActivity(intent);
            finish();
        }
       }
    
    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
      super.onActivityResult(requestCode, resultCode, data); 
      
    }
    
    

    private void showDialogShareButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        builder.setTitle(getText(R.string.compartir).toString());
        
        final CharSequence[] choiceList = { getText(R.string.enviar_sms).toString(),getText(R.string.enviar_mail).toString() };

        builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                buffKey = which;
            }
        }).setCancelable(false).setPositiveButton(getText(R.string.Aceptar).toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // set buff to selected
                selected = buffKey;
                if(selected==0){
                    enviarSMS();
                }
                if(selected==1){
                    enviarMAIL();
                }
            }
        }).setNegativeButton(getText(R.string.cancel).toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initControls(){

        mapa = (MapView) this.findViewById(R.id.vistaMapa);
        
        //Recogemos los elementos de la interfaz que nos hacen falta para cargar los datos
        nombreTextView = (TextView) this.findViewById(R.id.lugarNombre);
        descripcionTextView = (TextView) this.findViewById(R.id.lugarDescripcion);
        latitudTextView = (TextView) this.findViewById(R.id.lugarLatitud);
        longitudTextView = (TextView) this.findViewById(R.id.lugarLongitud);
        imgFoto = (ImageView) findViewById(R.id.imagenFoto);
        imgFoto.setImageResource(R.drawable.no_imagen);
        ratingbar = (RatingBar)findViewById(R.id.idRatingbar);
        zoomImagen = (Button) findViewById(R.id.botonZoomImagen);
        zoomImagen.setVisibility(View.GONE);
        textCategoria = (TextView)findViewById(R.id.textCategoria);
        iconCategory = (ImageView)findViewById(R.id.iconCategory);
        
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
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
    
}
