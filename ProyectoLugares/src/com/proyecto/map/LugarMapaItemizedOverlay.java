package com.proyecto.map;

import java.util.ArrayList;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.proyecto.activity.EditarLugarActivityAux;
import com.proyecto.activity.MapaLugaresActivityAux;
import com.proyecto.activity.R;
import com.proyecto.database.LugaresDB;
import com.proyecto.entity.CategoriaEntity;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.LocationGeoCoder;

/**
 * Clase que representa un lugar almacenado del mapa 
 */
public class LugarMapaItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> items;   
    private Context                context;
    private Drawable               marker;

    private boolean                isPinch;
    private boolean                isMoving     = false;
    private long                   startTime    = 0;
    private float                  x;
    private float                  y;
    private MapaLugaresActivityAux mapActivity;
    private GeoPoint               currentPoint = null;
    private LocationManager        locManager;
    
    //Array para almacenar las categorias que recogemos de la BD
    ArrayList<com.proyecto.entity.CategoriaEntity> category = new ArrayList<com.proyecto.entity.CategoriaEntity>();
    
    private String[] categorias;
    
    private Long[] idcategorias;
    
    /**
     * Constructor por parametros
     * 
     * @param context
     *            Contexto de la aplicacion
     * @param defaultMarker
     *            Icono por defecto para marcar los puntos en el mapa
     * @param mapView
     *            Instancia del mapa sobre el que pintar los puntos
     */
    public LugarMapaItemizedOverlay(Context context, Drawable defaultMarker,MapView mapView) {
        super(boundCenterBottom(defaultMarker),mapView);
        this.setContext(context);
        this.locManager=(LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        items= new ArrayList<OverlayItem>();
        setMarker(defaultMarker);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }

    /**
     * Añade un nuevo lugar al mapa
     * 
     * @param latitud double Latitud del lugar
     * @param longitud double Longitud del lugar
     * @param etiqueta String Etiqueta del lugar
     * @param id String Identificador del lugar
     * @param rating float Rating del lugar
     */

    public void addLocalizacion(double latitud, double longitud, String etiqueta,String descripcion, String id,float rating) {
        int lt = (int) (latitud * 1E6);
        int ln = (int) (longitud * 1E6);
        GeoPoint punto = new GeoPoint(lt, ln);

        String direccion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(punto);
        
        etiqueta += "\n\n" + context.getText(R.string.latitud).toString()+":"+latitud;
        
        etiqueta += "\n" + context.getText(R.string.longitud).toString()+":"+longitud;
        
        if(!direccion.equals("")){
            etiqueta += "\n" + direccion;
        }else{
            etiqueta += "\n" + descripcion;
        }

        if(punto!=null){
            OverlayItem item = new OverlayItem(punto, etiqueta, id);
            items.add(item);
            populate();
        }
    }


    /**
     * Añade un nuevo lugar al mapa
     * 
     * @param gp GeoPoint Objeto que contiene latitud y longitud
     * @param etiqueta String Etiqueta del lugar
     */
    public void addLocalizacion(GeoPoint gp, String etiqueta) {
        
        String direccion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(gp);

        etiqueta += "\n\n" + context.getText(R.string.latitud).toString()+":"+gp.getLatitudeE6() / 1E6;
        
        etiqueta += "\n" + context.getText(R.string.longitud).toString()+":"+gp.getLongitudeE6() / 1E6;
        
        if(!direccion.equals(""))
            etiqueta += "\n" + direccion;
        
        OverlayItem item = new OverlayItem(gp, etiqueta, null);
        
        if(item!=null){
            items.add(item);
            populate();
        }
    }
    
    /**
     * Añade un nuevo lugar al mapa
     * 
     * @param latitud double Latitud del lugar
     * @param longitud double Longitud del lugar
     * @param etiqueta String Etiqueta del lugar
     */
    public void addLocalizacion(double latitud, double longitud, String etiqueta) {
        
        int lt = (int) (latitud * 1E6);
        int ln = (int) (longitud * 1E6);
        GeoPoint punto = new GeoPoint(lt, ln);
        
        String direccion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(punto);
        
        etiqueta += "\n\n" + context.getText(R.string.latitud).toString()+":"+latitud;
        
        etiqueta += "\n" + context.getText(R.string.longitud).toString()+":"+longitud;
        
        if(!direccion.equals(""))
            etiqueta += "\n" + direccion;
        
        if(punto!=null){
            OverlayItem item = new OverlayItem(punto, etiqueta, null);
            if(item!=null){
                items.add(item);
                populate();
            }
        }
    }

    /**
     * Metodo que se ejecuta cuando el usuario hace click sobre un punto vacio del mapa
     * 
     * @param gp GeoPoint Objeto que contiene latitud y longitud
     * @param mapView MapView Objeto que contiene la vista del mapa
     * @return boolean
     */
    public boolean onTap(final GeoPoint punto, final MapView mapView) {

        boolean tapped = super.onTap(punto, mapView);

        // Comprobamos si es un lugar guardado
        if (!tapped) {
            
            if (!isPinch) {
                
                showLocationDialog(punto);
            }
        }
        return true;
    }

    /**
     * Metodo que permite gestionar los gestos con los dedos
     * 
     * @param motionEvent MotionEvent Objeto que contiene la informacion sobre el gesto
     * @param mapView MapView Objeto que contiene la vista del mapa
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent, MapView mapView) {

        int action = motionEvent.getAction();
        int fingers = motionEvent.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isPinch = false;
                startTime = motionEvent.getEventTime();
                isMoving = false;
                x = motionEvent.getX();
                y = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                long upTime = motionEvent.getEventTime();

                if ((!isMoving) && ((upTime - startTime) > 1000) && (motionEvent.getAction() == MotionEvent.ACTION_UP)) {
                    if (mapActivity != null) {
                        Projection p = mapActivity.getVistaMapa().getProjection();

                        currentPoint = p.fromPixels((int) motionEvent.getX(), (int) motionEvent.getY());

                        showLocationDialog(currentPoint);
                    }

                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingers == 2) {
                    isPinch = true; // Two fingers, def a pinch
                }
                float newX = motionEvent.getX();
                float newY = motionEvent.getY();
                if ((Math.abs(newX - x) > 5) || (Math.abs(newY - y) > 5)) isMoving = true;
                x = newX;
                y = newY;
                break;
            default:
                break;
        }

        return super.onTouchEvent(motionEvent, mapView);
    }

    /**
     * Metodo que se ejecuta cuando el usuario hace click sobre un punto vacio del mapa
     * lanzando la actividad que permite crear un nuevo lugar
     * 
     * @param gp GeoPoint Objeto que contiene latitud y longitud del lugar a crear
     */
    private void showLocationDialog(final GeoPoint gp) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.guardar_lugar);
        
        final Bundle extra = new Bundle();
        
        String mensaje="";
        
        //Ventana que se muestra de forma asíncrona mientras se cargan los datos
        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        if (gp != null) {

            double longitud = (double) (gp.getLongitudeE6() / 1E6);
            double latitud = (double) (gp.getLatitudeE6() / 1E6);

            mensaje += "\n" + context.getText(R.string.latitud).toString()+":"+latitud;
            
            mensaje += "\n" + context.getText(R.string.longitud).toString()+":"+longitud;
            
            extra.putDouble("longitud", longitud);
            extra.putDouble("latitud", latitud);

            // Obtener descripcion del lugar
            String descripcion= LocationGeoCoder.getInstance(context, locManager).obtenerLocationGeoCoder(gp);
            
            mensaje += "\n" + descripcion;
            
            extra.putString("descripcion", descripcion);
            extra.putString("editarMapa", "editarDesdeMapa");
        }
        
        dialog.setMessage(context.getText(R.string.confirmar_guardar_lugar).toString()+"\n"+mensaje);
        
        dialog.setPositiveButton(R.string.ok, new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                obtenerCategorias(extra);
                
            }
        });
        dialog.setNegativeButton(R.string.cancel, new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Limpia los puntos del mapa
     * 
    */
    public void clear() {
        items.clear();
        populate();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Drawable getMarker() {
        return marker;
    }

    public void setMarker(Drawable marker) {
        this.marker = marker;
    }
    
    public void obtenerCategorias(final Bundle extra){
    
      //Campos a recuperar en la consulta
        final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.CATEGORIA,LugaresDB.Lugares.DESCRIPCION};
        
        //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_CATEGORIAS;

        Cursor cursor = context.getContentResolver().query(uri,columnas,null, null, null);

        category=new ArrayList<CategoriaEntity>();
        
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
        
        //inicializar array con tantos lugares como hayamos encontrado
        categorias=new String[category.size()+1];
        idcategorias=new Long[category.size()+1];
        
        for(int i=0;i<categorias.length;i++){
            categorias[i]="";
            idcategorias[i]=new Long(0);
        }
        
        categorias[0]=context.getText(R.string.seleccione_categoria).toString();
        idcategorias[0]=new Long(0);
        
        for (int i = 0; i < category.size(); i++) {
            categorias[i+1]=category.get(i).getNombre();
            idcategorias[i+1]=category.get(i).get_id();
        }
        
        //Si hay un cursor abierto, se cierra
        if (cursor!= null) {
            cursor.close();
        }
        
        if(categorias!=null && categorias.length>0){
        
            AlertDialog.Builder builder =new AlertDialog.Builder(context);
            
                              builder.setTitle(context.getText(R.string.categorias).toString());
                               
                              final CharSequence[] choiceList =categorias;

                              int selected = 0; // select at 0

                              builder.setSingleChoiceItems(
                                      choiceList,
                                      selected,
                                      new DialogInterface.OnClickListener() {
                                   
                                  @Override
                                  public void onClick(
                                          DialogInterface dialog,
                                          int which) {
                                      
                                      if(which>0){
                                          
                                          //Ventana que se muestra de forma asíncrona mientras se cargan los datos
                                          AsyncLoading asyncLoading = new AsyncLoading(context);
                                          asyncLoading.execute();
                                      
                                          //Llamamos a EditarLugarActivity para crear un lugar nuevo
                                          Intent intent = new Intent(context, EditarLugarActivityAux.class);
                                          extra.putString("nombreCategoria", categorias[which]);
                                          extra.putLong("idCategoria", idcategorias[which]);
                                          extra.putString("modo", "crearLugar");
                                          intent.putExtras(extra);
                                          context.startActivity(intent);

                                          dialog.dismiss();
                                      }
                                      
                                 }
                             });
                              AlertDialog alert = builder.create();
                              alert.show();
        
        }
    
    }
}
