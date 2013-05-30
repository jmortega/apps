package com.proyecto.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.entity.LugarEntity;
import com.proyecto.map.LugarMapaItemizedOverlay;
import com.proyecto.map.LugaresLocationListener;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.LocationGeoCoder;
import com.proyecto.utility.NetWorkStatus;
import com.proyecto.utility.ToastPersonalizado;
/**
 * Actividad que muestra el mapa y los puntos que ha registrado el usuario 
 */
public class MapaLugaresActivityAux extends MapActivity{
    
    //Elementos Layout para mostrar los datos
    private MapView mapa;
	private MapController mapController;
	private Drawable drawable;
	private List<Overlay> mapOverlays;
	private GeoPoint geoPoint;
	private LocationManager locManager;
	
	//datos de un lugar
    private Double latitud, longitud;
    private String nombre;
    private String descripcion;
    private Integer id;
    private Integer idCategoria;
    private Float rating;
    
    private String bestProvider ="";
    
    String retorno="";
    
    //guardamos criterio de busqueda
    private String busqueda="";

    //guardamos resultado de busqueda
    private String resultadoBusqueda="";
    private Double latitudBusqueda, longitudBusqueda;
    private GeoPoint puntoBusqueda;
    
    private EditText lugar;
    
    private boolean buscar_lugar=false;
    private boolean buscar_mi_posicion=false;
    
    private Button toggle;
    private Button myPosicion;
    private Button buscar;

    private Dialog dialogo;
    
	//Lista de lugares
	private	List<LugarEntity> lugares = new ArrayList<LugarEntity>();
	
	//Contexto
	private Context context = this;
	
	private LugaresLocationListener lugaresListener;

	
	
	public GeoPoint getGeoPoint() {
	        return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
         this.geoPoint = geoPoint;
	}

	/**
    * Crea la vista e inicializa los componentes
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
		setContentView(R.layout.mapa);
		
		//inicializar controles
		initControls();
        
		//Ventana que se muestra de forma asíncrona mientras se cargan los datos
		AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
		//establecemos las propiedades del mapa
        mapa.displayZoomControls(true);
        
        mapa.setBuiltInZoomControls(true);//Activa controles zoom
        mapa.setSatellite(false);         //Desactiva vista satélite
        mapa.setStreetView(false);        //Desactiva StreetView
        mapa.setTraffic(false);           //Desactiva información de tráfico
        
		//Obtenemos el controlador del mapa
		mapController = mapa.getController();
		
		//ver mapa completo
		mapController.setZoom(1);
		
		//Cargamos el recurso que utilizaremos de marca
		drawable = this.getResources().getDrawable(R.drawable.marker);
		
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  drawable.getIntrinsicHeight());  
		
		//Obtenemos la lista de capas sobre el mapa
		mapOverlays = mapa.getOverlays();
		
		//Obtenemos la ultima posicion conocida por el Proveedor de Red
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    
        //Añadimos el manejador del GPS
        lugaresListener= new LugaresLocationListener(getApplicationContext(), mapController);
        
        this.locManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        geoPoint=null;
        
        //cargamos lugares en el mapa
        cargarLugares();
		
        mensajeEstadoRed();
        
        //Establece vista satelite activa/desactiva
        toggle = (Button) findViewById(R.id.idToggleButtonAux);
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapa.isSatellite()) {
                    mapa.setSatellite(false);
                } else {
                    mapa.setSatellite(true);
                }
            }
        });
        
        //Establece vista satelite activa/desactiva
        myPosicion = (Button) findViewById(R.id.idBotonMiPosicion);
        myPosicion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                
                //Ventana que se muestra de forma asíncrona mientras se realiza la busqueda
                MyLocationLoading searchLoading = new MyLocationLoading(context);
                searchLoading.execute();
                
            }
        });
        
        
        //texto buscar
        buscar = (Button) findViewById(R.id.botonBuscarMapa);
        buscar.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if(lugar!=null && lugar.getText()!=null && !lugar.getText().toString().equals("")){
                    
                    //Ventana que se muestra de forma asíncrona mientras se realiza la busqueda
                    SearchLoading searchLoading = new SearchLoading(context,lugar.getText().toString());
                    searchLoading.execute();
 
                }else{
                    
                    Toast.makeText(getApplicationContext(),getText(R.string.informar_busqueda).toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
		
	}

	/**
	 * Metodo que se encarga de cargar los lugares en el mapa
	 */
	public void cargarLugares() {
		//Obtenemos la informacion de la BD

	    if(buscar_lugar){
	        
            if(retorno.equals(getText(R.string.no_encontrado_lugar_mapa).toString())){
                Toast.makeText(getApplicationContext(),getText(R.string.no_encontrado_lugar_mapa).toString(),Toast.LENGTH_LONG).show();
            }
            
            if(retorno.equals(getText(R.string.conexion_internet_info).toString())){
               
                ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.conexion_internet_info).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                
            }
            
            if(retorno.equals(getText(R.string.no_encontrado_lugar_mapa_aux).toString())){
                
                ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.no_encontrado_lugar_mapa_aux).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                
            }
            
	    }
	    

		//Uri de la tabla a consultar
	    Uri uri = Uri.parse("content://com.proyecto.database.lugaresDB/lugares/");
	    
		//Obtenemos el cursor que nos permitira recorrer la lista de lugares
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		
		mapController.setZoom(1);
		
		//limpiamos puntos del mapa
		mapOverlays.clear();
		
		LugarMapaItemizedOverlay itemizedoverlay = new LugarMapaItemizedOverlay(this, drawable,mapa);
        mapOverlays.add(itemizedoverlay);
        
		if(resultadoBusqueda!=null && !resultadoBusqueda.equals("")){
            
            //zoom sobre el lugar
            mapController.setZoom(16);

            //Creamos una capa donde dibujaremos nuestras marcas
            Drawable drawable = this.getResources().getDrawable(R.drawable.lugar2);
        
            itemizedoverlay = new LugarMapaItemizedOverlay(this, drawable,mapa);
        
            if(latitudBusqueda!=null && longitudBusqueda!=null){
                
                itemizedoverlay.addLocalizacion(latitudBusqueda,longitudBusqueda,resultadoBusqueda);
        
                mapOverlays.add(itemizedoverlay);
        
                puntoBusqueda = new GeoPoint((int) (latitudBusqueda * 1E6),(int) (longitudBusqueda * 1E6));
                
            }
        }
		

		if(geoPoint!=null && buscar_mi_posicion){
		    
		    //zoom sobre el lugar
		    mapController.setZoom(16);
		    
		    Drawable drawable_my_location = getResources().getDrawable(R.drawable.my_location);
		    
		    itemizedoverlay = new LugarMapaItemizedOverlay(this, drawable_my_location,mapa);

		    itemizedoverlay.addLocalizacion(geoPoint,getText(R.string.posicion_actual).toString());
            
            mapOverlays.add(itemizedoverlay);
        }
		
		
		//si hay lugares a mostrar
		if (cursor!=null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			//Creamos una capa donde dibujaremos nuestras marcas
			 itemizedoverlay = new LugarMapaItemizedOverlay(this, drawable,mapa);
			
			//Anyadimos marcas para todos los puntos almacenados
			do {
				latitud = cursor.getDouble(cursor.getColumnIndex(LugaresDB.Lugares.LATITUD));
				longitud = cursor.getDouble(cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD));
				nombre = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE));
				descripcion = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
				id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
				rating = new Float(cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.RATING))); 
				idCategoria = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA));
			
				//Construimos objeto lugar con los datos obtenidos
				LugarEntity lugar=new LugarEntity(id, nombre,descripcion,latitud,longitud);
				
				//añadimos lugar al array
				if(lugar!=null){
				    lugares.add(lugar);
				}

		        itemizedoverlay.addLocalizacion(latitud,longitud, nombre,descripcion,id.toString()+"-"+idCategoria.toString(),rating);
		      
				
			} while (cursor.moveToNext());
			
			mapOverlays.add(itemizedoverlay);
			

		}
		
		//si no hay lugares registrados pongo la capa para poder añadir
		if (mapOverlays.size() == 0) {
            itemizedoverlay = new LugarMapaItemizedOverlay(this, drawable,mapa);
            mapOverlays.add(itemizedoverlay);
            Toast.makeText(getApplicationContext(),getText(R.string.crear_lugar).toString(),Toast.LENGTH_LONG).show();
        }
		
		//Si hay un cursor abierto, se cierra
        if (cursor != null) {
            cursor.close();
        }
        
        //Recorremos los puntos del mapa
		recorrerPuntosMapa();
		

	}

	/**
     * Se recorren los puntos almacenados del mapa a partir de la lista de lugares almacenada
    */
    protected void recorrerPuntosMapa() {

        //posicion actual
        if(geoPoint!=null && buscar_mi_posicion){
            mapController.setZoom(16);
            
            mapController.animateTo(geoPoint);

            String direccion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(geoPoint);
            
            String etiqueta="";
            
            etiqueta += "\n" + context.getText(R.string.latitud).toString()+":"+geoPoint.getLatitudeE6() / 1E6;
            
            etiqueta += "\n" + context.getText(R.string.longitud).toString()+":"+geoPoint.getLongitudeE6() / 1E6;
            
            if(!direccion.equals(""))
                etiqueta += "\n" + direccion;
            
            Toast.makeText(getApplicationContext(),getText(R.string.posicion_actual) + "\n"+ etiqueta,Toast.LENGTH_LONG).show();
            
        }
        
        if(geoPoint==null && buscar_mi_posicion){
            
            Toast.makeText(getApplicationContext(),getText(R.string.no_posicion_gps).toString(),Toast.LENGTH_LONG).show();

            mapController.setZoom(1);
        }
        
        //poscion busqueda
        if((puntoBusqueda!=null) && buscar_lugar){
            mapController.animateTo(puntoBusqueda);
            
            Toast.makeText(getApplicationContext(),resultadoBusqueda.toString(),Toast.LENGTH_LONG).show();
            
        }
        
     
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		cargarLugares();
	}
	
	/**
     * Permite obtener la posicion actual mediante GPS/Red y pintarla en el mapa
    */
    public void obtenerPosicion() {
 
        geoPoint=null;
        buscar_lugar=false;
        buscar_mi_posicion=true;
        
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW); //bajo consumo de bateria
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //precision
        String providerFine = locManager.getBestProvider(criteria, true);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String providerCoarse = locManager.getBestProvider(criteria, true);
        
        String providerGPS=LocationManager.GPS_PROVIDER;
        String providerNETWORK=LocationManager.NETWORK_PROVIDER;
                
        //realizar peticiones del servicio de localizacion con el proveedor obtenido
        if(providerFine != null)
        {
            locManager.requestLocationUpdates(providerFine, 5*60000, 100, lugaresListener); //update each 5 minutes
        }
        
        if(providerCoarse != null)
        {
            locManager.requestLocationUpdates(providerCoarse, 5*60000, 100, lugaresListener);//update each 5 minutes
        }
        
        

        if((providerFine == null) && (providerCoarse == null) && (providerGPS == null) && (providerNETWORK == null))
        {
            geoPoint=null;
        }
        
        else
        {
            bestProvider= providerFine;
            
            //Obtener la ultima posición conocida a partir del provider
            geoPoint=this.getLastKnownGeoPoint(bestProvider);
            
            if(geoPoint==null){
           
                bestProvider= providerCoarse;

                //Obtener la ultima posición conocida a partir del provider
                geoPoint=this.getLastKnownGeoPoint(bestProvider);
                
                if(geoPoint==null){
                    
                    if(providerGPS!=null && !providerGPS.equals("")){
                        
                        bestProvider= providerGPS;
               
                        //Obtener la ultima posición conocida a partir del provider
                        geoPoint=this.getLastKnownGeoPoint(bestProvider);
                    }
                   
                    if(providerNETWORK!=null && !providerNETWORK.equals("") && geoPoint==null){
                        
                        bestProvider= providerNETWORK;
                      
                        //Obtener la ultima posición conocida a partir del provider
                        geoPoint=this.getLastKnownGeoPoint(bestProvider);
                        
                    }
                    
                }
            }
        
        }

        if(geoPoint!=null){
            locManager.removeUpdates(lugaresListener); //detener actualizaciones si se tiene la posicion actual
        }

        
    }
      
    /**
     * Comportamiento de la tecla ATRAS del terminal
     * @keyCode int Codigo de la tecla pulsada
     * @event KeyEvent Evento que ha provocado la llamada al metodo
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_S) {  
            mapa.setSatellite(!mapa.isSatellite());  
            return true;
            
        } else if (keyCode == KeyEvent.KEYCODE_Z) {  
            mapa.displayZoomControls(true);  
            return true;  
        } 
        
        else if (keyCode == KeyEvent.KEYCODE_I) {
            mapa.getController().setZoom(mapa.getZoomLevel() + 1);
            return true;
            
        } else if (keyCode == KeyEvent.KEYCODE_O) {
            mapa.getController().setZoom(mapa.getZoomLevel() - 1);
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * Se muestran las opciones de menú de la pantalla mapa
     * @param menu Objeto menu 
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mapa, menu);
        return true;
    }
    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * @param item MenuItem 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar sobre "Posicion" 
            case R.id.posicion:
                
                //Ventana que se muestra de forma asíncrona mientras se realiza la busqueda
                MyLocationLoading searchLoading = new MyLocationLoading(context);
                searchLoading.execute();

                return true;
                
            case R.id.gps_sistema:
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                return true;
            // Al pulsar sobre "ve mapa completo"
            case R.id.verMapaCompleto:
                
                mapController.setZoom(1);

                
                return true;
                
            case R.id.reconocimientoVoz:
                
                //comprobar si está instalado el reconocimiento de voz
                if(hasVoicerec()){
                    
                    final Intent intentSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intentSpeech.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);        
                    intentSpeech.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    intentSpeech.putExtra(RecognizerIntent.EXTRA_PROMPT, getText(R.string.buscar_voz_texto).toString()); 
                    startActivityForResult(intentSpeech, Constants.ACTIVIDAD_RECONOCIMIENTO_VOZ);
                    
                }else{
                    
                    dialogo = onCreateDialog(Constants.DIALOGO_VOICE);
                    dialogo.show();
                    //ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.voice_not_installed).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                    
                }
                
                return true;    
            // Al pulsar sobre "Buscar lugar"
            case R.id.buscarLugar:
                
                //compborar conexion a internet
                if (!NetWorkStatus.getInstance(context).isOnline(context)) {
                    
                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.conexion_internet_info).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                    
                }else{

                    if(lugar!=null && lugar.getText()!=null && !lugar.getText().toString().equals("")){
                        
                        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                        SearchLoading searchLoading2 = new SearchLoading(context,lugar.getText().toString());
                        searchLoading2.execute();
                        
                    }else{
                        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                        AsyncLoading asyncLoading2 = new AsyncLoading(context);
                        asyncLoading2.execute();
                    
                        Intent intentBuscar = new Intent();
                        intentBuscar.setClass(getApplicationContext(), BuscarLugarActivity.class);
                        startActivityForResult(intentBuscar, Constants.ACTIVIDAD_BUSQUEDA);
                    }
                }
                return true;
            //Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Obtiene una instantcia del mapa
     * @return MapView 
    */
    public MapView getVistaMapa() {
        return mapa;
    }
    
    
    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
    
    /**
     * Obtiene la útlima posicion conocida por el GPS o el best Provider
     * 
     * @param bestProvider
     *            String
     * @return GeoPoint           
     */
    public GeoPoint getLastKnownGeoPoint(String bestProvider){
        
        GeoPoint geoPoint=null;
        
        if(locManager!=null){
            Location localizacion = locManager.getLastKnownLocation(bestProvider);
        
            if(localizacion != null)
            {

                geoPoint = new GeoPoint((int) (localizacion.getLatitude() * 1E6),(int) (localizacion.getLongitude() * 1E6));
            
            }
        }
        
        return geoPoint;
        
    }
    
    /**
     * Metodo que gestiona las respuestas de las actividades
    */
     @Override 
     public void onActivityResult(int requestCode, int resultCode, Intent data) {     
       super.onActivityResult(requestCode, resultCode, data); 

       //Comprobamos que Actitiy es la que nos responde
       switch(requestCode) { 
         case Constants.ACTIVIDAD_BUSQUEDA: 
             
             //Ralizamos la busqueda
             if (resultCode == Activity.RESULT_OK) {
                 
                 Bundle extras = data.getExtras();
                 
                 if(extras != null) {
                     
                     buscar_lugar=true;
                     
                     busqueda = extras.getString("busqueda");
                     
                     if(busqueda!=null){
                         
                         lugar.setText(busqueda);
                         
                         //Ventana que se muestra de forma asíncrona mientras se realiza la busqueda
                         SearchLoading searchLoading = new SearchLoading(context,busqueda);
                         searchLoading.execute();

                     }

                 }
             
             }
             break;
         case Constants.ACTIVIDAD_RECONOCIMIENTO_VOZ:
             
             if (resultCode == Activity.RESULT_OK) {
                 ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                 if(matches!=null && matches.size()>0){
                     String cadena="";
                     for(int i=0;i<matches.size();i++){
                        
                         cadena+=matches.get(i).toString();
                     }
                     
                     if(cadena!=null && !cadena.equals("")){
                         lugar.setText(cadena);
                         //Ventana que se muestra de forma asíncrona mientras se realiza la busqueda
                         SearchLoading searchLoading = new SearchLoading(context,cadena.toString());
                         searchLoading.execute();
                     }
                 }
             }
             break;
       }
       
     }
     
     private void mensajeEstadoRed(){
         
         String mensaje_estado_red="";
         
         
         if (locManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
             
             mensaje_estado_red=getText(R.string.gps_habilitado).toString();
             
         }
         else
         {
    
             mensaje_estado_red=getText(R.string.gps_deshabilitado).toString();
         }
         
         if (locManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
             
             mensaje_estado_red+="\n" + getText(R.string.red_habilitada).toString();
         }
         else
         {
             mensaje_estado_red+="\n" +getText(R.string.red_deshabilitada).toString();
             
         }
         
         //comprobar conexion a internet
         if (NetWorkStatus.getInstance(context).isOnline(context)) {
             
             mensaje_estado_red+="\n" + getText(R.string.conexion_internet_ok).toString();
             
         }else{
             
             mensaje_estado_red+="\n" + getText(R.string.conexion_internet_error).toString();
         }
         
         ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(mensaje_estado_red.toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
         
         
     }
     
     /**
      * Busqueda de un punto en el mapa a partir del nombre
      * @param busqueda
      *            String
      */
     protected String buscarLugarMapa(String busqueda) {

         //inicializar variables para guardar la busqueda
         resultadoBusqueda="";
         latitudBusqueda=null;
         longitudBusqueda=null;
         puntoBusqueda=null;

         buscar_lugar=true;
         buscar_mi_posicion=false;
         
         boolean red_disponible = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
         
         boolean redOnline=false;
         
         if (NetWorkStatus.getInstance(context).isOnline(context)) {
             
             redOnline=true;
         }

         //si hay red y esta Online se intenta obtener la direccion del lugar a partir de latitud y longitud
         
         if(red_disponible && redOnline){
             
             Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
         
             List<Address> addresses;
             try {
                 addresses = geoCoder.getFromLocationName(busqueda, 1);
             
                 if (addresses!=null && addresses.size() > 0) {
                     for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();i++){

                         if(addresses.get(0).getLocality()!=null && !addresses.get(0).getLocality().equals("")){
                             resultadoBusqueda += getText(R.string.ciudad)+": "+ addresses.get(0).getLocality() + "\n";
                         }
                         if(addresses.get(0).getCountryName()!=null && !addresses.get(0).getCountryName().equals("")){
                             resultadoBusqueda += getText(R.string.pais)+": "+ addresses.get(0).getCountryName() + " ";
                         }
                         
                         if(addresses.get(0).getCountryCode()!=null && !addresses.get(0).getCountryCode().equals("")){
                             resultadoBusqueda += addresses.get(0).getCountryCode() + "\n";
                         }
                         
                         if(addresses.get(0).getPostalCode()!=null && !addresses.get(0).getPostalCode().equals("")){
                             resultadoBusqueda += getText(R.string.codigo_postal)+": "+ addresses.get(0).getPostalCode() + "\n";
                          }
                         
                         if(addresses.get(0).getPhone()!=null && !addresses.get(0).getPhone().equals("")){
                             resultadoBusqueda += getText(R.string.telefono)+": "+ addresses.get(0).getPhone() + "\n";
                         }
                         
                         if(addresses.get(0).getAdminArea()!=null && !addresses.get(0).getAdminArea().equals("")){
                             resultadoBusqueda += getText(R.string.area) +": "+ addresses.get(0).getAdminArea() + " ";
                         }
                         
                         if(addresses.get(0).getSubAdminArea()!=null && !addresses.get(0).getSubAdminArea().equals("")){
                             resultadoBusqueda += addresses.get(0).getSubAdminArea() + "\n";
                         }
                         
                         if(new Double(addresses.get(0).getLatitude())!=null){
                             latitudBusqueda=new Double(addresses.get(0).getLatitude());
                             resultadoBusqueda += getText(R.string.latitud)+":  "+ new Double(addresses.get(0).getLatitude()).toString() + "\n";
                         }
                         
                         if(new Double(addresses.get(0).getLongitude())!=null){
                             longitudBusqueda=new Double(addresses.get(0).getLongitude());
                             resultadoBusqueda += getText(R.string.longitud)+ ":  "+new Double(addresses.get(0).getLongitude()).toString() + "\n";
                         }
                         
                     }
                 }else{
                     retorno=getText(R.string.no_encontrado_lugar_mapa).toString();
      
                 }

                 if(resultadoBusqueda!=null && !resultadoBusqueda.equals("")){
                     retorno=getText(R.string.encontrado_lugar_mapa).toString();
                 }
                 
                 
             } catch (IOException e) {
                 e.printStackTrace();
                 retorno=getText(R.string.no_encontrado_lugar_mapa_aux).toString();

             }
         }else{
             
             retorno=getText(R.string.conexion_internet_info).toString();
             
         }
         
         return retorno;
     }
     
     private void initControls(){

         //recuperamos la vista del mapa
         mapa = (MapView) this.findViewById(R.id.mapa);
         
         lugar = (EditText) findViewById(R.id.texto_lugar);
         
         toggle = (Button) findViewById(R.id.idToggleButtonAux);
         
         myPosicion = (Button) findViewById(R.id.idBotonMiPosicion);
         
         buscar = (Button) findViewById(R.id.botonBuscarMapa);
     }
     
     @Override
     public boolean dispatchKeyEvent(KeyEvent event) {
             return super.dispatchKeyEvent(event);
     }
     
     
     /**
      * Clase que permite realizar la busqueda de forma asíncrona
      */
     public class SearchLoading extends AsyncTask<String, Void, Boolean> {

         private ProgressDialog dialog;
         private Context        ctx;
         private String        search="";

         /**
          * Constructor a partir del contexto
          * 
          * @param context
          *            Contexto de la actividad
          */
         public SearchLoading(Context ctx,String search) {
             this.ctx = ctx;
             dialog = new ProgressDialog(ctx);
             this.search= search;
         }

         @Override
         protected Boolean doInBackground(String... strings) {
             
             buscar_lugar=true;

             retorno=buscarLugarMapa(lugar.getText().toString());

             return buscar_lugar;
             
         }

         /**
          * Muestra ventana de dialogo
          * 
          */
         protected void onPreExecute() {
             this.dialog = ProgressDialog.show(ctx, ctx.getText(R.string.buscar), ctx.getText(R.string.buscando)+" "+search, true, false);
         }

         /**
          * Elimna ventana de dialogo cuando termine el proceso
          * 
          * @param success
          *            Indica si ha finalizado el proceso
          * 
          */
         @Override
         protected void onPostExecute(final Boolean success) {
             
             if (dialog.isShowing()) {
                 dialog.dismiss();
             }
             
             cargarLugares();
             
         }

     }
     
     /**
      * Clase que permite realizar la busqueda de forma asíncrona
      */
     public class MyLocationLoading extends AsyncTask<String, Void, Boolean> {

         private ProgressDialog dialog;
         private Context        ctx;

         /**
          * Constructor a partir del contexto
          * 
          * @param context
          *            Contexto de la actividad
          */
         public MyLocationLoading(Context ctx) {
             this.ctx = ctx;
             dialog = new ProgressDialog(ctx);
         }

         @Override
         protected Boolean doInBackground(String... strings) {
             
             buscar_mi_posicion=true;

             (new Thread(new Runnable() {
                 
                 @Override
                 public void run() {
                     Looper.prepare();
                     obtenerPosicion();

                 }
             })).start();
             

             return buscar_mi_posicion;
             
         }

         /**
          * Muestra ventana de dialogo
          * 
          */
         protected void onPreExecute() {
             this.dialog = ProgressDialog.show(ctx, ctx.getText(R.string.buscar), ctx.getText(R.string.buscando_posicion_actual), true, false);
         }

         /**
          * Elimna ventana de dialogo cuando termine el proceso
          * 
          * @param success
          *            Indica si ha finalizado el proceso
          * 
          */
         @Override
         protected void onPostExecute(final Boolean success) {
             
             if (dialog.isShowing()) {
                 dialog.dismiss();
             }
             
             cargarLugares();
             
         }

     }
     
     public boolean hasVoicerec() {
         final PackageManager pm = getPackageManager();
         final List<ResolveInfo> activities =
         pm.queryIntentActivities(
         new Intent(
         RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
         return (activities.size() != 0);
     }
     
     @Override
     protected Dialog onCreateDialog(int id) {
         Dialog newDialog = null;
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         Animation animShow = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
         final Animation animHide = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

         switch (id) {
             case Constants.DIALOGO_VOICE:
                 Context mContext = getApplicationContext();
                 LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                 final View layout = inflater.inflate(R.layout.voice, (ViewGroup) findViewById(R.id.layout_root));
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
     
}
