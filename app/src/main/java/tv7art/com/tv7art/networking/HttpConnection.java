package tv7art.com.tv7art.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Esta Clase es la encargada de la conexion, solicitud y respuesta a un servicio remoto
 *  ya sea por metodo POST, GET, DELETE, PUT dependiendo del servicio retornara un string con
 *  el recibido que puede ser un objeto json, xml etc dependiendo de como este configurado el servicio
 *  para este proyecto solo se implemento el metodo GET
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */
public class HttpConnection {

    /**atributos TimeOut
     * */
    private static final int READ=20000;//20 segundos;
    private static final int CONNECT=10000;//10 segundos;

    /**
     * Metodo de lectura Get
     * @param url url del servicio remoto, esta url contiene los parametros necesarios para consumir el servicio
     * @return una instancia de la clase Response que contiene un mensaje con la infomación recibida y un
     * Response code.
     *
     */
    public Response get(String url) throws IOException {
        URL u =  new URL(url);

        HttpURLConnection con = (HttpURLConnection) u.openConnection();

        con.setRequestMethod("GET");//Tipo de metodo de la conexión
        con.setDoInput(true);//habilita el response code

        con.setReadTimeout(READ);//fijando el Tiempo de espera de lectura
        con.setConnectTimeout(CONNECT);//fijando el Tiempo de espera de la conexión

        con.connect();

        InputStream in = con.getInputStream();
        Response response =  new Response(streamToString(in), con.getResponseCode());
        return response;
    }


    /**
     * Metodo para pasar de InputStream a String
     * @param in  InputString para convertir a string
     * @return String con el contenido leido del inputstream.
     *
     */
    private String streamToString(InputStream in) throws  IOException
    {
        StringBuilder rta = new StringBuilder();
        String rline = "";
        BufferedReader br= new BufferedReader(new InputStreamReader(in));

        try
        {
            while ((rline= br.readLine())!= null)//leemos linea a linea hasta llegar al final
            {
                rta.append(rline);//vamos adjuntando en el stringbuilder;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rta.toString();//retornamos la informacion del stringbuilder en un string
    }
}