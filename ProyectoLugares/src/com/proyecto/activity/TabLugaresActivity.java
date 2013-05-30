package com.proyecto.activity;

import android.app.TabActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TabHost;
/**
 * Actividad que muestra las pestanas para mostrar los lugares
 */
public class TabLugaresActivity extends TabActivity {
    
    //Modo en el que cargamos la activity
    private String modo="";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        
        Resources res = getResources();     
        TabHost tabHost = getTabHost();     
        TabHost.TabSpec spec;            
        Intent intent;

        intent = new Intent().setClass(this, ListaLugaresActivityAux.class);

        spec = tabHost.newTabSpec(getText(R.string.lista).toString()).
               setIndicator(getText(R.string.lista).toString(),res.getDrawable(R.drawable.lista)).setContent(intent);
        
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ListaCategoriasActivity.class);
        
        spec = tabHost.newTabSpec(getText(R.string.categorias).toString()).
                setIndicator(getText(R.string.categorias).toString(),res.getDrawable(R.drawable.category)).setContent(intent);

        tabHost.addTab(spec);

      //Uri de la tabla a consultar
        Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES;

        Cursor cursor = getContentResolver().query(uri,null, null, null, null);

        //colocamos el cursor en el primer elemento
        cursor.moveToFirst();

        if(cursor.getCount()>0){
            
        
        intent = new Intent().setClass(this, ComboLugaresActivity.class);
        
        spec = tabHost.newTabSpec(getText(R.string.combo).toString()).
                setIndicator(getText(R.string.combo).toString(),res.getDrawable(R.drawable.combo)).setContent(intent);

        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ImagenesActivity.class);
        
        spec = tabHost.newTabSpec(getText(R.string.galeriaImagenes).toString()).
                setIndicator(getText(R.string.galeriaImagenes).toString(),res.getDrawable(R.drawable.view_gallery)).setContent(intent);

        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, BuscadorMisLugaresActivity.class);
        
        spec = tabHost.newTabSpec(getText(R.string.buscar).toString()).
                setIndicator(getText(R.string.buscar).toString(),res.getDrawable(R.drawable.buscar2)).setContent(intent);

        tabHost.addTab(spec);
        
        }
        
        intent = new Intent().setClass(this, ExportImportActivity.class);
        
        spec = tabHost.newTabSpec(getText(R.string.exportar_importar).toString()).
                setIndicator(getText(R.string.exportar_importar).toString(),res.getDrawable(R.drawable.export)).setContent(intent);

        tabHost.addTab(spec);
        
      //Comprobamos en que modo debemos cargar la activity
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            modo = extras.getString("modo");
            
        }
        
        if (modo.equals("nuevaCategoria") || modo.equals("editarCategoria")|| modo.equals("categorias")) {
            tabHost.setCurrentTab(1);
        }else{
            tabHost.setCurrentTab(0);
        }
        
        
    }

}
