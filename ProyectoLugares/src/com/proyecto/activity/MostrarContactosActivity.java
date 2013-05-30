package com.proyecto.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Actividad que muestra una lista con los contactos del telefono
 */
public class MostrarContactosActivity extends ListActivity {
    
        
    private Cursor mCursor;
    
    /** Called when the activity is first created. */
         @Override
          public void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.lista_contactos);
              
              String contactos="";
              
              //obtenemos datos para mostrar en el mail
              Bundle extras = getIntent().getExtras();
              if(extras != null) {
                  contactos=extras.getString("contactos");
              }
              
              if(contactos!=null && contactos.equals("sms")){
                  
                  // Consulta: contactos con teléfono ordenados por nombre
                      mCursor = getContentResolver().query(
                          Data.CONTENT_URI,
                          new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,
                                  Phone.TYPE },
                          Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
                                  + Phone.NUMBER + " IS NOT NULL", null,
                          Data.DISPLAY_NAME + " ASC");
                  
                  startManagingCursor(mCursor);
                  
                  // Lista de contactos
                  ListAdapter adapter = new SimpleCursorAdapter(this, // context
                          android.R.layout.simple_list_item_2, // Layout para las filas
                          mCursor, // cursor
                          new String[] { Data.DISPLAY_NAME, Phone.NUMBER },
                          new int[] { android.R.id.text1, android.R.id.text2 } 
                  );
                  
                  if(mCursor!=null && mCursor.getCount()>0){
                      setTitle(mCursor.getCount()+" "+getText(R.string.contactos_encontrados).toString()+"\n\n"+getText(R.string.seleccione_telefono).toString());
                      Toast.makeText(getApplicationContext(),getText(R.string.seleccione_telefono).toString(),Toast.LENGTH_LONG).show();
                  }
                  setListAdapter(adapter);
                  
                  
              }
              
              
              if(contactos!=null && contactos.equals("mail")){
                  
                  setTitle(getText(R.string.seleccione_email).toString());
                  
               // Consulta: contactos con email ordenados por nombre
                  mCursor = getContentResolver().query(
                          Data.CONTENT_URI,
                          new String[] { Data._ID, Data.DISPLAY_NAME, Email.DATA1,
                                  Phone.TYPE },
                          Data.MIMETYPE + "='" + Email.CONTENT_ITEM_TYPE + "' AND "
                                  + Email.DATA1 + " IS NOT NULL", null,
                          Data.DISPLAY_NAME + " ASC");
           
                 startManagingCursor(mCursor);
           
                  // Lista de contactos
                  ListAdapter adapter = new SimpleCursorAdapter(this, // context
                          android.R.layout.simple_list_item_2, // Layout para las filas
                          mCursor, // cursor
                          new String[] { Data.DISPLAY_NAME, Phone.NUMBER },
                          new int[] { android.R.id.text1, android.R.id.text2 } 
                  );
                  
                  
                  if(mCursor!=null && mCursor.getCount()>0){
                      setTitle(mCursor.getCount()+" "+getText(R.string.contactos_encontrados).toString()+"\n"+getText(R.string.seleccione_telefono).toString());

                      Toast.makeText(getApplicationContext(),getText(R.string.seleccione_email).toString(),Toast.LENGTH_LONG).show();
                  }
                  
                  setListAdapter(adapter);
                  
                
                  
              }

         }
      
          @Override
          protected void onListItemClick(ListView l, View v, int position, long id) {
              Intent result = new Intent();
      
              
              // Obtiene los datos de la posicion selecciona
              Cursor c = (Cursor) getListAdapter().getItem(position);
              int colIdx = c.getColumnIndex(Phone.NUMBER);
              String phone = c.getString(colIdx);
      
             // Guardamos el telefono en un extra para tenerlo disponible en la actividad llamante
              result.putExtra("telefono", phone);
              setResult(Activity.RESULT_OK, result);
       
              // Cierra la actividad y devuelve el control al llamador
              finish();
         }

}
