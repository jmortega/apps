package com.proyecto.activity;


import com.proyecto.constants.Constants;
import com.proyecto.utility.AsyncLoading;
import com.proyecto.utility.NetWorkStatus;
import com.proyecto.utility.ToastPersonalizado;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import android.widget.EditText;
import android.widget.Toast;

/**
 * Actividad que muestra un formulario para enviar un SMS
 */
public class SendSMSActivity extends Activity implements OnClickListener {
	
	//Contexto
	private Context context = this;

	//Controles formulario
	private EditText telefonoTextView;
	private EditText mensajeTextView;
	private Button boton;
	 
	private String destino="";
	private String texto="";
	
	private LocationManager locManager;
	
	private String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    private String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
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
		setContentView(R.layout.enviar_sms);

		//inicializar controles
        initControls();
        

		//Obtenemos la ultima posicion conocida por el Proveedor de Red
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
		AsyncLoading asyncLoading = new AsyncLoading(context);
        asyncLoading.execute();
		
        //obtenemos datos para mostrar en el campo de texto el mensaje a enviar
        
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            texto= extras.getString("datos");
            
            if(texto!=null)
                mensajeTextView.setText(texto.toString());
        }
        
        boton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                destino = telefonoTextView.getText().toString();
                texto = mensajeTextView.getText().toString();
                
                enviaSMS(destino, texto);
                
                
            }
        });
        
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
              switch (getResultCode()) {
                case Activity.RESULT_OK: 
                  //Toast.makeText(_context, getText(R.string.enviado_sms_ok).toString(), Toast.LENGTH_SHORT).show(); 
                  ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.enviado_sms_ok).toString(),
                          (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
                  break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: 
                    Toast.makeText(_context, getText(R.string.envio_sms_error).toString(), Toast.LENGTH_SHORT).show(); 
                  break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: 
                    Toast.makeText(_context, getText(R.string.envio_sms_error).toString(), Toast.LENGTH_SHORT).show(); 
                   break;
                case SmsManager.RESULT_ERROR_NULL_PDU: 
                    Toast.makeText(_context, "Error en el PDU", Toast.LENGTH_SHORT).show(); 
                   break;
              }
            }
          }, 
          new IntentFilter(SENT_SMS_ACTION));

         registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                Toast.makeText(_context, getText(R.string.recibido_sms_ok).toString(), Toast.LENGTH_SHORT).show(); 
            }
          }, 
          new IntentFilter(DELIVERED_SMS_ACTION));
        
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
        menuInflater.inflate(R.menu.menu_sms, menu);
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
                
            case R.id.btnEnviarSMS:
                enviaSMS(destino, texto);
                return true;
                
            // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
    * Metodo que realiza el envio con validación previa del formulario
    * @param String destino
    * @param String texto
    */
    public void enviaSMS(String destino, String texto) {

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
                // Se envia el mensaje
                SmsManager smsMgr = SmsManager.getDefault();

                Intent sentIntent = new Intent(SENT_SMS_ACTION);
                PendingIntent sentPI = 
                    PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);
                
                Intent deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
                PendingIntent deliverPI = 
                    PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent, 0);

                
                smsMgr.sendTextMessage(destino, null, texto, sentPI, deliverPI);

                telefonoTextView.setText("");
            
                mensajeTextView.setText("");

                } catch (Exception e) {

                    ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.envio_sms_error).toString(),
                    (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

                }
        
            }else{
                
                //en caso negativo informamos de error en los campos
                ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.conexion_internet_info).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());
            
                
            }
        }else{
            
            ToastPersonalizado.getInstance(context).mostrarToastPersonalizado(getText(R.string.comprobar_campos_sms).toString(), (ViewGroup) findViewById(R.id.toastRaiz), getLayoutInflater());

        }

    }
    
    /**
    * Metodo que valida los datos del formulario
    */
    public boolean validarDatosFormulario() {
        
        String telefonoText = telefonoTextView.getText().toString();
        String mensajeText = mensajeTextView.getText().toString();
        
        boolean result=false;
        
        //si el campo telefono está vacio mostramos mensaje 
        if(telefonoText==null || telefonoText.equals("")){
            Toast.makeText(getApplicationContext(),R.string.informar_telefono,Toast.LENGTH_LONG).show();
            
            telefonoTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
        }

        
        //si el campo descripcion está vacio mostramos mensaje
        if(mensajeText==null || mensajeText.equals("")){
            Toast.makeText(getApplicationContext(),R.string.informar_mensaje,Toast.LENGTH_LONG).show();

            mensajeTextView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fondo_aux_rojo));
        }
        
        if(mensajeText!=null && !mensajeText.equals("") 
          && telefonoText!=null && !telefonoText.equals("")){
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
        intent.putExtra("contactos", "sms");
        
        startActivityForResult(intent, Constants.ACTIVIDAD_CONTACTOS);
        
    }
    
    /**
     * Metodo que gestiona las respuestas de las actividades
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if ((requestCode == Constants.ACTIVIDAD_CONTACTOS) && (resultCode == Activity.RESULT_OK)) {
            try {
                String telefono = data.getStringExtra("telefono");
                telefonoTextView.setText(telefono);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void initControls(){

        telefonoTextView = (EditText) findViewById(R.id.NumeroTelefono);
        mensajeTextView = (EditText) findViewById(R.id.TextoSms);
        
        boton = (Button) findViewById(R.id.BotonEnviarSMS);
        
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            return super.dispatchKeyEvent(event);
    }
    
}
