package com.proyecto.activity;

import com.proyecto.database.LugaresDB;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.StatusBarNotify;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Actividad que permite crear nuevos lugares y editar los registrados
 */
public class CrearCategoriaActivity extends Activity{

   //Elementos Layout para mostrar los datos	
    private EditText nombreTextView, descripcionTextView;
    
	//datos de una categoria
	private String categoria, descripcion;
	
	//Id de la categoria que estamos editando
	private Long idCategoria;
	
	//Modo en el que cargamos la activity
	private String modo;
	
	//Contexto
	private Context context = this;
	
	//Variable de control
	private boolean control;
	
	    
	/**
    * Crea la vista e inicializa los componentes
    * @param savedInstanceState Bundle
    */
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        control = true;
        
		//Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
	    AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();

		//Comprobamos en que modo debemos cargar la activity
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			modo = extras.getString("modo");
			
		}
		
		//En funcion del Intent llamamos a la funcion que cargara la Activity
		if (modo.equals("crearCategoria")) {
			cargarModoCrear();
		}else if (modo.equals("editarCategoria")){
			idCategoria = extras.getLong("idCategoria");
			cargarModoEdicion(idCategoria);
		}

	}
	
    
    /**
    * Metodo que inicializa la activity en modo edicion
    * @param idLugar Long Identificador del lugar a editar
    */
	private void cargarModoEdicion(Long idCategoria) {
		//Cargamos la interfaz de modo edicion
		setContentView(R.layout.crear_categoria);
		
		//inicializar controles
        initControls();
        
		//Se obtienen los datos del lugar a editar consultando la BD a partir del idLugar
		
	    //Uri del elemento a recuperar
        Uri uri = ContentUris.withAppendedId(Uri.parse(com.proyecto.database.LugaresProvider.CONTENT_URI_CATEGORIAS + "/#"), idCategoria);
        
        //Obtenemos el cursor con los datos solicitados
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        
        //Obtenemos los indices de las columnas que queremos consultar
        int indexCategoria = cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA);
        int indexDescripcion = cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION);

		//Comprobamos que al menos existe un registro en caso contrario mostramos
		//que se ha produccido un error
		if (cursor.moveToFirst()) {
			//Obtenemos los datos a editar
			categoria =  cursor.getString(indexCategoria);
			descripcion = cursor.getString(indexDescripcion);
            
            //Cargamos en pantalla los datos
            if(categoria!=null){
                nombreTextView.setText(categoria);
            }
            if(descripcion!=null){
                descripcionTextView.setText(descripcion);
            }

		} else {
			//Mostramos que se ha producido un error
			nombreTextView.setText(R.string.errorRegistroBD);
			descripcionTextView.setText(R.string.errorRegistroBD);

		    //Los datos no han sido cargados correctamente
		    control = false;
		}
		
		//Si hay un cursor abierto, se cierra
        if (cursor != null) {
            cursor.close();
        }
       
	}

    /**
    * Metodo que inicializa la actividad en modo creacion
    */
	private void cargarModoCrear() {

		//Cargamos la interfaz de modo nuevo
		setContentView(R.layout.crear_categoria);
		
		//inicializar controles
        initControls();
        
	}


	/**
	* Metodo que finaliza la actividad
	*/
    public void cancelar(View v) {
           finish();
    }
	   

	/**
	* Metodo que valida los datos del formulario
	*/
	public boolean validarDatosFormulario() {
		String nombreText = nombreTextView.getText().toString();

		boolean result=false;
		
		//si el campo nombre está vacio mostramos mensaje 
		if(nombreText==null || nombreText.equals("")){
		    Toast.makeText(getApplicationContext(),R.string.informar_nombre,Toast.LENGTH_LONG).show();
		    
		    nombreTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
		}
		else{
		    nombreTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
		}
		
		if(nombreText!=null && !nombreText.equals("")){
		    result=true;
		}
		
		return result;
	}
	

	/**
	* Metodo que gestiona las respuestas de las actividades
	*/
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  
	}


	/**
	* Metodo que guarda en BD los datos de una categoria
	*/
	public void guardar(View v) {
	    
	    actualizarCategoria();

	}
	

    /**
  * Comportamiento de la tecla ATRAS del terminal
  * @keyCode int Codigo de la tecla pulsada
  * @event KeyEvent Evento que ha provocado la llamada al metodo
  */
  public boolean onKeyDown(int keyCode, KeyEvent event) {

     return super.onKeyDown(keyCode, event);
 }
  
  /**
   * Se muestran las opciones de menú de la pantalla mostrar
   * @param menu Objeto menu 
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate(R.menu.menu_crear_categoria, menu);

      return true;
  }
  
  /**
   * Definimos las acciones correspondientes con cada opción de menú
   * @param item MenuItem 
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          
           // Al pulsar sobre "Actalizar"
          case R.id.botonGuardarCategoria:
              
              actualizarCategoria(); 
              return true;
              
          // Al pulsar sobre "Cancelar"
          case R.id.botonCancelar:
              
              finish();
              return true;
              
          //Acción por defecto
          default:
              return super.onOptionsItemSelected(item);
      }
  }
  

  

  public void actualizarCategoria(){
      
      if (control){
          Uri uri;
          
          if(idCategoria!=null){
              uri = Uri.parse("content://com.proyecto.database.lugaresDB/categorias/" + idCategoria);
          }
          else{
              uri = Uri.parse("content://com.proyecto.database.lugaresDB/categorias");
          }
          
          ContentValues categoria = new ContentValues();

          //Comprobamos que los datos introducidos en el formulario son validos
          if (validarDatosFormulario()) {

              String nombre = nombreTextView.getText().toString();
              String descripcion = descripcionTextView.getText().toString();

              if(idCategoria!=null){
                  categoria.put(LugaresDB.Lugares._ID, idCategoria);
              }
              
              categoria.put(LugaresDB.Lugares.CATEGORIA, nombre);
              categoria.put(LugaresDB.Lugares.DESCRIPCION, descripcion);

              Intent intent = new Intent();
              
              //Guardar y actualizar lugar en BD
          if (modo.equals("editarCategoria")){
              
              getContentResolver().update(uri, categoria, null, null);

              ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.categoria_update_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

              StatusBarNotify.getInstance(context).statusBarNotify("update_categoria",nombre,descripcion);
              
              intent.putExtra("modo", "editarCategoria");
              
              intent.setClass(getApplicationContext(),TabLugaresActivity.class);
          }
          
           if (modo.equals("crearCategoria")){
               
                  getContentResolver().insert(uri, categoria);

                  ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.categoria_create_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

                  StatusBarNotify.getInstance(context).statusBarNotify("nueva_categoria",nombre,nombre);
                  
                  intent.putExtra("modo", "nuevaCategoria");
                  
                  intent.setClass(getApplicationContext(),TabLugaresActivity.class);
                  
           }
          
           startActivity(intent);
              
           finish();
           
          } else {
              
         }
      }
      
  }
  
  
  private void initControls(){

      //Obtenemos los elementos de la interfaz con los que vamos a interactuar
      nombreTextView = (EditText) this.findViewById(R.id.editNombreCategoria);
      descripcionTextView = (EditText) this.findViewById(R.id.editDescripcionCategoria);

  }
  
  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
          return super.dispatchKeyEvent(event);
  }
  
}
