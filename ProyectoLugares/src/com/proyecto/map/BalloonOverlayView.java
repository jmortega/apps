package com.proyecto.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.proyecto.activity.MostrarLugarActivityAux;
import com.proyecto.activity.R;

public class BalloonOverlayView extends FrameLayout {

    private LinearLayout layout;
    private TextView     title;
    private String       idLugar;
    private String       idCategoria;
    private ImageView bolaMundo;

    /**
     * Constructor por parámetros.Crea un nuevo BalloonOverlayView.
     *
     * @param context             - Contexto .
     * @param balloonBottomOffset - Distancia en pixels
     */
    public BalloonOverlayView(final Context context, int balloonBottomOffset) {

        super(context);

        setPadding(10, 5, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.balloon_map_overlay, layout);
        title = (TextView) v.findViewById(R.id.balloon_title);

        //Cerrar elemento
        ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                layout.setVisibility(GONE);
            }
        });
        
        //al pulsar sobre la imagen de la bola del mundo se abre un popup para ver los detalles del lugar
        
        bolaMundo = (ImageView) v.findViewById(R.id.ball_img_button);
        bolaMundo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
                if(idLugar!=null && !idLugar.equals("")){
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                
                    builder.setMessage(context.getText(R.string.ver_detalle_lugar).toString()+" "+title.getText().toString());
                
                    builder.setCancelable(true);
                
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int idAux) {

                            Intent intent = new Intent(context, MostrarLugarActivityAux.class);
                            
                            intent.putExtra("idLugar", new Long(idLugar));
                            
                            intent.putExtra("mostrarDesdeMapa", true);
                            
                            context.startActivity(intent);
                            
                        }
                    })
                    
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                            public void onClick(DialogInterface dialog, int idAux) {
                                dialog.cancel();
                            }
                        }).show();
                
            }
            }
        });

        
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;

        addView(layout, params);

    }

    /**
     * Establece los datos para un item en concreto
     *
     * @param item - El Overlay item que contiene la información
     */
    public void setData(OverlayItem item) {
        layout.setVisibility(VISIBLE);
        if (item.getTitle() != null) {
            title.setVisibility(VISIBLE);
            title.setText(item.getTitle());
        } else {
            title.setVisibility(GONE);
        }
        if (item.getSnippet() != null) {
            String []snippet=item.getSnippet().toString().split("-");
            
            if(snippet!=null && snippet.length==1){
                idLugar=snippet[0];
            }
            
            if(snippet!=null && snippet.length==2){
                idLugar=snippet[0];
                idCategoria=snippet[1];
                iconoCategoria(idCategoria);
            }
        }
    }
    
    private void iconoCategoria(String idCategoria){

        if(idCategoria!=null && idCategoria.equals("0")){
            bolaMundo.setImageResource(R.drawable.restaurant);
        }
        
        if(idCategoria!=null && idCategoria.equals("1")){
            bolaMundo.setImageResource(R.drawable.bar);
        }
        
        if(idCategoria!=null && idCategoria.equals("2")){
            bolaMundo.setImageResource(R.drawable.cafe);
        }
        
        if(idCategoria!=null && idCategoria.equals("3")){
            bolaMundo.setImageResource(R.drawable.compras);
        }
        
        if(idCategoria!=null && idCategoria.equals("4")){
            bolaMundo.setImageResource(R.drawable.hoteles);
        }
        
        if(idCategoria!=null && idCategoria.equals("5")){
            bolaMundo.setImageResource(R.drawable.musica);
        }
        
        if(idCategoria!=null && idCategoria.equals("6")){
            bolaMundo.setImageResource(R.drawable.parques);
        }
        
        if(idCategoria!=null && idCategoria.equals("7")){
            bolaMundo.setImageResource(R.drawable.aeropuertos);
        }
    }

}