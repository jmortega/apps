package com.proyecto.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.proyecto.constants.Constants;
import com.proyecto.database.LugaresDB;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.StatusBarNotify;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * Actividad que permite crear nuevos lugares y editar los registrados
 */
public class EditarLugarActivityAux extends Activity{

   //Elementos Layout para mostrar los datos	
	private TextView longitudTextView, latitudTextView;
    private EditText nombreTextView, descripcionTextView;
    
    private RadioButton radioButtonCamara;
    private RadioButton radioButtonGaleria;
    private Button zoomImagen;
    
	//Rating
	private RatingBar ratingbar;
	private Float rating=new Float(0);
	
	//combo categorias
	private Spinner  spinner;
	private String[] categorias;
	
	//datos de un lugar
	private Double longitud, latitud;
	private String nombre, descripcion,descripcionLocation;
    private String nombreCategoria="";
    
  //Datos categoria
    private TextView textCategoria;
    private ImageView iconCategory;
    
	//Vista donde mostraremos la imagen del lugar
	private ImageView imgFoto;

	//Uri de la imagen almacenada
	private String foto;

	//Id del lugar que estamos editando
	private Long idLugar;
	
	//Id de la categoria
	private Long idCategoria;

	//Modo en el que cargamos la activity
	private String modo;
	
	//Contexto
	private Context context = this;
	
	//Variable de control
	private boolean control;
	
	//Array para almacenar las categorias que recogemos de la BD
    ArrayList<com.proyecto.entity.CategoriaEntity> category = new ArrayList<com.proyecto.entity.CategoriaEntity>();
    
	private int     imageWidth  = 0;
	private int     imageHeight = 0;
	    
	public Float getRating() {
	        return rating;
	}

	public void setRating(Float rating) {
	        this.rating = rating;
	}
	    
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
		if (modo.equals("crearLugar")) {
		    longitud = extras.getDouble("longitud");
            latitud = extras.getDouble("latitud");
            descripcion= extras.getString("descripcion");
            nombreCategoria = extras.getString("nombreCategoria");
            idCategoria = extras.getLong("idCategoria");
			cargarModoCrear();
		}else if (modo.equals("editarLugar")){
			idLugar = extras.getLong("idLugar");
			idCategoria = (long) extras.getInt("idCategoria");
			nombreCategoria = extras.getString("nombreCategoria");
			cargarModoEdicion(idLugar);
		}
		
        ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setRating(rating);
                ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.puntuacion_actualizada)+" "+rating, (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

    
            }
        }); 
        
	}
	
    
    /**
    * Metodo que inicializa la activity en modo edicion
    * @param idLugar Long Identificador del lugar a editar
    */
	private void cargarModoEdicion(Long idLugar) {
		//Cargamos la interfaz de modo edicion
		setContentView(R.layout.editar_lugar);
		
		//inicializar controles
        initControls();
        
        cargarCategorias();
        
		//Se obtienen los datos del lugar a editar consultando la BD a partir del idLugar
		
	    //Uri del elemento a recuperar
        Uri uri = ContentUris.withAppendedId(Uri.parse(com.proyecto.database.LugaresProvider.CONTENT_URI_LUGARES + "/#"), idLugar);
        
        //Obtenemos el cursor con los datos solicitados
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        
        File file = null;
        
        //Obtenemos los indices de las columnas que queremos consultar
        int indexNombre = cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE);
        int indexDescripcion = cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION);
        int indexFoto = cursor.getColumnIndex(LugaresDB.Lugares.FOTO);
        int indexLon = cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD);
        int indexLat = cursor.getColumnIndex(LugaresDB.Lugares.LATITUD);
        int indexRating = cursor.getColumnIndex(LugaresDB.Lugares.RATING);
        int indexCategoria = cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA);
        
		//Comprobamos que al menos existe un registro en caso contrario mostramos
		//que se ha produccido un error
		if (cursor.moveToFirst()) {
			//Obtenemos los datos a editar
			nombre =  cursor.getString(indexNombre);
			descripcion = cursor.getString(indexDescripcion);
			longitud = new Double(cursor.getString(indexLon));
		    latitud = new Double(cursor.getString(indexLat));
			foto = cursor.getString(indexFoto);
			idCategoria=(long) cursor.getInt(indexCategoria);
			
			if(cursor.getString(indexRating)!=null){
			    rating = new Float(cursor.getString(indexRating));
			}
			
			if(foto!=null){
			    //Cargamos en la interfaz los datos
			    Uri uriFoto = Uri.parse(foto);

	            file = new File(foto);
                if(file.exists()){ //si existe la imagen
                    imgFoto.setImageURI(uriFoto);
                    zoomImagen.setEnabled(true);
                }else{
                    imgFoto.setImageResource(R.drawable.no_imagen);
                    zoomImagen.setEnabled(false);
                }
			}
            

            //Cargamos en pantalla los datos
            if(nombre!=null){
                nombreTextView.setText(""+nombre);

            }
            if(descripcion!=null){
                descripcionTextView.setText(""+descripcion);
            }
            if(latitud!=null){
                latitudTextView.setText(""+latitud.toString());
            }
            if(longitud!=null){
                longitudTextView.setText(""+longitud.toString());
            }
            
            if(rating!=null){
                ratingbar.setRating(rating);
                
            }
            
            if(nombreCategoria!=null && !nombreCategoria.equals("")){
                textCategoria.setText(""+nombreCategoria);
                textCategoria.requestFocus();
            }
            
            setTitle(String.format(getString(R.string.editar)));

            //obtenemos icono de la categoria
            iconoCategoria(nombreCategoria);
            
		} else {
			//Mostramos que se ha producido un error
			nombreTextView.setText(R.string.errorRegistroBD);
			descripcionTextView.setText(R.string.errorRegistroBD);
			latitudTextView.setText(R.string.errorRegistroBD);
	        longitudTextView.setText(R.string.errorRegistroBD);
		    imgFoto.setImageResource(R.drawable.no_imagen);
		    
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
		setContentView(R.layout.crear_lugar);
		
		//inicializar controles
        initControls();
        
		if (longitud != null){
			
	        longitudTextView.setText(longitud.toString());
		}
		
		if (latitud != null){
		    latitudTextView.setText(latitud.toString());
		}
		
		if (descripcion != null){
		    descripcionTextView.setText(descripcion.toString());
	    }
		
		 if(nombreCategoria!=null && !nombreCategoria.equals("")){
             textCategoria.setText(""+nombreCategoria);
             textCategoria.requestFocus();
         }
         
         setTitle(String.format(getString(R.string.crear_lugar)));

         //obtenemos icono de la categoria
         iconoCategoria(nombreCategoria);
	}

	
    /**
     * Metodo que llama a la camara para obtener una imagen
     */
    public void obtenerImagen(View v) {

        obtenerImagenAux();
        
    }
	    
	/**
    * Metodo que llama a la camara para obtener una imagen
    */
	public void ImagenCamara() {
		if (control) {
			Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, Constants.ACTIVIDAD_CAMARA);
		}
	}

	/**
    * Metodo que elimina un lugar con confirmación de borrado
    */
	public void eliminar(View v) {
	    
	        eliminarLugar();

    }
	
	/**
	* Metodo que finaliza la actividad
	*/
    public void cancelar(View v) {
           finish();
    }
	   
    /**
    * Metodo que llama a la Galeria par obtener una imagen como resultado
    */
	public void galeriaImagenes() {
		if (control) {
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
			startActivityForResult(Intent.createChooser(i, getString(R.string.galeriaImagenes)), Constants.ACTIVIDAD_GALERIA);
		}
	}
	

	/**
	* Metodo que valida los datos del formulario
	*/
	public boolean validarDatosFormulario() {
		String nombreText = nombreTextView.getText().toString();
		String descripcionText = descripcionTextView.getText().toString();
		
		boolean result=false;
		
		//si el campo nombre está vacio mostramos mensaje 
		if(nombreText==null || nombreText.equals("")){
		    Toast.makeText(getApplicationContext(),R.string.informar_nombre,Toast.LENGTH_LONG).show();
		    
		    nombreTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
		}
		else{
		    nombreTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
		}
		
		//si el campo descripcion está vacio mostramos mensaje
		if(descripcionText==null || descripcionText.equals("")){
            Toast.makeText(getApplicationContext(),R.string.informar_descripcion,Toast.LENGTH_LONG).show();

            descripcionTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
        }else{

            descripcionTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
        }
		
		if(nombreText!=null && !nombreText.equals("") 
		  && descripcionText!=null && !descripcionText.equals("") && (longitud != null) && (latitud != null)){
		    result=true;
		}
		
		return result;
	}
	
	/**
	* Metodo que carga la actividad que permite editar la localizacion donde seleccionaremos la posicion en el mapa
	*/
	public void editar_localizacion(View v) {
	    
	    editarLocalizacion();

	}

	/**
	* Metodo que gestiona las respuestas de las actividades
	*/
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  
	  //Ventana que se muestra de forma asíncrona mientras se cargan los datos
      AsyncLoading asyncLoading = new AsyncLoading(context);
      asyncLoading.execute();
      
	  OutputStream outStream = null;
	  File file = null;
	  
	  //Comprobamos que la Tarjeta SD este montada
	  String state = Environment.getExternalStorageState();
	  if (!state.equals(Environment.MEDIA_MOUNTED)){
		  Toast.makeText(this, getText(R.string.sd_no_montada), Toast.LENGTH_LONG).show();
	  } else {
	      
		  //Comprobamos que el directorio para almacenar las imagenes existe
		  File dir = new File(Environment.getExternalStorageDirectory().toString() + "/lugares");
		  
		  if (!dir.exists()) {
			dir.mkdirs();
		  }

		  //Creamos el archivo donde almacenaremos la foto
		  file = new File(dir, android.text.format.DateFormat.format("yyyyMMdd_hhmmss", new java.util.Date()).toString() + ".png");
		  
		  try {
			outStream = new FileOutputStream(file);
		  } catch (FileNotFoundException e1) {
			 e1.printStackTrace();
			 ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_en_el_proceso).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
		        
		  }
	  }
	  
	  //Comprobamos que Actitiy es la que nos responde
	  switch(requestCode) { 
	    case Constants.ACTIVIDAD_MAPA: 
	    	//Mostramos por pantalla la localizacion obtenida y la almacenamos
	    	if (resultCode == Activity.RESULT_OK) { 
	    	    
	    	    Bundle extras = data.getExtras();
	    	    
	            if(extras != null) {
	               
	                latitud = extras.getDouble("latitud");
	                longitud = extras.getDouble("longitud");
	                descripcionLocation = extras.getString("descripcion");

	                //actualizamos campos latitud y longitud
	                latitudTextView.setText(latitud.toString());
	                longitudTextView.setText(longitud.toString());
	                
	                latitudTextView.setTextColor(R.color.negro);
	                longitudTextView.setTextColor(R.color.negro);
	                
	                latitudTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
	                longitudTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
	                
	                //actualizamos campo decripcion
	                if(descripcionLocation!=null && !descripcionLocation.equals("") && !descripcionLocation.equals(descripcion)){
	                   descripcionTextView.setText(descripcionLocation.toString());
	                   descripcionTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));

	                   ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.localizacion_actualizada)+"\n\n"+getText(R.string.latitud)+": "+latitud+"\n"+getText(R.string.longitud)+": "+longitud
                               +"\n"+getText(R.string.descripcion)+": "+descripcionLocation, (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

	                }else{
	                    
	                   ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.localizacion_actualizada)+"\n\n"+getText(R.string.latitud)+": "+latitud+"\n"+getText(R.string.longitud)+": "+longitud, (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

	                }
	                
	            }
			
            }
	      	break; 
	    case Constants.ACTIVIDAD_CAMARA:
	        
	        //Si la activity nos devuelve una imagen de la camara
	        
	    	if (resultCode != RESULT_CANCELED && state.equals(Environment.MEDIA_MOUNTED)) {
	    	
	    	    try{
	    	        
	    	    try {
	    	        
	    	        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
	    	        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 350, 275,true);
	    	        resized.compress(Bitmap.CompressFormat.PNG, 100, outStream);

	    	        imageWidth = bitmap.getWidth();
                    imageHeight = bitmap.getHeight();
                    
	    	    } catch (Exception ex) {
                    ex.printStackTrace();
                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_imagen_camara).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                    finish(); 
                }
	    	    
				try {
					outStream.flush();
					outStream.close();
				} catch (Exception ex) {
					ex.printStackTrace();
		            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_imagen_camara).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
		            finish();   
				}

				//Obtenemos la uri de la foto almacenada
				if(file!=null){
				    foto = file.getPath();
				
                        /**
                         * Para guardar la imagen en la galeria, utilizamos una
                         * conexion a un MediaScanner
                         */
                        new MediaScannerConnectionClient() {
                            private MediaScannerConnection msc = null;
                            {
                                msc = new MediaScannerConnection(getApplicationContext(), this);
                                msc.connect();
                            }

                            public void onMediaScannerConnected() {
                                msc.scanFile(foto, null);
                            }

                            public void onScanCompleted(String path, Uri uri) {
                                msc.disconnect();
                            }
                        };
				}
				     
				if(foto!=null){
				    imgFoto.setImageURI(Uri.parse(foto));
				    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.imagen_camara_ok).toString()+"\n"+getText(R.string.resolucion).toString()+" "+imageWidth+"X"+imageHeight, (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
				}
				
			} catch (OutOfMemoryError e) {
			    
			    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_imagen_camara).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
			    System.gc();
			    finish();

			}
	    }
				
			break;
	    case Constants.ACTIVIDAD_GALERIA:
	    	if (resultCode != RESULT_CANCELED && state.equals(Environment.MEDIA_MOUNTED)) {
				
	    		Uri selectedImageUri = data.getData();

				Bitmap bitmap = null;

				try{
				    
				try {
					bitmap = DecodeUri(selectedImageUri, this.getContentResolver());
					//MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
					
					bitmap = Bitmap.createScaledBitmap(bitmap, 350, 275,true);

	                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	                
	                imageWidth = bitmap.getWidth();
	                imageHeight = bitmap.getHeight();
					
				} catch (Exception e) {
					e.printStackTrace();
					ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_imagen_galeria).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
					finish();
				}

				try {
					outStream.flush();
					outStream.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_imagen_galeria).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
					finish();
				}
				
				if(file!=null)
				    foto = file.getPath();
				
				if(bitmap!=null){
				    imgFoto.setImageBitmap(bitmap);
				    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.imagen_galeria_ok).toString()+"\n"+getText(R.string.resolucion).toString()+" "+imageWidth+"X"+imageHeight, (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
				}
				
	    	    } catch (OutOfMemoryError e) {
                
	    	        ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.error_imagen_camara).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
	    	        System.gc();
	    	        finish();

            }

			}
	    	break;
	  } 
	}


    // Comprimir bitmap
    public Bitmap DecodeUri(Uri selectedImage, ContentResolver contResolver) {

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contResolver.openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 140;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(contResolver.openInputStream(selectedImage), null, o2);

        } catch (Exception e) {
            return null;

        }

    }
	
	/**
	* Metodo que guarda en BD los datos de un lugar
	*/
	public void guardar(View v) {
	    
	    actualizarLugar();

	}
	
    /**
     * Se llama al hacer click en el boton "Zoom imagen"
     * 
     * @param v
     *            View
     */
    public void zoomImagen(View v) {
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ZoomImagenActivity.class);
        intent.putExtra("rutaImagen",foto);
        startActivity(intent);

    }
		
    /**
  * Comportamiento de la tecla ATRAS del terminal
  * @keyCode int Codigo de la tecla pulsada
  * @event KeyEvent Evento que ha provocado la llamada al metodo
  */
  public boolean onKeyDown(int keyCode, KeyEvent event) {
     if (keyCode == KeyEvent.KEYCODE_BACK) {
         if (modo.equals("crearLugar")) {
             finish();  
         }else{
             
             //Ventana que se muestra de forma asíncrona mientras se cargan los datos
             AsyncLoading asyncLoading = new AsyncLoading(context);
             asyncLoading.execute();
             
             Intent intent = new Intent(getApplicationContext(), MostrarLugarActivityAux.class);
             //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
             intent.putExtra(LugaresDB.Lugares._ID, idLugar);
             intent.putExtra("idLugar", idLugar);
             startActivity(intent);
             finish();
         }
         return true;
     }
     return super.onKeyDown(keyCode, event);
 }
  
  @Override
  protected Dialog onCreateDialog(int idAux) {
      Dialog newDialog = null;
      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      LayoutInflater factory = LayoutInflater.from(this);
      final View textEntryView = factory.inflate(R.layout.custom_dialog_aux, null);
      
      TextView text = (TextView) textEntryView.findViewById (R.id.dialog_text);         

      text.setText(getText(R.string.info_delete).toString());
      
      switch (idAux) {
          case Constants.DIALOGO_BORRADO_LUGAR:
              builder.setMessage(R.string.confirmar_eliminar_lugar);
              builder.setView(textEntryView); 
              builder.setIcon(R.drawable.warning);
              builder.setCancelable(true);
              builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int idAux) {

                      int borrado = getContentResolver().delete(
                              LugaresDB.CONTENT_URI_LUGARES,
                              LugaresDB.Lugares._ID + "=?", new String[] { "" + idLugar });
                      if (borrado > 0) {

                          ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugar_eliminado_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

                          StatusBarNotify.getInstance(context).statusBarNotify("eliminar",nombre,descripcion);

                          Intent intent = new Intent();
                          intent.setClass(getApplicationContext(), TabLugaresActivity.class);
                          startActivity(intent);
                          
                          finish();
                      } else {// el elemento no se ha podido borrar
                          Toast.makeText(getApplicationContext(),R.string.lugar_eliminado_error,Toast.LENGTH_LONG).show();
                      }
                  }
              });

              builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
                  public void onClick(DialogInterface dialog, int idAux) {
                      dialog.cancel();
                  }
              });
              newDialog = builder.create();
              break;

      }

      return newDialog;

  }
  
  /**
   * Se muestran las opciones de menú de la pantalla mostrar
   * @param menu Objeto menu 
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      if (modo.equals("editarLugar")){
          MenuInflater menuInflater = getMenuInflater();
          menuInflater.inflate(R.menu.menu_editar, menu);
      }
      if (modo.equals("crearLugar")){
          MenuInflater menuInflater = getMenuInflater();
          menuInflater.inflate(R.menu.menu_crear, menu);
      }
      return true;
  }
  
  /**
   * Definimos las acciones correspondientes con cada opción de menú
   * @param item MenuItem 
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          // Al pulsar sobre "Editar Localizacion"
          case R.id.btnEditarLocalizacion:
              
              editarLocalizacion();
              return true;
              
           // Al pulsar sobre "Actalizar"
          case R.id.btnActualizarLugar:
              
              actualizarLugar(); 
              return true;
              
          // Al pulsar sobre "Eliminar"
          case R.id.btnEliminarLugar:
              
              eliminarLugar();
              return true;
         
          // Al pulsar sobre "Cancelar"
          case R.id.btnCancelar:
              
              finish();
              return true;
          
          //Al pulsar sobre "Obtener imagen"
          case R.id.btnObtenerImagen:
              
              obtenerImagenAux();
              
              return true;
          
          // Al pulsar sobre "Volver"
          case R.id.volver:
              
              //Ventana que se muestra de forma asíncrona mientras se cargan los datos
              AsyncLoading asyncLoading = new AsyncLoading(context);
              asyncLoading.execute();
              
              Intent intent = new Intent(getApplicationContext(), MostrarLugarActivityAux.class);
              //Le pasamos el id del Lugar seleccionado para poder cargar posteriormente sus datos
              intent.putExtra(LugaresDB.Lugares._ID, idLugar);
              intent.putExtra("idLugar", idLugar);
              startActivity(intent);
              finish();
              
              return true;
              
          //Acción por defecto
          default:
              return super.onOptionsItemSelected(item);
      }
  }
  
  public void eliminarLugar(){
      
         // Confirmar borrado
         Dialog confirmarBorrado;
         confirmarBorrado = onCreateDialog(Constants.DIALOGO_BORRADO_LUGAR);
         confirmarBorrado.show();

  }
  
  public void obtenerImagenAux() {

      if (radioButtonCamara.isChecked()) {

          ImagenCamara();

      } else if (radioButtonGaleria.isChecked()) {

          galeriaImagenes();
      }
  }
  
  public void editarLocalizacion(){
      
      //Ventana que se muestra de forma asíncrona mientras se cargan los datos
      AsyncLoading asyncLoading = new AsyncLoading(context);
      asyncLoading.execute();
      
      Intent intent = new Intent();

      if (latitud != null){
          intent.putExtra("latitud", latitud);
      }
          
      if(longitud != null){
          intent.putExtra("longitud", longitud);
      }
      
      intent.setClass(getApplicationContext(),LocalizacionMapaActivity.class);
      
      startActivityForResult(intent, Constants.ACTIVIDAD_MAPA);
   }
  

  public void actualizarLugar(){
      
      if (control){
          Uri uri;
          
          if(idLugar!=null){
              uri = Uri.parse("content://com.proyecto.database.lugaresDB/lugares/" + idLugar);
          }
          else{
              uri = Uri.parse("content://com.proyecto.database.lugaresDB/lugares");
          }
          ContentValues lugar = new ContentValues();

          //Comprobamos que los datos introducidos en el formulario son validos
          if (validarDatosFormulario()) {

              String nombre = nombreTextView.getText().toString();
              String descripcion = descripcionTextView.getText().toString();
              String lat = latitud.toString();
              String lon = longitud.toString();

              if(idLugar!=null){
                  lugar.put(LugaresDB.Lugares._ID, idLugar);
              }
              
              lugar.put(LugaresDB.Lugares.NOMBRE, nombre);
              lugar.put(LugaresDB.Lugares.DESCRIPCION, descripcion);
              lugar.put(LugaresDB.Lugares.LATITUD, lat);
              lugar.put(LugaresDB.Lugares.LONGITUD, lon);
              lugar.put(LugaresDB.Lugares.FOTO, foto);
              lugar.put(LugaresDB.Lugares.RATING, rating);
              lugar.put(LugaresDB.Lugares.CATEGORIA, idCategoria);
              
              Intent intent = new Intent();
              
              //Guardar y actualizar lugar en BD
          if (modo.equals("editarLugar")){
              
              getContentResolver().update(uri, lugar, null, null);

              ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugar_actualizado_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

              StatusBarNotify.getInstance(context).statusBarNotify("actualizar",nombre,descripcion);
              
              intent.putExtra("idLugar", idLugar);
              
              intent.putExtra("idCategoria", idCategoria);
              
              intent.setClass(getApplicationContext(), MostrarLugarActivityAux.class);
          }
          
           if (modo.equals("crearLugar")){
               
                  getContentResolver().insert(uri, lugar);

                  ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.lugar_creado_ok).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

                  StatusBarNotify.getInstance(context).statusBarNotify("guardar",nombre,descripcion);
                  
                  intent.setClass(getApplicationContext(),TabLugaresActivity.class);
                  
           }
          
           startActivity(intent);
              
           finish();
           
          } else {
              //en caso negativo informamos de error en los campos
              ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.comprobar_campos).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
          }
      }
      
  }
  
  
  private void initControls(){

      //Obtenemos los elementos de la interfaz con los que vamos a interactuar
      nombreTextView = (EditText) this.findViewById(R.id.editLugarNombre);
      descripcionTextView = (EditText) this.findViewById(R.id.editLugarDescripcion);
      latitudTextView = (TextView) this.findViewById(R.id.lugarLatitud);
      longitudTextView = (TextView) this.findViewById(R.id.lugarLongitud);
      imgFoto = (ImageView) findViewById(R.id.imagenFoto);
      imgFoto.setImageResource(R.drawable.no_imagen);
      ratingbar = (RatingBar)findViewById(R.id.idRatingbar);
      zoomImagen = (Button) findViewById(R.id.botonZoomImagen);
      textCategoria = (TextView)findViewById(R.id.textCategoria);
      iconCategory = (ImageView)findViewById(R.id.iconCategory);
      
      spinner = (Spinner) findViewById(R.id.SpinnerCategorias);
      
      zoomImagen.setEnabled(false);
      
      //inicializo componentes radioButton
      radioButtonCamara = (RadioButton)findViewById(R.id.radiobtnCamara);
      radioButtonGaleria = (RadioButton)findViewById(R.id.radiobtnGaleria);
      
      
  }
  
  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
          return super.dispatchKeyEvent(event);
  }
  
  private void iconoCategoria(String nombreCategoria){

      if(nombreCategoria.equals(getText(R.string.restaurantes).toString())){
          iconCategory.setImageResource(R.drawable.restaurant);
      }
      
      if(nombreCategoria.equals(getText(R.string.bares).toString())){
          iconCategory.setImageResource(R.drawable.bar);
      }
      
      if(nombreCategoria.equals(getText(R.string.cafe).toString())){
          iconCategory.setImageResource(R.drawable.cafe);
      }
      
      if(nombreCategoria.equals(getText(R.string.compras).toString())){
          iconCategory.setImageResource(R.drawable.compras);
      }
      
      if(nombreCategoria.equals(getText(R.string.hoteles).toString())){
          iconCategory.setImageResource(R.drawable.hoteles);
      }
      
      if(nombreCategoria.equals(getText(R.string.musica).toString())){
          iconCategory.setImageResource(R.drawable.musica);
      }
      
      if(nombreCategoria.equals(getText(R.string.parques).toString())){
          iconCategory.setImageResource(R.drawable.parques);
      }
      
      if(nombreCategoria.equals(getText(R.string.aeropuertos).toString())){
          iconCategory.setImageResource(R.drawable.aeropuertos);
      }
      
      if(nombreCategoria.equals(getText(R.string.parking).toString())){
          iconCategory.setImageResource(R.drawable.parking);
      }
      
      if(nombreCategoria.equals(getText(R.string.universidades).toString())){
          iconCategory.setImageResource(R.drawable.university);
      }
  }
  
  public void cargarCategorias(){
    //Campos a recuperar en la consulta
      final String[] columnas = {LugaresDB.Lugares._ID,LugaresDB.Lugares.CATEGORIA};
      
      //Uri de la tabla a consultar
      Uri uri = com.proyecto.database.LugaresProvider.CONTENT_URI_CATEGORIAS;

      Cursor cursor = getContentResolver().query(uri,columnas,null, null, null);

      //colocamos el cursor en el primer elemento
      cursor.moveToFirst();

      for (int i = 0; i < cursor.getCount(); i++) {
          int id = cursor.getInt(cursor.getColumnIndex(LugaresDB.Lugares._ID));
          String categoria = cursor.getString(cursor.getColumnIndex(LugaresDB.Lugares.CATEGORIA));

          com.proyecto.entity.CategoriaEntity categoriaEntity = new com.proyecto.entity.CategoriaEntity(id,categoria);
          
          category.add(categoriaEntity);
          
          //avanzar el cursor al siguiente elemento para siguiente iteracion
          cursor.moveToNext();
      }
      

      //inicializar array con tantos lugares como hayamos encontrado
      categorias=new String[category.size()+1];
      
      for(int i=0;i<categorias.length;i++)
          categorias[i]="";
      
      categorias[0]=getText(R.string.nueva_categoria).toString();
      
      
      for (int i = 0; i < category.size(); i++) {
          categorias[i+1]=category.get(i).getNombre();
      }
      
      //Si hay un cursor abierto, se cierra
      if (cursor!= null) {
          cursor.close();
      }
      
      if(categorias!=null && categorias.length>0){
          
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
          spinner.setAdapter(adapter);
          spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

          public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
              int position = spinner.getSelectedItemPosition();
              
              if(position>0){
                  
                  idCategoria=category.get(position-1).get_id();
                  nombreCategoria=category.get(position-1).getNombre();
                  Toast.makeText(getApplicationContext(),getText(R.string.nueva_categoria)+":"+nombreCategoria,Toast.LENGTH_LONG).show();

              }
              
          }

          public void onNothingSelected(AdapterView<?> arg0) {
          }

          });
      }
  }
  
}
