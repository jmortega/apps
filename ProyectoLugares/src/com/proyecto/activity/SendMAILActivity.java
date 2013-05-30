package com.proyecto.activity;


import java.io.File;

import com.proyecto.constants.Constants;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.NetWorkStatus;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Actividad que muestra un formulario para enviar un SMS
 */
public class SendMAILActivity extends Activity implements OnClickListener {
	
	//Contexto
	private Context context = this;

	//Controles formulario
	private EditText destinoTextView;
	private EditText asuntoTextView;
	private EditText mensajeTextView;
	private ImageView imgFoto;
	private CheckBox chkImagenAdjunta;
    private Button boton;

	private String destino="";
	private String asunto="";
	private String texto="";
	private String foto="";
	private File file;
	private Uri uriFoto;
	
	private LocationManager locManager;
	
	 /**
     * Crea la vista y los componentes
     * @param savedInstanceState Bundle
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Quitar la barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
		//Activamos el modo fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		//Cargamos el layout
		setContentView(R.layout.enviar_mail);

		//inicializar controles
        initControls();
        
		//Obtenemos la ultima posicion conocida por el Proveedor de Red
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
		AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
		
        //obtenemos datos para mostrar en el mail
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            texto= extras.getString("datos");
            foto= extras.getString("foto");
            asuntoTextView.setText(getText(R.string.lugar_favorito).toString());
            
            if(texto!=null)
                mensajeTextView.setText(texto.toString());
            
            //Parseamos la uri de la foto
            if(foto!=null){
                
                file = new File(foto.toString());
                
                uriFoto = Uri.fromFile(file);
                
                imgFoto.setImageURI(uriFoto);
                
                chkImagenAdjunta.setClickable(true);

                
                //Toast.makeText(context, uriFoto.toString(), Toast.LENGTH_SHORT).show();
                
            }else{
                
                imgFoto.setImageResource(R.drawable.no_imagen);
                
                //no se puede checkear imagen adjunta si no la imagen no esta disponible
                chkImagenAdjunta.setChecked(false);
                chkImagenAdjunta.setClickable(false);
            }
        }
        
        boton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                destino =  destinoTextView.getText().toString();
                asunto = asuntoTextView.getText().toString();
                texto = mensajeTextView.getText().toString();
                
                enviaMAIL(destino,asunto, texto, foto);
                
                
            }
        });
        
        //establezco listener para boton mostrar contactos
        findViewById(R.id.BotonMostrarContactos).setOnClickListener(this);

	}


    /**
     * Comportamiento de la tecla ATRAS del terminal
     * 
     * @keyCode int Codigo de la tecla pulsada
     * @event KeyEvent Evento que ha provocado la llamada al metodo
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	 
    /**
     * Se muestran las opciones de menú de la lista de lugares
     * 
     * @param menu
     *            Objeto menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mail, menu);
        return true;
    }
	    
    /**
     * Definimos las acciones correspondientes con cada opción de menú
     * 
     * @param item
     *            MenuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar sobre "Volver"
            case R.id.volver:
                finish();
                return true;
                
            case R.id.btnEnviarMAIL:
                
                enviaMAIL(destino,asunto, texto,foto);
                
                return true;
                
            // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
    * Metodo que realiza el envio con validación previa del formulario
    * @param String destino
    * @param String asunto
    * @param String mensaje
    * @param String foto
    */
    public void enviaMAIL(String destino, String asunto,String mensaje,String foto) {

        boolean red_disponible = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        boolean redOnline=false;
        
        if (NetWorkStatus.getInstance(context).isOnline(context)) {
            
            redOnline=true;
        }

        
      //Comprobamos que los datos introducidos en el formulario son validos
        if (validarDatosFormulario()) {
            
        //comprobamos si hay red y esta Online
        
        if(red_disponible && redOnline){
            
                try {
                    
                    // Se envia el correo a traves del intent ACTION_SEND
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
             
                    //vamos a enviar texto plano a menos que el checkbox esté marcado
                    intent.setType("plain/text");
             
                    //colocamos los datos para el envío
                    if(destino!=null){
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{destino});
                    }
                    
                    if(asunto!=null){
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, asunto.toString());
                    }
                    
                    if(mensaje!=null){
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, mensaje.toString());
                    }
             
                    //revisamos si el checkbox está marcado enviamos la imagen adjunta
                    if (chkImagenAdjunta!=null && chkImagenAdjunta.isChecked()) {
                        
                        //colocamos la imagen adjunta en el stream
                        intent.putExtra(Intent.EXTRA_STREAM, uriFoto);
     
                        //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://" + getPackageName() + "/" +R.drawable.icon));
                        
             
                        //indicamos el tipo de dato
                        intent.setType("image/png");
                    }
             
                    //iniciamos la actividad
                    startActivity(Intent.createChooser(intent, getText(R.string.lugar_favorito).toString()));
                    
                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.envio_mail_ok).toString(),(ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                    
                } catch (Exception e) {

                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.envio_mail_error).toString(),
                    (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

                }
        
            }else{
                
                //en caso negativo informamos de error en los campos
                ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.conexion_internet_info).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
            
                
            }
        }else{
            
            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.comprobar_campos_mail).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

        }

    }
    
    /**
    * Metodo que valida los datos del formulario
    */
    public boolean validarDatosFormulario() {
        
        String destinoText = destinoTextView.getText().toString();
        String mensajeText = mensajeTextView.getText().toString();
        
        boolean result=false;
        
        //si el campo destino está vacio mostramos mensaje 
        if(destinoText==null || destinoText.equals("")){
            Toast.makeText(getApplicationContext(),R.string.informar_destinatario,Toast.LENGTH_LONG).show();
            
            destinoTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
        }else{
            
            destinoTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
        }

        
        //si el campo texto está vacio mostramos mensaje
        if(mensajeText==null || mensajeText.equals("")){
            Toast.makeText(getApplicationContext(),R.string.informar_mensaje,Toast.LENGTH_LONG).show();

            mensajeTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
            
        }else{
            
            mensajeTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_verde));
        }
        
        if(mensajeText!=null && !mensajeText.equals("") && destinoText!=null && !destinoText.equals("")){
            result=true;
        }
        
        return result;
    }


    /**
     * Metodo que llama a la activity de mostrar contactos
     */
    @Override
    public void onClick(View arg0) {

        AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
        
        
        Intent intent = new Intent(this,MostrarContactosActivity.class);
        intent.putExtra("contactos", "mail");
        
        startActivityForResult(intent, Constants.ACTIVIDAD_CONTACTOS);
        
    }
    
    /**
     * Metodo que gestiona las respuestas de las actividades
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if ((requestCode == Constants.ACTIVIDAD_CONTACTOS) && (resultCode == Activity.RESULT_OK)) {
            try {
                String destino = data.getStringExtra("telefono");
                
                destino =destinoTextView.getText().toString()+destino+";";
                
                destinoTextView.setText(destino);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private void initControls(){

        //Recogemos los elementos de la interfaz que nos hacen falta para cargar los datos
        destinoTextView = (EditText) findViewById(R.id.Destino);
        mensajeTextView = (EditText) findViewById(R.id.Texto);
        asuntoTextView = (EditText) findViewById(R.id.Asunto);
        imgFoto = (ImageView) findViewById(R.id.imagenFoto);
        chkImagenAdjunta = (CheckBox) findViewById(R.id.checkImagenAdjunta);
        boton = (Button) findViewById(R.id.BotonEnviarMAIL);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
    
}
