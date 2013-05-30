package com.proyecto.utility;

import com.proyecto.activity.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ToastPersonalizado{

    private static ToastPersonalizado instance  = new ToastPersonalizado();
    static Context               context;

    public static ToastPersonalizado getInstance(Context ctx) {
        context = ctx;
        return instance;
    }

    /**
     * Muestra un mensaje con el texto pasado por parametro
     * 
     * @param text
     *            String texto a mostrar
     */
    public void mostrarToastPersonalizado(String text,ViewGroup vistaRaiz,LayoutInflater inflater) {

        
        View layout = inflater.inflate(R.layout.toast, vistaRaiz);
        // Cambiamos el texto (buscamos el label y lo modificamos)
        TextView texto = (TextView) layout.findViewById(R.id.texto);
        texto.setText(text);
        // Creamos el toast y le asignamos la vista que hemos personalizado
        Toast toast = new Toast(context);
        toast.setView(layout);
        // Lo centramos en pantalla
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        // Permanecerá más rato en pantalla
        toast.setDuration(Toast.LENGTH_LONG);
        // Lo mostramos
        toast.show();

    }

}
