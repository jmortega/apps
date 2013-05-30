package com.proyecto.utility;

import com.proyecto.activity.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Clase que permite mostrar de forma asíncrona un diálogo mientras se cargan
 * los datos
 */
public class AsyncLoading extends AsyncTask<String, Void, Boolean> {

    private ProgressDialog dialog;
    private Context        ctx;

    /**
     * Constructor a partir del contexto
     * 
     * @param context
     *            Contexto de la actividad
     */
    public AsyncLoading(Context ctx) {
        this.ctx = ctx;
        dialog = new ProgressDialog(ctx);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return null;
    }

    /**
     * Muestra ventana de dialogo
     * 
     */
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(ctx, ctx.getText(R.string.cargando), ctx.getText(R.string.cargando_datos), true, false);
    }

    /**
     * Elimna ventana de dialogo cuando termine el proceso
     * 
     * @param success
     *            Indica si ha finalizado el proceso
     * 
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
