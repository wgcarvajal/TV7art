package tv7art.com.tv7art.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Esta Clase es Extiende de la clase AsyncTask para hacer comunicación asíncrona usando los metodos de la clase
 * clase HttpConnecion ya que estos metodos se sincronizan con servicios remotos lo que causarian bloqueo de la
 * del hilo principal, eta clase contiene una interfaz OnHttpResponse que debe ser implementada por la clase que haga uso de esta.
 * ya que por esta via se enviara el la instancia de la clase Response con la informacion que retorno el servicio remoto
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */

public class HttpAsyncTask extends AsyncTask<String, Void, Response>
{
    /**atributos con el indice que va ha representar hacer cada metodo de conexión
     * */
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;

    /**
     * Interfaz para la comunicacion entre la clase HttpAsyncTask y la clase que haga uso e implemente el
     * metodo onResponse ya que por esta via se le devolvera el resultado de la comunicacon con el servicio remoto
     *
     */
    public interface OnHttpResponse
    {
        void onResponse(Response response);
    }

    /** atributos de la clase */
    int method;
    OnHttpResponse response;
    Context context;
    int consulta;

    /**
     * Constructor parmetrizado de la clase HttpAsyncTask
     * @param method tipo de metodo que se usara para establecer la conexion
     * @param response referencia de la clase que implemento el metodo
     * @param  context referencia del contexto o entorno de la aplicacion.
     * @param consulta valor que identifica el tipo de consulta para cuando se devuelva el resultado este valor es
     * retornado junto con el y asi la clase que invoco al metodo
     * sepa a que consulta pertenece ya que todas las peticiones regresan por la misma
     * via la cual es la implementacion del metodo onresponse.
     * */
    public HttpAsyncTask(int method, OnHttpResponse response , Context context,int consulta)
    {
        this.method = method;
        this.response = response;
        this.context = context;
        this.consulta = consulta;
    }

    /**
     * este metodo se ejecuta por debajo y es en este metodo donde se hace la comunicación con el servicio
     * remoto haciendo uso de la clase HttpConection
     * @return el Response con el resultado de la petición
     */
    @Override
    protected Response doInBackground(String... params)
    {
        HttpConnection con = new HttpConnection();

        Response response=null;

        if(isConnected())
        {
            try {
                switch (method) {
                    case GET:
                        response = con.get(params[0]);//para esta aplicacion solo se usara el metodo GET.
                        break;
                    case POST:

                        break;
                    case PUT:

                        break;
                    case DELETE:

                        break;
                }
                response.setError(HttpError.NO_ERROR);
            }catch (SocketTimeoutException e){
                response = new Response(HttpError.TIMEOUT);
            }catch(IOException e){
                response = new Response(HttpError.SERVER);
            }
        }
        else
        {
            response = new Response(HttpError.NO_INTERNET);
        }
        return response;
    }

    /**
     * este metodo se ejecuta cuanto ya se obtiene el resultado de la peticion
     * @param s Response con el resultado a quien se le fija el tipo de consulta para que
     *          la clase que implementa el metodo onResponse sepa el tipo correspondiente
     */
    @Override
    protected void onPostExecute(Response s)
    {
        s.setConsulta(consulta);
        this.response.onResponse(s);
    }

    /**
     * Este metodo comprueba si hay conexiones activas ene l dispositvo haciendo uso
     * de los servicios que provee android
     * @return devuelve true si hay conexiones activas y false si no las hay.
     */
    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork!=null)        {

            return activeNetwork.isConnectedOrConnecting();
        }
        return false;

    }
}//fin de la clase HttpAsyncTask

