package com.proyecto.adapter;

import java.util.ArrayList;
import com.proyecto.activity.R;
import com.proyecto.entity.CategoriaEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adaptador utilizado para mostrar la lista de categorias en una lista con un formato personalizado
 */
public class ListaCategoriasAdapter extends BaseAdapter {
    
    //lista para guardar las categorias
	private ArrayList<CategoriaEntity> categorias;
	
	private LayoutInflater mInflater;
	/**
	 * Constructor del adaptador
	 * @param context Context Contexto de la aplicacion
	 * @param lugares ArrayList Array con la lista de categorias
	 */
	public ListaCategoriasAdapter(Context context, ArrayList<CategoriaEntity> categorias) {
		this.mInflater = LayoutInflater.from(context);
		this.categorias = categorias;
	}
	
	/**
	 * Devuelve la cantidad de categorias almacenadas
	 */
	public int getCount() {
		return categorias.size();
	}
	
	/**
	 * Devuelve el item de tipo Categoria indicado como parametro
	 * @param index int Indice donde se encuentra la Categoria requerido en la lista
	 */
	public CategoriaEntity getItem(int index) {
		return categorias.get(index);
	}
	
	/**
	 * Devuelve el indice indicado como parametro
	 * @param position int Indice donde se encuentra la categoria en la lista
	 */
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Rellena la vista y la devuelve con los datos de una categoria
	 * Se realizara tantas veces como Lugares existan hasta completar la vista con la lista
	 * @param position int Posicion en la lista de Categorias
	 * @param convertView View Vista que contiene los datos de un Lugar
	 * @param parent ViewGroup Grupo de vistas
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
		    convertView = mInflater.inflate(R.layout.categorias_item, null);
			holder = new ViewHolder();
			holder.nombre = (TextView) convertView.findViewById(R.id.categoriaNombre);
			holder.iconoCategoria = (ImageView) convertView.findViewById(R.id.iconoCategoria);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CategoriaEntity categoria = getItem(position);
		if(categoria.getNombre()!=null){
		    holder.nombre.setText(categoria.getNombre());
		}
		
		holder.iconoCategoria.setImageResource(R.drawable.network);
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("0")){
		    holder.iconoCategoria.setImageResource(R.drawable.restaurant);
		}
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("1")){
            holder.iconoCategoria.setImageResource(R.drawable.bar);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("2")){
            holder.iconoCategoria.setImageResource(R.drawable.cafe);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("3")){
            holder.iconoCategoria.setImageResource(R.drawable.compras);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("4")){
            holder.iconoCategoria.setImageResource(R.drawable.hoteles);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("5")){
            holder.iconoCategoria.setImageResource(R.drawable.musica);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("6")){
            holder.iconoCategoria.setImageResource(R.drawable.parques);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("7")){
            holder.iconoCategoria.setImageResource(R.drawable.aeropuertos);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("8")){
            holder.iconoCategoria.setImageResource(R.drawable.parking);
        }
		
		if(String.valueOf(categoria.get_id())!=null && String.valueOf(categoria.get_id()).equals("9")){
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
		ImageView iconoCategoria;
	}
}
