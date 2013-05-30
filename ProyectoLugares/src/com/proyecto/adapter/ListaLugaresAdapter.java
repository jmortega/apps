package com.proyecto.adapter;

import java.io.File;
import java.util.ArrayList;
import com.proyecto.activity.R;
import com.proyecto.entity.LugarEntity;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adaptador utilizado para mostrar la lista de lugares en una lista con un formato personalizado
 */
public class ListaLugaresAdapter extends ArrayAdapter<LugarEntity> {
    
    //lista para guardar los lugares
	private ArrayList<LugarEntity> lugares;
	
	private LayoutInflater mInflater;
	/**
	 * Constructor del adaptador
	 * @param context Context Contexto de la aplicacion
	 * @param lugares ArrayList Array con la lista de lugares
	 */
	public ListaLugaresAdapter(Context context,int textViewResourceId, ArrayList<LugarEntity> lugares) {
	    super(context, textViewResourceId, lugares);
		this.mInflater = LayoutInflater.from(context);
		this.lugares = lugares;
	}
	
	/**
	 * Devuelve la cantidad de lugares almacenados
	 */
	public int getCount() {
		return lugares.size();
	}
	
	/**
	 * Devuelve el item de tipo Lugar indicado como parametro
	 * @param index int Indice donde se encuentra el Lugar requerido en la lista
	 */
	public LugarEntity getItem(int index) {
		return lugares.get(index);
	}
	
	/**
	 * Devuelve el indice indicado como parametro
	 * @param position int Indice donde se encuentra el lugar rquerido en la lista
	 */
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Rellena la vista y la devuelve con los datos de un Lugar
	 * Se realizara tantas veces como Lugares existan hasta completar la vista con la lista
	 * @param position int Posicion en la lista de Lugares
	 * @param convertView View Vista que contiene los datos de un Lugar
	 * @param parent ViewGroup Grupo de vistas
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		File file = null;
		if (convertView == null) {
		    convertView = mInflater.inflate(R.layout.lugares_item, null);
			holder = new ViewHolder();
			holder.nombre = (TextView) convertView.findViewById(R.id.lugarNombre);
			holder.descripcion = (TextView) convertView.findViewById(R.id.lugarDescripcion);
			holder.foto = (ImageView) convertView.findViewById(R.id.imagenFoto);
			holder.iconoCategoria = (ImageView) convertView.findViewById(R.id.iconoCategoria);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LugarEntity lugar = getItem(position);
		if(lugar.getNombre()!=null){
		    holder.nombre.setText(lugar.getNombre());
		}
		
		if(lugar.getDescripcion()!=null){
		    holder.descripcion.setText(lugar.getDescripcion());
		}
		
		if (lugar.getFoto()==null || "".equals(lugar.getFoto()))
			holder.foto.setImageResource(R.drawable.no_imagen);
		else{
		    
		    
			holder.foto.setImageURI(Uri.parse(lugar.getFoto()));
			
			file = new File(lugar.getFoto());
            if(file.exists()){ //si existe la imagen
                holder.foto.setImageURI(Uri.parse(lugar.getFoto()));
            }else{
                holder.foto.setImageResource(R.drawable.no_imagen);

            }
            
		}
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("0")){
		    holder.iconoCategoria.setImageResource(R.drawable.restaurant);
		}
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("1")){
            holder.iconoCategoria.setImageResource(R.drawable.bar);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("2")){
            holder.iconoCategoria.setImageResource(R.drawable.cafe);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("3")){
            holder.iconoCategoria.setImageResource(R.drawable.compras);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("4")){
            holder.iconoCategoria.setImageResource(R.drawable.hoteles);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("5")){
            holder.iconoCategoria.setImageResource(R.drawable.musica);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("6")){
            holder.iconoCategoria.setImageResource(R.drawable.parques);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("7")){
            holder.iconoCategoria.setImageResource(R.drawable.aeropuertos);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("8")){
            holder.iconoCategoria.setImageResource(R.drawable.parking);
        }
		
		if(String.valueOf(lugar.getCategoria())!=null && String.valueOf(lugar.getCategoria()).equals("9")){
            holder.iconoCategoria.setImageResource(R.drawable.university);
        }
		

		return convertView;
	}
	
	/**
	 * Clase que define los campos de un elemento de la lista del adapter
	 *
	 */
	class ViewHolder {
		TextView nombre;
		TextView descripcion;
		ImageView foto;
		ImageView iconoCategoria;
	}
}
