package com.proyecto.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Actividad que muestra las imágenes de los lugares que están registrados como favoritos
 */

public class SplashScreenActivity extends Activity 
{
    private int sleepTime = 3000;
    
    protected void onCreate(Bundle savedInstanceState) {
        
        
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.inicio);
        
        Thread splashThread = new Thread() {
        
            @Override
            public void run() {
            
                try {
                    Thread.sleep(sleepTime);
                    handler_Splash.sendEmptyMessage(0);
                    
                } catch (InterruptedException e) {                  
                    e.printStackTrace();
                }
            }
        };
        
        splashThread.start();       
        
    }
    
    /* Handler to show the new friend position */
    private Handler handler_Splash = new Handler() {
            @Override
            public void handleMessage(Message msg) 
            {
                myFinish();
            }
    };
    
    private void myFinish()
    {
        
        Intent i = new Intent(this,PrincipalActivityAux.class); 
        startActivity(i);
        this.finish();
        
    }
        
}

