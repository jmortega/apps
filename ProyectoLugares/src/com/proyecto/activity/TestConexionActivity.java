package com.proyecto.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Actividad que chequea la conexión del usuario mostrando información sobre la misma
 */

public class TestConexionActivity extends Activity {

    private ConnectivityReceiver receiver       = null;
    private TextView             txtNetworkInfo = null;
    private ImageView            connected      = null;
    private ImageView            disconnected   = null;
    
    private String estado = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.test_conexion);

        initControls();
        
        receiver = new ConnectivityReceiver();

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);

        super.onDestroy();
    }

    /**
     * Obtiene el estado de la conexion
     * @param NetworkInfo.State state
     */
    private String getNetworkStateString(NetworkInfo.State state) {
        estado = getText(R.string.desconocido).toString();

        switch (state) {
            case CONNECTED:
                estado = getText(R.string.conectado).toString();
                connected.setVisibility(View.VISIBLE);
                disconnected.setVisibility(View.INVISIBLE);
                break;
            case CONNECTING:
                estado = getText(R.string.conectando).toString();
                connected.setVisibility(View.INVISIBLE);
                disconnected.setVisibility(View.INVISIBLE);
                break;
            case DISCONNECTED:
                estado = getText(R.string.desconectado).toString();
                connected.setVisibility(View.INVISIBLE);
                disconnected.setVisibility(View.VISIBLE);
                break;
            case DISCONNECTING:
                estado = getText(R.string.desconectando).toString();
                connected.setVisibility(View.INVISIBLE);
                disconnected.setVisibility(View.INVISIBLE);
                break;
            case SUSPENDED:
                estado = getText(R.string.suspendido).toString();
                connected.setVisibility(View.INVISIBLE);
                disconnected.setVisibility(View.INVISIBLE);
                break;
            default:
                estado = getText(R.string.desconocido).toString();
                connected.setVisibility(View.INVISIBLE);
                disconnected.setVisibility(View.INVISIBLE);
                break;
        }

        return estado;
    }

    private class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (null != info) {
                String state = getNetworkStateString(info.getState());
                String stateString = info.toString().replace(',', '\n');

                String infoString = String.format(getText(R.string.tipo_red).toString()+":" +"%s\n"+getText(R.string.estado_red).toString()+":"+"%s\n\n%s", info.getTypeName(), state, stateString);

                txtNetworkInfo.setText(infoString);
            }
        }
    }
    
    private void initControls(){

        txtNetworkInfo = (TextView) findViewById(R.id.txtNetworkInfo);
        connected = (ImageView) findViewById(R.id.imageViewConnected);
        disconnected = (ImageView) findViewById(R.id.imageViewDisconnected);
        connected.setVisibility(View.INVISIBLE);
        disconnected.setVisibility(View.INVISIBLE);
    }
    
    /**
     * Comportamiento de la tecla ATRAS del terminal
     * @keyCode int Codigo de la tecla pulsada
     * @event KeyEvent Evento que ha provocado la llamada al metodo
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            
            //Devolvemos el estado de la conexion a la pantalla principal
            
            Intent intent= new Intent();
            
            if(estado.equals(getText(R.string.conectado).toString())){
                intent.putExtra("estado", "conectado");
            }
            if(estado.equals(getText(R.string.desconectado).toString())){
                intent.putExtra("estado", "desconectado");
            }

            intent.setClass(getApplicationContext(),PrincipalActivityAux.class);
            setResult(Activity.RESULT_OK, intent);
            
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    

    /**
     * Se muestran las opciones de menú de la pantalla principal
     * @param menu Objeto menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_imagenes, menu);
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
                
              //Devolvemos el estado de la conexion a la pantalla principal
                
                Intent intent= new Intent();
                
                if(estado.equals(getText(R.string.conectado).toString())){
                    intent.putExtra("estado", "conectado");
                }
                if(estado.equals(getText(R.string.desconectado).toString())){
                    intent.putExtra("estado", "desconectado");
                }

                intent.setClass(getApplicationContext(),PrincipalActivityAux.class);
                setResult(Activity.RESULT_OK, intent);
                
                finish();
                return true;
                
                    
                // Acción por defecto
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
