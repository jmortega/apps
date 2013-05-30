package com.proyecto.utility;

import android.os.AsyncTask;

public class ImportExportAsyncTask extends AsyncTask<String, Void, Boolean> {

    /**
     * Interfaz que deberian implementar las clases que la quieran usar Sirve
     * como callback una vez termine la tarea asincrona
     *
     */
    public interface ImportExportAsyncTaskResponder {
            public void backupLoaded(Boolean result);
    }

    private ImportExportAsyncTaskResponder responder;

    /**
     * Constructor. Es necesario que nos pasen un objeto para el callback
     *
     * @param responder
     */
    public ImportExportAsyncTask(ImportExportAsyncTaskResponder responder) {
            this.responder = responder;
    }

    /**
     * Ejecuta el proceso en segundo plano
     */
    @Override
    protected Boolean doInBackground(String... datos) {
            Boolean resultado = null;
            try {
                    String entrada = (String) datos[0];

                    if (entrada.equals("exportar")) {
                            resultado = DataImportExport.exportar(false);
                    } else {
                            resultado = DataImportExport.importar(false);
                    }
            } catch (Exception e) {
                    return false;
            }

            return resultado;
    }

    /**
     * Se ha terminado la ejecucion comunicamos el resultado al llamador
     */
    @Override
    protected void onPostExecute(Boolean result) {
            if (responder != null) {
                    responder.backupLoaded(result);
            }
    }

}


