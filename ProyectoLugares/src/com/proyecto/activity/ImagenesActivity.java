package com.proyecto.activity;

import java.util.ArrayList;

import com.proyecto.database.LugaresDB;
import com.proyecto.entity.LugarEntity;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * Actividad que muestra las imágenes de los lugares que están registrados como favoritos
 */

public class ImagenesActivity extends Activity implements ViewFactory {

    //Array para almacenar los lugares que recogemos de la BD
    private ArrayList<com.proyecto.entity.LugarEntity> lugares = new ArrayList<com.proyecto.entity.LugarEntity>();
    
    private ImageSwitcher iSwitcher;

    private SlidingDrawer mySlidingDrawer;
    
    private Button myButton;
    
    private Gallery gallery;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeria_imagenes);

        //inicializar controles
        initControls();
        
        //Campos a recuperar en la consulta
        final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.FOTO};
        
        //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;
        
        Cursor cursor = getContentResolver().query(uri,columnas, null, null, null);

        //colocamos el cursor en el primer elemento
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            String path_foto="";
            if(cursor.getColumnIndex(LugaresDB.Lugares.FOTO)>0){
                path_foto = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.FOTO));
                
                com.proyecto.entity.LugarEntity lugar = new com.proyecto.entity.LugarEntity(path_foto);
                
                if(path_foto!=null){
                    lugares.add(lugar);
                }
            }
            
            
            //avanzar el cursor al siguiente elemento para siguiente iteracion
            cursor.moveToNext();
        }
        
        //Si hay un cursor abierto, se cierra
        if (cursor!= null) {
            cursor.close();
        }
        
        iSwitcher.setFactory(this);
        iSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        iSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));


        gallery.setAdapter(new ImageAdapter(this,lugares));
        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                
                LugarEntity lugar = lugares.get(position);
 
                if (lugar.getFoto()!=null && !"".equals(lugar.getFoto()))
                    iSwitcher.setImageURI(Uri.parse(lugar.getFoto()));

            }
        });
        

        
        mySlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            
            
            @Override
            public void onDrawerOpened() {
                myButton.setText(getText(R.string.ocultar_imagenes).toString());
                
            }
        });
        
        mySlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            
            
            @Override
            public void onDrawerClosed() {
                myButton.setText(getText(R.string.mostrar_imagenes).toString());
                
            }
        });
        
    }

    public class ImageAdapter extends BaseAdapter {

        private Context ctx;
        private ArrayList<LugarEntity> lugares;
        int imageBackground;
        
        public ImageAdapter(Context c,ArrayList<LugarEntity> lugares) {
            ctx = c;
            
            TypedArray typArray = obtainStyledAttributes(R.styleable.GalleryTheme);
            imageBackground = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 1);
            typArray.recycle();
            
            this.lugares = lugares;
        }

        @Override
        public int getCount() {

            return lugares.size();
        }

        @Override
        public LugarEntity getItem(int index) {

            return lugares.get(index);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View arg1, ViewGroup arg2) {

            LugarEntity lugar = (LugarEntity) getItem(position);
            
            ImageView iView = new ImageView(ctx);
            
            if (lugar.getFoto()!=null && !"".equals(lugar.getFoto()))
                iView.setImageURI(Uri.parse(lugar.getFoto()));
            
            iView.setScaleType(ImageView.ScaleType.FIT_XY);
            iView.setLayoutParams(new Gallery.LayoutParams(150, 120));
            iView.setBackgroundResource(imageBackground);
            
            
            return iView;
        }

    }

    @Override
    public View makeView() {
        ImageView iView = new ImageView(this);
        iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        iView.setBackgroundColor(0xFF000000);
        return iView;
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
        menuInflater.inflate(R.menu.menu_imagenes, menu);
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
                
                finish();
                return true;
                
                    
                // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void initControls(){

        iSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher);
        
        gallery = (Gallery) findViewById(R.id.Galeria);
        
        mySlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        
        myButton = (Button) findViewById(R.id.handle);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
}
