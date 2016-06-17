package tv7art.com.tv7art;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tv7art.com.tv7art.adaptadores.AdaptadorSerie;
import tv7art.com.tv7art.modelo.Serie;
import tv7art.com.tv7art.networking.HttpAsyncTask;
import tv7art.com.tv7art.networking.HttpError;
import tv7art.com.tv7art.networking.Response;

/**
 * Esta clase instancia y controla el layout  activity_main que es la vista que contiene
 * el gridview y este asu ves muestra las las series es dos columnas, esta clase contiene toda la logica
 * para permitirle al usuario buscar una serie por el criterio titutlo haciendo uso de la clase HttpAsyncTask e implementado el
 * metodo de su interface OnHttpResponse por donde llega el resultado de la comunicacion con el servicio remoto,
 * tambien implemta la logica de un infinite scroll que ira creciendo a medida que el usuario avance si
 * el resultado de la busqueda arroja un numero considerable de series.
 *
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */
public class MainActivity extends AppCompatActivity implements HttpAsyncTask.OnHttpResponse, AbsListView.OnScrollListener, View.OnKeyListener, AdapterView.OnItemClickListener {

    /**atributos de la clase */
    private GridView gridSeries;
    private EditText editTextBuscar;

    private static RelativeLayout progressLayout;//efecto de carga circular al final del gridviewseries
    private ProgressDialog pd = null;

    private AdaptadorSerie adaptadorSerie;//
    private List<Serie> data = new ArrayList();//lista que contendra los items de tipo serie que se van llegado atraves de las busquedas

    private int totalResultados = 0;//numero total de resultados que arroja una consulta
    private int itemCount = 0;

    private boolean estaCargando = true;//bandera para ayudar a determinar cuando cargar mas datos al list data
    private String busqueda ="";//almacena la cadena de busqueda  

    private int tamMaximoSeries = 2000;//tamaño maximo que pude llegar a tener el List data
    private int tamMaximoConsulta = 9;//numero de intems maximo por consulta




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** instanciamos el layout activity_main y referenciamos las vistas qe contiene a su respectivo atributo
         * de clase*/
        setContentView(R.layout.activity_main);
        progressLayout = (RelativeLayout)findViewById(R.id.load_progress_layout);
        editTextBuscar = (EditText)findViewById(R.id.edit_text_buscar);
        editTextBuscar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);//el teclado tendra la opcion de busqueda para el edittextbuscar
        editTextBuscar.setOnKeyListener(this);//ponemos a escuchar el edittextbuscar cada ves que escribe
        gridSeries = (GridView)findViewById(R.id.grid_series);
        gridSeries.setOnItemClickListener(this);//gridview escuchado cuando se da click por un elemento que contiene
        adaptadorSerie = new AdaptadorSerie(this,data);//creamos el adaptadorSerie y le pasamos la referencia de la lista
        gridSeries.setAdapter(adaptadorSerie);//fijamos el adaptador al gridseries
        gridSeries.setOnScrollListener(this);//ponemos el griddview a escuchar cuando el scroll tenga activida
                                            //mandamos this para que la misma clase implemente el metodo
        loadSeries();//la primera ves se cargan las series haciendo una busqueda sin criterio
    }

    public void loadSeries()
    {
        //ejecutamos el progressDialog ya que esta consulta puede tardar unos segundos e indicarle al usuario que hay un proceso en ejecucion
        pd = ProgressDialog.show(this,getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere), true, false);
        //creamos una instancia de la clase HttpAsyncTask y le pasamos los parametros requeridos
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SIN_ESPECIFICAR);
        //ejecutamos el metodo asincrono mandandole como parametro la url con los parametros necesarios para establecer la comunicacion con el servicio remoto
        task.execute(Serie.query_buscar_series + "?language=es&api_key=" + Serie.API_KEY);
    }


    public void loadSeriesConBusqueda()
    {
        //ejecutamos el progressDialog ya que esta consulta puede tardar unos segundos e indicarle al usuario que hay un proceso en ejecucion
        pd = ProgressDialog.show(this,getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere), true, false);
        //creamos una instancia de la clase HttpAsyncTask y le pasamos los parametros requeridos
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SIN_ESPECIFICAR);
        task.execute(Serie.query_buscar_series_titulo + "?query=" + busqueda + "&language=es&api_key=" + Serie.API_KEY);
    }

    /**Implementacion del metodo onResponse de la interface OnHttpResponse de la clase HttpAsyncTask
     * unico metodo para recibir el resultado de la comunicacion con el servicio remoto*/
    @Override
    public void onResponse(Response response)
    {
        if(pd!=null)// si el progressDialog esta activo lo cerramos
        {
            pd.dismiss();
            pd=null;
        }
        progressLayout.setVisibility(View.GONE);//ocultamos el progress layout este o no ya oculto
        if(validateError(response))//validamos el error y el response code para comprobar si el resultado es el esperado
        {
                //logLargeString(response.getMsg());
                if(this.data.isEmpty())//comprbamos el estado de la lista data si esta vacio cargamos los elementos de la consulta
                {
                    cargarDatos(response.getMsg());
                }
                else
                {
                    //si no esta vacio adicionamos los elementos de la nueva consulta
                    cargarMasDatos(response.getMsg());
                }
        }
    }

    /**
     * este metodo contiene la logica para cargar datos la lista data
     * este metodo carga maximo nueve primeros resultados de la consulta a la lista data
     * @param  json String con el resultado de la consulta al servicio remoto
     */
    private void cargarDatos(String json)
    {
        try
        {
            JSONObject jsonPeliculas = new JSONObject(json);//intenta pasar de string a formato JSON
            int tresultados = jsonPeliculas.getInt("total_results");//valor total de resultados de la consulta este dato util para saber cuantas paginas podemos usar sabiendo que el maximo permido es 1000 segun el api de themoviebd
            if(tresultados == 0)
            {
                // si no hay resultados informamos que la consulta no arrojo resultados
                Toast.makeText(this,getResources().getString(R.string.no_se_encontraron_resultados),Toast.LENGTH_LONG).show();
            }
            else
            {
                JSONArray jsonArray = jsonPeliculas.getJSONArray("results");//el campo que necesitamos es un arrayJson
                int tamano = 0;
                if (tresultados > this.tamMaximoSeries)// si el total de items que arrojo la consulta  es mayor que tamano maximo permitido
                {
                    this.totalResultados = this.tamMaximoSeries;//totalresultados es igual al maximo permitido
                    tamano = this.tamMaximoConsulta;//si es mayor que el maximo de series tambien va ha ser mayor que el tamaño maximo consulta
                }
                else
                {
                    //si no es mayor esntonces fijamos como total el total que arrojo la consulta
                    this.totalResultados = tresultados;
                    if(tresultados > tamMaximoConsulta)
                    {
                        //pero si este sigue siendo mayor que el maximo de la consulta pues seguiremos fijando el tamaño como el valor de  tamMaximoConsulta
                        tamano = tamMaximoConsulta;
                    }
                    else
                    {
                        //sino pues el tamano va a ser total que arrojo la consulta
                        tamano = tresultados;
                    }
                }
                //recorremos el jsonArray creando llenado la lista data con las instacias de la clase Serie a los que se le van fijando el valor de sus atributos
                for(int i = 0;i< tamano; i++)
                {
                    JSONObject jso = jsonArray.getJSONObject(i);
                    Serie s = new Serie();
                    s.setImagen_serie(jso.getString(Serie.PARAMETRO_IMG_PRINCIPAL_SERIE));
                    s.setNombre_serie(jso.getString(Serie.PARAMETRO_NOMBRE_SERIE));
                    s.setId_serie(jso.getInt(Serie.PARAMETRO_ID_SERIE));
                    data.add(s);
                }
                adaptadorSerie.notifyDataSetChanged();//se notifica al adaptador de que hubieron cambios para que los muestre
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * este metodo contiene la logica para cargar mas datos la lista data
     * este metodo carga maximo nueve primeros resultados de la consulta a la lista data
     * @param  json String con el resultado de la consulta al servicio remoto
     */
    private void cargarMasDatos(String json)
    {
        try
        {
            JSONObject jsonPeliculas = new JSONObject(json);//intenta pasar de string a formato JSON
            JSONArray jsonArray = jsonPeliculas.getJSONArray("results");//el campo que necesitamos es un arrayJson
            int tamanoActual = data.size();//numero total de series que hay actualmente en la lista data
            int modulo = tamanoActual % 20;//el api themoviebd arroja 20 elementos por pagina entonces sacamos el mod 20

            int tamanoRestante = 20 -modulo;//cuantos faltan para completar la consulta a esa pagina
            int tamano = 0;
            if(tamanoRestante > 9)//como tenemos que mostrar 9 datos por consultas comprabamos si lo que falta es mayora a 9
            {
                tamano = modulo+9;// entonces el tamaño es igual a lo que nos dio el modulo mas 9
            }
            else
            {
                tamano = modulo +tamanoRestante;//si no alcanzan para otro grupo de 9 entonces tamaño es igual a que hay en modulo menos tamañorestante
            }

            //recorremos el jsonArray creando llenado la lista data con las instacias de la clase Serie a los que se le van fijando el valor de sus atributos
            for(int i = modulo;i< tamano; i++)
            {
                JSONObject jso = jsonArray.getJSONObject(i);
                Serie s = new Serie();
                s.setImagen_serie(jso.getString(Serie.PARAMETRO_IMG_PRINCIPAL_SERIE));
                s.setNombre_serie(jso.getString(Serie.PARAMETRO_NOMBRE_SERIE));
                s.setId_serie(jso.getInt(Serie.PARAMETRO_ID_SERIE));
                data.add(s);
            }
            adaptadorSerie.notifyDataSetChanged();//se notifica al adaptador de que hubieron cambios para que los muestre

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * este metodo valida si hay o no algun error en el Response de la consulta al servicio remoto
     * @param  response instancia de la Clase Response donde viene el resultado de la consulta al servicio remoto
     */
    protected boolean validateError(Response response)
    {
        int error = response.getError();
        if(error == HttpError.NO_ERROR){
            int code = response.getCode();
            if(code == 200){
                return true;// si no hay error y el codigoo es igual 200 retornara true de lo contrario retornara false
            }else if(code == 404)
            {
                Toast.makeText(this, R.string.http_error_404, Toast.LENGTH_SHORT).show();
                return  false;
            }else
                Toast.makeText(this, R.string.http_error_server, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (error == HttpError.NO_INTERNET)
        {
            Toast.makeText(this, R.string.http_error_internet, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(error == HttpError.TIMEOUT)
        {
            Toast.makeText(this, R.string.http_error_timeout, Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            Toast.makeText(this, R.string.http_error_server, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * metodo que prepara la consulta al servicio remoto dependiendo de
     * dos tipos de busqueda ; uno con la palabra correspondiente a buscar de acuerdo al criterio de busqueda
     * y otro para cuando no hay un  criterio de busqueda
     * este metodo es usado por invocado por la implementacion del metodo onscroll cuando se llega al final del scroll
     * con el fin de cargar mas datos
     */
    private void actualizarListaSeries()
    {
        int tamano = data.size();
        progressLayout.setVisibility(View.VISIBLE);
        if(tamano < this.totalResultados)//se comprueba si ya se llego al limite permitodo de series en la lista data
        {
            int pagina = (tamano / 20)+1;
            String query;
            if(busqueda== "")//comprobamos que el atributo busqueda este vacio
            {
                query= Serie.query_buscar_series+"?page="+pagina+"&language=es&api_key="+Serie.API_KEY;
            }
            else
            {
                //si buqueda no es vacio entonces la actualizacion de la lista se seguira haciendo sobre el mismo patro de la url
                query= Serie.query_buscar_series_titulo+"?query="+busqueda+"&page="+pagina+"&language=es&api_key="+Serie.API_KEY;
            }

            HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SIN_ESPECIFICAR);
            task.execute(query);//ejecuta el metdo remoto pasando la consulta configurada

        }
        else {
            //si ya se llego al limite se deja de buscar asi el usuario quiera seguir bajando por el scroll
            progressLayout.setVisibility(View.GONE);
            Log.i("total:", data.size() + "");
            //Toast.makeText(this,getResources().getString(R.string.no_hay_mas_datos),Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Metodo que prepara la cadena de busqueda validando en busca de caracteres especiales
     * y de no encontrarlos escarpa el caracter espacio con %20 antes de que pase a los metodos donde
     * se realiza la busqueda
     */
    private void buscar()
    {
        if(editTextBuscar.getText().toString().isEmpty())//si el editText esta vacio se realiza la busqueda con la opcion loadseries()
        {
            //inicializando contadores
            this.totalResultados=0;
            this.itemCount=0;
            this.estaCargando = true;
            //removiendo lass series actuales de la lista data
            data.removeAll(data);
            //notifica al adaptador para que el gridview muestre los cambios
            adaptadorSerie.notifyDataSetChanged();
            busqueda = "";//se fija vacio
            loadSeries();
        }
        else
        {
            // si no esta vacio entonces se valida la cadena con validarBusqueda
            // y si no encuentra caracteres especiales escarpa los espacios en blanco remplazandolos con %20
            //invoca el metodo loadSeriesConBusqueda
            this.busqueda = editTextBuscar.getText().toString();
            if(!validarBusqueda())
            {
                this.busqueda = this.busqueda.replace(" ","%20");
                //inicializando contadores
                this.totalResultados=0;
                this.itemCount=0;
                this.estaCargando=true;
                data.removeAll(data);//removiendo lass series actuales de la lista data
                adaptadorSerie.notifyDataSetChanged();//notifica al adaptador para que el gridview muestre los cambios
                loadSeriesConBusqueda();
            }
            else
            {
                //si encuentra caracteres especiales notifica al usuario
                //termina la ejecucion
                this.busqueda = "";
                Toast.makeText(this,getResources().getString(R.string.caracteres_especiales_no_permitidos),Toast.LENGTH_SHORT).show();
            }
        }

    }//fin del metodo buscar



    /**
     * Metodo que busca caracters especiales en la cadena que hay actualmente en el atributo buscar
     * haciendo uso del patron de busqueda [^A-Za-z0-9 ] por medio de la clase Pattern
     * y realizando la busqueda con la el metodo find de la clase Matcher
     * @return  false(si no encuentra caracters especiales) o true (si encuentra caracteres especiales)
     */
    public boolean validarBusqueda()
    {
        String PATTERN_BUSQUEDA = "[^A-Za-z0-9 ]";
        Pattern pattern = Pattern.compile(PATTERN_BUSQUEDA);

        Matcher matcher = pattern.matcher(this.busqueda);
        return matcher.find();
    }

    /**
     * implementacion del metodo OnItemClickListener de la clase AdapterView
     * esta  funcion es invocada al hacer click sobre un elemento en el gridview
     * esta implementacion crea un Intent  para invocar una instancia de el activity
     * DetalleSerieActivity donde se mostrara el detalle de la serie
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this,DetalleSerieActivity.class);
        intent.putExtra("serie",data.get(position));//se pasa la serie seleccionada al activity DetalleSerieActivity  de acuerdo a la posicion
        startActivity(intent);//se invoca el activity
    }

    /** este metodo no lo implentaremos pereo era obligacion sobreescribirlo al implemtar
     * AbsListView.OnScrollListener
     */
    @Override
    public void onScrollStateChanged(AbsListView arg0, int scrollState)
    {

    }


    /**
     * implementacion del metodo OnScrollListener de la clase AbsListView
     * esta implementacion contiene la logica del scroll infinito
     * que al llegar al final de scroll carga mas elentos al gridview
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        //siempre que totalItemcount(numero de items actuales) aumenta en numero entra a este
        //condicional la varialbe esta cargando se fija false itemcount se iguala totalItemcount
        if (estaCargando && (totalItemCount > itemCount))
        {
            estaCargando = false;//
            itemCount = totalItemCount;
        }

        //como en la funcion anterior se cambio estacargando a false este condicional se cumple en el momento
        //totalItemcount (numero de elementos) que en este caso seria nuestro ultimo elemento menos
        //la cantidad de items visibles este numero debe ser menor o igual al primer elemento visible en pantalla
        //con esto nos aseguramos que estamos al final del scrollview
        if (!estaCargando && (totalItemCount - visibleItemCount)<= firstVisibleItem)
        {
            actualizarListaSeries();//actualizamos la lista de series en busca de mas elementos para incluir a la lista
            estaCargando = true;//esta cargando se fija nuevamente true y se vuelve a repetir el proceso
        }
    }

    /**
     * implementacion del metodo OnKeyListener de la clase View
     * esta metodo es invocado cada ves que se recibe una entrada en el edittexserie
     * detectanto un enter_action
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextBuscar.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            buscar();//al detectar enter se invoca el metodo buscar()
            return true;
        }
        return false;
    }
}//fin de la clase MainActivity
