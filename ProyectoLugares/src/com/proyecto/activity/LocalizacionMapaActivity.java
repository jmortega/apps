package com.proyecto.activity;


import java.util.List;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.proyecto.map.LugarMapaItemizedOverlay;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.ContentViewFlipper;
import com.proyecto.utility.LocationGeoCoder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad principal que muestra la localizacion de un punto en el mapa y permite modificar dicho punto
 */
public class LocalizacionMapaActivity extends MapActivity {
   
    //Vista que muestra el mapa
    private MapView mapa;
    
    //Controlador para actuar sobre la vista
    private MapController mapController;
    
    //TextView que mostraran las coordenadas
    private TextView longitudText;
    private TextView latitudText;
    
    private Button toggle;
    
    //Variables donde almacenaremos las coordenadas
    private Double longitud;
    private Double latitud;
    
    private String descripcion="";
    
    //Lista de capas sobre el mapa
    private List<Overlay> mapOverlays;
    
    //Capa personalizada
    private LugarMapaItemizedOverlay itemizedoverlay;
    
    //Marca que utilizaremos para indicar el lugar seleccionado
    private Drawable marker;
    
    private LocationManager locManager;
    
    private boolean                isPinch;
    private boolean                isMoving     = false;
    private float                  x;
    private float                  y;
    
    //Contexto
    private Context context = this;

    private ContentViewFlipper MyViewFlipper;
    
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
        setContentView(R.layout.mapa_localizacion);
        
        initControls();
        
        //Animation
        Animation animationFlipIn  = AnimationUtils.loadAnimation(this, R.anim.flipin);
        Animation animationFlipOut = AnimationUtils.loadAnimation(this, R.anim.flipout);
        
        MyViewFlipper.setAnimation(animationFlipIn);
        MyViewFlipper.setInAnimation(animationFlipIn);
        MyViewFlipper.setOutAnimation(animationFlipOut);
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        //Establecemos las propiedades del mapa
        mapa.setBuiltInZoomControls(true);//Activa controles zoom
        mapa.setSatellite(false);         //Desactiva vista satélite
        mapa.setStreetView(false);        //Desactiva StreetView
        mapa.setTraffic(false);           //Desactiva información de tráfico
        mapa.setClickable(true);           
        
        //Obtenemos el controlador del mapa
        mapController = mapa.getController();
        
        //Establecemos zoom
        mapController.setZoom(12);
        
        //Obtenemos la lista de capas sobre el mapa
        mapOverlays = mapa.getOverlays();
        
        //Cargamos el recurso que utilizaremos de marca
        marker = this.getResources().getDrawable(R.drawable.marker);
        
        this.locManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            latitud = extras.getDouble("latitud");
            longitud = extras.getDouble("longitud");
            
            //Creamos un objeto GeoPoint que es el que usa el Controlador
            GeoPoint localizacion = new GeoPoint((int) (latitud * 1E6),(int) (longitud * 1E6));
            
            //Movemos el mapa hasta la ultima posicion, fijamos el zoom y activamos la vista satelite
            mapController.setCenter(localizacion);
            
          //Obtener descripcion del lugar
            descripcion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(localizacion);
            
            String mensaje="";
            
            mensaje +=  context.getText(R.string.actual_localizacion).toString();
            
            mensaje += "\n\n"+ context.getText(R.string.latitud).toString()+":"+latitud;
        
            mensaje += "\n" + context.getText(R.string.longitud).toString()+":"+longitud;
        
            mensaje += "\n" + descripcion;
            
            //Dibujamos una marca en el punto seleccionado
            itemizedoverlay = new LugarMapaItemizedOverlay(this, marker,mapa);
            itemizedoverlay.addLocalizacion(localizacion, mensaje);
            
            latitudText.setText(latitud.toString());
            longitudText.setText(longitud.toString());

            //antes de añadir la capa limpiamos para no ir acumulando
            mapOverlays.clear();
            mapOverlays.add(itemizedoverlay);
        }
        
        //Establece vista satelite activa/desactiva
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
        
    }
    

    /**
     * Metodo que gestiona las pulsaciones en el mapa
     * @param motionEvent MotionEvent
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionType = motionEvent.getAction();
        int fingers = motionEvent.getPointerCount();
        
        switch (actionType) {
            case MotionEvent.ACTION_DOWN:
                isPinch = false;
                isMoving = false;
               
                x = motionEvent.getX();
                y = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isMoving = true;
                if (fingers == 2) {
                    isPinch = true; // Two fingers, def a pinch
                }
                float newX = motionEvent.getX();
                float newY = motionEvent.getY();
                if ((Math.abs(newX - x) > 5) || (Math.abs(newY - y) > 5)) isMoving = true;
                x = newX;
                y = newY;
                break;
            case MotionEvent.ACTION_UP:
                
                if ((!isPinch) || (!isMoving)) {

                    //Obtenemos las coordenadas del pixel seleccionado
                    Projection proj = mapa.getProjection();
                    GeoPoint gp = proj.fromPixels((int)motionEvent.getX(), (int)motionEvent.getY()); 
                
                    //Almacenamos las coordenadas y las mostramos por pantalla
                    latitud = new Double(gp.getLatitudeE6()/ 1E6);
                    longitud = new Double(gp.getLongitudeE6()/ 1E6);

                    latitudText.setText(latitud.toString());
                    longitudText.setText(longitud.toString());

                    //Dibujamos una marca en el punto seleccionado
                    itemizedoverlay = new LugarMapaItemizedOverlay(this, marker,mapa);
                
                    String mensaje="";
                
                    //Obtener descripcion del lugar
                    descripcion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(gp);
                
                    itemizedoverlay.addLocalizacion(gp, descripcion);
                
                    mensaje +=  context.getText(R.string.nueva_localizacion).toString();
                
                    mensaje += "\n\n"+ context.getText(R.string.latitud).toString()+":"+latitud;
                
                    mensaje += "\n" + context.getText(R.string.longitud).toString()+":"+longitud;
                
                    mensaje += "\n" + descripcion;
                

                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                
                    //antes de añadir la capa limpiamos para no ir acumulando
                    mapOverlays.clear();
                    
                    mapOverlays.add(itemizedoverlay);
                }
        }

        return super.dispatchTouchEvent(motionEvent);
    }
    
    
    /**
     * Se muestran las opciones de menú de la pantalla mostrar
     * @param menu Objeto menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_newlocation, menu);

        return true;
    }
    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * @param item MenuItem 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
            // Al pulsar sobre "Aceptar Localizacion"
            case R.id.btnAceptarLocalizacion:
                
                //Devolvemos las nuevas coordenadas a la pantalla de editar
                Intent intent= new Intent();
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("descripcion", descripcion);
                intent.setClass(getApplicationContext(),EditarLugarActivityAux.class);
                setResult(Activity.RESULT_OK, intent);
                
                finish();
                
                return true;
                
             // Al pulsar sobre "Volver" 
            case R.id.volver:
                finish();
                return true;
                
             // Al pulsar sobre "ve mapa completo"
            case R.id.verMapaCompleto:
                mapController.setZoom(1);
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
    public boolean onKeyDown(int keyCode, KeyEvent  event) {
        
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        
        return false;
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }
    
    private void initControls(){

        MyViewFlipper = (ContentViewFlipper)findViewById(R.id.viewflipper);
        
        mapa = (MapView) this.findViewById(R.id.mapa);
        
        toggle = (Button) findViewById(R.id.idToggleButton);
        
        //Recuperamos los TextView donde mostraremos las coordenadas del punto seleccionado
        latitudText = (TextView) this.findViewById(R.id.latitudText);
        
        longitudText = (TextView) this.findViewById(R.id.longitudText);
        
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }

    
}