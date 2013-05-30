package com.proyecto.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.libresoft.sdk.ARviewer.Types.ExtenalInfo;
import com.libresoft.sdk.ARviewer.Types.GenericLayer;
import com.libresoft.sdk.ARviewer.Types.GeoNode;
import com.libresoft.sdk.ARviewer.Types.GeoNodePositionComparator;
import com.libresoft.sdk.ARviewer.Types.Photo;
import com.libresoft.sdk.ARviewer.Types.User;
import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.entity.LugarEntity;
import com.proyecto.service.LocationServiceAux;
import com.proyecto.service.LocationUtilsAux;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Actividad que muestra una lista de los lugares almacenados en el sistema
 */
public class RealidadAumentadaActivity extends ListActivity {
	
    private static NodeAdapter mAdapter;
    private static ArrayList<GeoNode> mNodeList = new ArrayList<GeoNode>();
    private static Context mContext;
    
    private GenericLayer mylayer;

    //Array para almacenar los lugares que recogemos de la BD
    private ArrayList<com.proyecto.entity.LugarEntity> lugares = new ArrayList<com.proyecto.entity.LugarEntity>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Start location service */        
        startService(new Intent(this,LocationServiceAux.class));                          
        
        /* Init context and LibreGeoSocial */
        mContext = getBaseContext();
   
        mAdapter = new NodeAdapter(this);
        setListAdapter(mAdapter);  
        
        mylayer = new GenericLayer(0, "", "My Layer", "Description", null, null, null, null, null, null);
        
        new getDataTask().execute();
        
    }
     
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(this,LocationServiceAux.class));
        
    }
    
    private class getDataTask extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pd;
        
         protected void onPreExecute() 
         {
             pd = ProgressDialog.show(RealidadAumentadaActivity.this,mContext.getText(R.string.cargando), mContext.getText(R.string.cargando_datos), true);         
         }
         
        @Override
        protected Void doInBackground(Void... params) {
            
            mNodeList.clear();
            
            
            final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.NOMBRE, LugaresDB.Lugares.DESCRIPCION,LugaresDB.Lugares.LATITUD,LugaresDB.Lugares.LONGITUD,LugaresDB.Lugares.FOTO,LugaresDB.Lugares.RATING,LugaresDB.Lugares.CATEGORIA};
            
            //Uri de la tabla a consultar
            Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;

            lugares=new ArrayList<LugarEntity>();
            
            Cursor cursor = getContentResolver().query(uri,columnas, null, null, LugaresDB.Lugares.CATEGORIA_SORT_ORDER);

            //colocamos el cursor en el primer elemento
            cursor.moveToFirst();

            ArrayList<GeoNode> collection = new ArrayList<GeoNode>();
            
            for (int i = 0; i < cursor.getCount(); i++) {
                String path_foto="";
                int id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
                String nombre = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE));
                String descripcion = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION));
                Double longitud = new Double(cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD)));
                Double latitud = new Double(cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.LATITUD)));
                if(cursor.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                    path_foto = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.FOTO));
                }
                com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(id,nombre,descripcion,latitud,longitud,path_foto);
                lugares.add(lugar);
                //avanzar el cursor al siguiente elemento para siguiente iteracion
                cursor.moveToNext();
            }
            
            //Si hay un cursor abierto, se cierra
            if (cursor!= null) {
                cursor.close();
            }
            
            for (int i=0; i<lugares.size(); i ++){
                
                User user=new User(null,"","","","","",0.0,0.0,0.0,0.0, 
                        null,"",new Integer(0),"",null, new Integer(0), "", "", null);
                
                Photo geoNode=new Photo((int) lugares.get(i).get_id(), lugares.get(i).getLatitud(), lugares.get(i).getLongitud(), new Double(0),  new Double(5),  lugares.get(i).getNombre(), lugares.get(i).getDescripcion(),lugares.get(i).getFoto(),"",user,"", new Double(0));
                geoNode.setFatherLayer(mylayer);
                geoNode.setId((int) lugares.get(i).get_id());
                geoNode.setLatitude(lugares.get(i).getLatitud());
                geoNode.setLongitude(lugares.get(i).getLongitud());
                geoNode.setUsername(lugares.get(i).getFoto());
                geoNode.setPhotoPath(lugares.get(i).getFoto());
                geoNode.setDescription(lugares.get(i).getDescripcion());
                geoNode.setName(lugares.get(i).getNombre());
                geoNode.setRadius(new Double(5));
                geoNode.setPhotoUrl(lugares.get(i).getFoto());
                
                //Obtener distancia entre localizacion actual y el punto de interes almacenado
                
                float distancia = LocationUtilsAux.calculateDistance(LocationServiceAux.getCurrentLocation(), geoNode.getLocation());
                String distance=String.valueOf(distancia); 
                geoNode.setPosition_since(distance);

                ExtenalInfo extInfo = new ExtenalInfo();
                extInfo.setPhotoThumbUrl(lugares.get(i).getFoto());
                
                if(geoNode.getLatitude()!=null && geoNode.getLongitude()!=null){
                    String url="http://www.panoramio.com/map/#lt="+geoNode.getLatitude().toString()+"&ln="+geoNode.getLongitude().toString()+"&z=1";
                    extInfo.setUrlInfo(url);
                }
                
                geoNode.setExternalInfo(extInfo);
                collection.add(geoNode);
                
               
                
            }

            mNodeList.addAll(collection);

            Collections.sort(mNodeList, new GeoNodePositionComparator());

            return null;
        }
        
        protected void onPostExecute(Void unused) {
            if (!isFinishing()) 
            {
                pd.dismiss();                           
                mAdapter.notifyDataSetChanged();

                mylayer.setNodes(mNodeList);
                
                try
                {       
                    Intent intent = new Intent("com.libresoft.apps.ARviewer.VIEWER");                
                    intent.putExtra("LAYER", mylayer);
                    intent.putExtra("LATITUDE", LocationServiceAux.getCurrentLocation().getLatitude());
                    intent.putExtra("LONGITUDE", LocationServiceAux.getCurrentLocation().getLongitude());
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:com.libresoft.apps.ARviewer"));
                    startActivity(i);
                }
                
                if(mNodeList.size()==0){
                    setContentView(R.layout.lista_lugares);
                }
                
                
                
            }
        }       
    }
    
    
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        
        GeoNode mynode = mNodeList.get(position);
        
        if ( mynode.getExternalInfo() != null)
        {
            String url = mynode.getExternalInfo().getUrlInfo();
        
            if (url!=null)
            {
                Intent i = new Intent(RealidadAumentadaActivity.this, PanoramioActivity.class);
                i.putExtra("url", url);
                startActivity(i);
            }
        }
        
    }
    
    protected static GeoNode getNodeList(int position){
        if((mNodeList != null) && (mNodeList.size() > position))
            return mNodeList.get(position);
        return null;
    }

    public static class NodeAdapter extends BaseAdapter {

        private Context mContext;

        public NodeAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mNodeList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Make up a new view
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layer_ra, null);
            } else {
                // Use convertView if it is available
                view = convertView;
            }
            

            GeoNode myResource = getNodeList(position);

            String titulo = null;
            String descripcion = null;
            String distancia = null;

            // Calculate distance
            String distanceString = "";
            
            if (myResource.getLatitude() == -1.0
                    && myResource.getLongitude() == -1.0) {
                distanceString = mContext.getText(R.string.desconocida).toString();
            } else if (LocationServiceAux.getCurrentLocation() == null)
                distanceString = mContext.getText(R.string.desconocida).toString();
            else {
                // Calculate distance
                float dist = LocationUtilsAux.calculateDistance(LocationServiceAux.getCurrentLocation(), myResource.getLocation());

                distanceString = LocationUtilsAux.displayDistance(dist,LocationUtilsAux.AUTO);
            }
        
            ImageView img = (ImageView) view.findViewById(R.id.image);
            File file = null;

            if (Photo.class.isInstance(myResource)) {
                Photo myphoto = (Photo) getNodeList(position);

                if (myphoto.getUsername()==null || "".equals(myphoto.getUsername())){
                    img.setImageResource(R.drawable.no_imagen);
                }else{

                file = new File(myphoto.getUsername());
                
                if(file.exists()){ //si existe la imagen
                    img.setImageURI(Uri.parse(myphoto.getUsername()));
                }else{
                    img.setImageResource(R.drawable.no_imagen);

                }
                
                }
                
                titulo = myphoto.getName();
                
                descripcion = myphoto.getDescription()+"\n";

                distancia =  mContext.getText(R.string.distancia).toString() +":"+distanceString ;
                
            }

            TextView t = (TextView) view.findViewById(R.id.title);
            t.setText(titulo);

            t = (TextView) view.findViewById(R.id.description);
            t.setText(descripcion);
            
            t = (TextView) view.findViewById(R.id.distance);
            t.setText(distancia);

            return view;

        }

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        menu.add(0, Constants.MENU_AR_VIEWER, 0, "AR Viewer").setIcon(R.drawable.arviewer);
        
        menu.add(0, Constants.MENU_INFO_VIEWER, 0, "Info").setIcon(R.drawable.info); 
        
        menu.add(0, Constants.MENU_ORDER_BY_ASC, 0, getText(R.string.ordenar_distancia_asc).toString()).setIcon(R.drawable.ordenar2);
        
        menu.add(0, Constants.MENU_ORDER_BY_DESC, 0,  getText(R.string.ordenar_distancia_desc).toString()).setIcon(R.drawable.ordenar); 
        
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            
            case Constants.MENU_AR_VIEWER:
                
                
                mylayer.setNodes(mNodeList);
                
                try
                {       
                    Intent intent = new Intent("com.libresoft.apps.ARviewer.VIEWER");                
                    intent.putExtra("LAYER", mylayer);
                    intent.putExtra("LATITUDE", LocationServiceAux.getCurrentLocation().getLatitude());
                    intent.putExtra("LONGITUDE", LocationServiceAux.getCurrentLocation().getLongitude());
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:com.libresoft.apps.ARviewer"));
                    startActivity(i);
                }
                
                
                
                break;
                
            case Constants.MENU_INFO_VIEWER:
                showDialog(Constants.DIALOGO_INFO);
                break;
                
            case Constants.MENU_ORDER_BY_ASC:

                shellSortOrderByASC(mNodeList);
                
                mAdapter.notifyDataSetChanged();

                mylayer.setNodes(mNodeList);
                
                break;
                
            case Constants.MENU_ORDER_BY_DESC:

                shellSortOrderByDESC(mNodeList);
                
                mAdapter.notifyDataSetChanged();

                mylayer.setNodes(mNodeList);
                
                break;
        }
        
        return true;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        
        switch (id) {
        
            case Constants.DIALOGO_INFO:
                
                LayoutInflater factory2 = LayoutInflater.from(this);
                final View textEntryView2 = factory2.inflate(R.layout.custom_dialog_aux, null);
                
                TextView text1 = (TextView) textEntryView2.findViewById (R.id.dialog_text);         
    
                text1.setText(getText(R.string.info_arviewer).toString());
    
                
                return new AlertDialog.Builder(this)            
                .setTitle("ARviewer")               
                .setView(textEntryView2) 
                .setIcon(R.drawable.icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {

                    }
                })
                .setNegativeButton(getText(R.string.obtener_arviewer).toString(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {
                                                    
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:com.libresoft.apps.ARviewer"));
                        startActivity(i);
                        
                    }
                })
                .create();
            }
    
        return null;
        
    }
    
    /**
     * mejora el ordenamiento por inserción comparando elementos separados
     * por un espacio de varias posiciones.
     * @param elementos
     *
     * @see Fileable
     */
    public void shellSortOrderByASC(ArrayList<GeoNode> elementos) {
        int i, j;
        boolean band;

        j = elementos.size() + 1;

        while (j > 0) {
            j = (int) (j / 2);
            band = true;

            while (band) {
                band = false;
                i = 0;
                while ((i + j) < elementos.size()) {
                    Float distancia=Float.valueOf(elementos.get(i).getPosition_since());
                    Float distanciaAux=Float.valueOf(elementos.get(i+j).getPosition_since());
                    if (distancia.compareTo(distanciaAux) > 0) {
                        change(elementos, i, i + j);
                        band = true;
                    }
                    i = i + 1;

                }
            }
        }
    }
    
    public void shellSortOrderByDESC(ArrayList<GeoNode> elementos) {
        int i, j;
        boolean band;

        j = elementos.size() + 1;

        while (j > 0) {
            j = (int) (j / 2);
            band = true;

            while (band) {
                band = false;
                i = 0;
                while ((i + j) < elementos.size()) {
                    Float distancia=Float.valueOf(elementos.get(i).getPosition_since());
                    Float distanciaAux=Float.valueOf(elementos.get(i+j).getPosition_since());
                    if (distancia.compareTo(distanciaAux) < 0) {
                        change(elementos, i, i + j);
                        band = true;
                    }
                    i = i + 1;

                }
            }
        }
    }
    
    /**
     * Cambia lo que tenemos en i, lo manda a j y viceversa.
     *
     * @param elementos
     * @param i
     * @param j
     *
     * @see ArrayList
     */
    private void change(ArrayList<GeoNode> elementos, int i, int j) {
        GeoNode aux1 = elementos.get(i);
        GeoNode aux2 = elementos.get(j);

        elementos.remove(i);
        elementos.add(i, aux2);

        elementos.remove(j);
        elementos.add(j, aux1);
    }
}
