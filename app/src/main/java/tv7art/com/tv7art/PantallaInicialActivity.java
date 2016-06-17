package tv7art.com.tv7art;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Esta clase instancia y controla el layout  activity_pantalla_inicial
 * que es la vista que se muestra al abrir la aplicacion
 * el ListView del layout  DetalleSerieActivity.
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */
public class PantallaInicialActivity extends AppCompatActivity
{
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicial);//instanciamos el layout  activity_pantalla_inicial
        context= this;//creamos una referencia al entorno actual de la aplicación para poder interactuar con esta
                        //desde la clase IrActivityPrincipal ya qe esta hace uso de un metodo Asíncrono.

        IrActiviyPrincipal ir= new IrActiviyPrincipal();//instaciamos la clase IrActiviyPrincipal
        ir.execute();//ejecutamos el metodo asíncrono.
    }

    class IrActiviyPrincipal extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Thread.sleep(3000);//esperamos tres segundos antes de continuar pero la ejecucion del hilo principal
                                    //sigue normal esto con el objetivo de mostrar el logo de la aplicacion
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Intent intent= new Intent(context,MainActivity.class);//Creamos un Intent para invocar al MainActivity
            startActivity(intent);//iniciamos el Main Activity
            finish();//terminamos la ejecucion de de PantallaInicialActivity
        }
    }
}//fin de la clase PantallaInicialActivity
