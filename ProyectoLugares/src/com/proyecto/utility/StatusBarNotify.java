package com.proyecto.utility;

import com.proyecto.activity.R;
import com.proyecto.activity.TabLugaresActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StatusBarNotify{

    private static StatusBarNotify instance  = new StatusBarNotify();
    static Context               context;

    //barra de nofiticacion
    private static String ns;
    private static NotificationManager notificationManager;
    
    public static StatusBarNotify getInstance(Context ctx) {
        context = ctx;

        //barra de notificacion
        ns = Context.NOTIFICATION_SERVICE;
        notificationManager =(NotificationManager) context.getSystemService(ns);
        
        return instance;
    }


    /**
    * Muestra mensaje en la barra de notificacion indicando la accion realizada por el usuario
    * @param opcion String indica el tipo de accion realizada por el usuario
    * @param nombreLugar String Nombre del lugar a mostrar en la notificacion
    * @param nombreLugar descripcion Descripcion del lugar a mostrar en la notificacion
    */
   public void statusBarNotify(String opcion,String nombreLugar,String descripcion) {
       
   // Definimos el aviso
    int icono = R.drawable.bolamundo;
    CharSequence aviso = "";
    CharSequence titulo = "";
    long hora = System.currentTimeMillis();
   
    
    if(opcion!=null && opcion.equals("nueva_categoria")){
        aviso  =  context.getText(R.string.registro_categoria);
        titulo =  context.getText(R.string.registro_categoria_text)+" "+nombreLugar;
    }
    
    if(opcion!=null && opcion.equals("update_categoria")){
        aviso  =  context.getText(R.string.actualiza_categoria);
        titulo =  context.getText(R.string.actualiza_categoria_text)+" "+nombreLugar;
    }
    
    if(opcion!=null && opcion.equals("guardar")){
        aviso  =  context.getText(R.string.registro_lugar_favorito);
        titulo =  context.getText(R.string.registro_lugar_text)+" "+nombreLugar;
    }
 
    if(opcion!=null && opcion.equals("actualizar")){
        aviso  = context.getText(R.string.actualiza_lugar_favorito);
        titulo = context.getText(R.string.actualiza_lugar_text)+" "+nombreLugar;
    }
 
    if(opcion!=null && opcion.equals("eliminar")){
         aviso  =  context.getText(R.string.elimina_lugar_favorito);
         titulo =  context.getText(R.string.elimina_lugar_text)+" "+nombreLugar;
    }
   
    Notification notification = new Notification(icono, aviso, hora);
    
   // Definimos los detalles del aviso
    CharSequence texto = descripcion;

    Intent notificationIntent = new Intent(context,TabLugaresActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);
    notification.setLatestEventInfo(context, titulo, texto,contentIntent);
    
   //AutoCancel: cuando se pulsa la notificaión ésta desaparece
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
     
    //Añadir sonido, vibración y luces
    //notification.defaults |= Notification.DEFAULT_SOUND;
    //notification.defaults |= Notification.DEFAULT_VIBRATE;
    //notification.defaults |= Notification.DEFAULT_LIGHTS;
    
   // Pasamos el aviso al manager
    final int LUGARNOTIFICACION_ID = 1;
    notificationManager.notify(LUGARNOTIFICACION_ID, notification);
    
    Toast.makeText(context,context.getText(R.string.nueva_notificacion).toString(),Toast.LENGTH_LONG).show();
    
   }

}
