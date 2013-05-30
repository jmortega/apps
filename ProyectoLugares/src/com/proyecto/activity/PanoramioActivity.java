package com.proyecto.activity;

import com.proyecto.constants.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PanoramioActivity extends Activity 
{

    private String url="";
    
    protected void onCreate(Bundle savedInstanceState) {
        
        
        super.onCreate(savedInstanceState);
 
        //Recogemos la url que se nos proporcionar a traves del intent de llamada
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            url = extras.getString("url");
        }
        
        showDialog(Constants.DIALOGO_INFO);
    }
  
    @Override
    protected Dialog onCreateDialog(int id) {
        
        switch (id) {
        
            case Constants.DIALOGO_INFO:
                
                LayoutInflater factory2 = LayoutInflater.from(this);
                final View textEntryView2 = factory2.inflate(R.layout.custom_dialog_aux, null);
                
                TextView text1 = (TextView) textEntryView2.findViewById (R.id.dialog_text);         
                
                text1.setText(url);
    
                return new AlertDialog.Builder(this)            
                .setTitle(getText(R.string.fotos_panoramio).toString())               
                .setView(textEntryView2) 
                .setIcon(R.drawable.panoramio)
                .setPositiveButton(getText(R.string.ir_al_sitio).toString(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {
                        if(url!=null){
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        }
                        
                        finish();
                    }
                })
                .setNegativeButton(getText(R.string.cancel).toString(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {
                                                    
                        finish();
                        
                    }
                })
                .create();
            }
    
        return null;
        
    }
}

