package tv7art.com.tv7art;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tv7art.com.tv7art.adaptadores.AdaptadorListaEpisodios;
import tv7art.com.tv7art.adaptadores.AdaptadorSpinnerTemporada;
import tv7art.com.tv7art.modelo.Episodio;
import tv7art.com.tv7art.modelo.Serie;
import tv7art.com.tv7art.modelo.Temporada;
import tv7art.com.tv7art.networking.HttpAsyncTask;
import tv7art.com.tv7art.networking.HttpError;
import tv7art.com.tv7art.networking.Response;

/**
 * Esta clase instancia y controla el layout  activity_detalle_serie que es la vista que contiene
 * el detalle de una serie en particular esta clase tiene la logica necesaria para cargar
 * la informacion de la serie y mostrarla en la vista
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */

public class DetalleSerieActivity extends AppCompatActivity implements View.OnClickListener ,HttpAsyncTask.OnHttpResponse, AdapterView.OnItemSelectedListener
{
    /**atributos de la clase */
    private Serie serie;
    private Button btnAtras;//referencia al Button que nos permitira devolvernos a la vista anterior
    private ImageView imgDetalleSerie;//referencia al ImageView donde se pondra la imagen representativa de la serie
    private TextView txtTituloDetalleSerie;//referencia al Textview donde se pondra el titulo de la serie
    private TextView txtGeneroDetalleSerie;//referencia al Textview donde se pondra la información de generos de la serie
    private TextView txtActoresDetalleSerie;//referencia al Textview donde se pondra los actores que participan en la serie
    private ScrollView scrollDetalleSerie;
    private Spinner spinnerTemporadas;
    private AdaptadorSpinnerTemporada adaptadorSpinnerTemporada;
    private AdaptadorListaEpisodios adaptadorListaEpisodios;
    private ListView listaEpisodios;
    private ProgressDialog pd = null;
    private static RelativeLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_serie);//instanciando activity_detalle_serie
        serie= (Serie)getIntent().getExtras().getSerializable("serie");//recibiendo el parametro serie enviado por el MainActivity

        /**obteniendo las referencias de las vistas del layout activity_detalle_serie*/
        imgDetalleSerie = (ImageView)findViewById(R.id.img_detalle_serie);
        progressLayout = (RelativeLayout)findViewById(R.id.load_progress_layout);
        txtTituloDetalleSerie = (TextView)findViewById(R.id.txt_titulo_detalle_serie);
        txtGeneroDetalleSerie = (TextView)findViewById(R.id.txt_genero_detatalle_serie);
        txtActoresDetalleSerie = (TextView)findViewById(R.id.txt_actores_detalle_serie);
        spinnerTemporadas = (Spinner)findViewById(R.id.spin_temporada_detalle_serie);
        scrollDetalleSerie = (ScrollView)findViewById(R.id.scroll_detalle_serie);
        listaEpisodios = (ListView)findViewById(R.id.list_view_episodios);
        btnAtras = (Button)findViewById(R.id.btn_atras);

        btnAtras.setOnClickListener(this);//boton atras escuchando un evento click
        spinnerTemporadas.setOnItemSelectedListener(this);//spinner temporadas escuchando por la seleccion de una opcion

        loadDatos();
    }

    /**
     * este metodo invoca la consulta inicial buscando los actores de la serie por medio del id de la serie
     * invocando el metodo asíncrono de la clase HttpAsyncTask
     * ocultando la vista que contiene los detalles de la seria por prevencion en caso de que
     * ocurra un error con la comunicación con el servicio remoto, si esto ocurre se mostrara
     * el respectivo mensaje del problema y solo estara habilitado el boton atras
     * con la bandera  CONSULTA_ACTORES que ayudara a identificar la accion a realizar cuando retorne el resultado
     */
    public void loadDatos()
    {
        //ejecutamos el progressDialog ya que esta consulta puede tardar unos segundos e indicarle al usuario que hay un proceso en ejecucion
        pd = ProgressDialog.show(this,getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere));
        scrollDetalleSerie.setVisibility(View.GONE);//ocultando el scrollview que contiene la mayoria de las vistas con el detalle de la serie
        listaEpisodios.setVisibility(View.GONE);//ocultando la lista de episodios de la serie
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_ACTORES);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "/credits?language=es&api_key=" + Serie.API_KEY);
    }

    /**
     * este metodo invoca la consulta para buscando por identificador de la serie
     * * invocando el metodo asíncrono de la clase HttpAsyncTask
     * con la bandera  CONSULTA_SERIE que ayudara a identificar la accion a realizar cuando retorne el resultado
     */
    public void loadGenerosTemporadas()
    {
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SERIE);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "?language=es&api_key=" + Serie.API_KEY);
    }

    /**
     * este metodo invoca la consulta para buscando por identificador de la serie y la primera temporada de la serie
     * * invocando el metodo asíncrono de la clase HttpAsyncTask
     * con la bandera  CONSULTA_EPISODIOS_PRIMER_TEMPORADA que ayudara a identificar la accion a realizar cuando retorne el resultado
     */
    public void loadEpisodiosPrimeraTemporada()
    {
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_EPISODIOS_PRIMER_TEMPORADA);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "/season/1?language=es&api_key=" + Serie.API_KEY);
    }

    /**
     * este metodo invoca la consulta para buscando por identificador de la serie y por el numero de la temporada
     * * invocando el metodo asíncrono de la clase HttpAsyncTask
     * con la bandera  CONSULTA_EPISODIOS que ayudara a identificar la accion a realizar cuando retorne el resultado
     */
    public void loadEpisodiosTemporada(int temporada)
    {

        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_EPISODIOS);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "/season/"+temporada+"?language=es&api_key=" + Serie.API_KEY);
    }

    /**
     * Implementacion View.OnClickListener para el boton atras
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            //asegura que el envento onclick sea invocado por el btn_atras
            case R.id.btn_atras:
                    finish();//finaliza la ejecucion del activity
                break;
            default:
                break;
        }
    }

    /**Implementacion del metodo onResponse de la interface OnHttpResponse de la clase HttpAsyncTask
     * unico metodo para recibir el resultado de la comunicacion con el servicio remoto
     * @param response contiene el resultado de la comunicacion con el servicio remoto
     * */
    @Override
    public void onResponse(Response response)
    {
        //valida que no se presentaran errores durante la comunicacion con el servicio remoto
        if(validarError(response))
        {
            if(response.getConsulta() == Serie.CONSULTA_ACTORES)//si el metodo que invoco la seria es para consultar actores se cumple la condicion
            {
                cargarActores(response.getMsg());
            }
            else if (response.getConsulta() == Serie.CONSULTA_SERIE)//si el metodo que invoco la seria es para consultar serie se cumple la condicion
            {
                cargarGenerosTemporadas(response.getMsg());

            }
            else if (response.getConsulta() == Serie.CONSULTA_EPISODIOS_PRIMER_TEMPORADA)//si el metodo que invoco la seria es para consultar episodios primer temporada se cumple la condicion
            {

                cargarEpisodiosPrimeraTemporada(response.getMsg());
            }
            else if (response.getConsulta() == Serie.CONSULTA_EPISODIOS)//si el metodo que invoco la seria es para consultar episodios temporada se cumple la condicion
            {
                cargarEpisodiosTemporada(response.getMsg());
            }
        }
        else
        {
            //si se produce un error se cierran los efecto de carga circulares
            if(pd!=null)
            {
                pd.dismiss();
                pd=null;
            }
            progressLayout.setVisibility(View.GONE);
        }
    }

    /**
     * metodo para cargar los actores en la lista actores del atributo serie
     * @param json string con el resultado de la consulta
     * */
   public void cargarActores(String json)
   {
       try
       {
           JSONObject jsonSerie = new JSONObject(json);//pasando a formato JSON
           JSONArray jsonArrayCast = jsonSerie.getJSONArray(Serie.PARAMETRO_CAST);//obteniendo el jsonArray de actores de la serie
           List<String> actores = new ArrayList();

           for(int i = 0; i<jsonArrayCast.length();i++)
           {
               JSONObject jactor = jsonArrayCast.getJSONObject(i);
               actores.add(jactor.getString(Serie.PARAMETRO_NOMBRE_ACTOR));
           }
           serie.setActores(actores);//agregando la lista actores a la lista de actores que contiene el atributo serie
           //la idea es ir obteniendo los datos requeridos y cuando se obtengan todos se mostraran en la vista al tiempo
           this.loadGenerosTemporadas();//invocamos el metodo que obtiene temporadas y generos de la serie
       }
       catch (JSONException e)
       {
           e.printStackTrace();//en caso de que se encuentren errores leyendo los JSON
       }
   }
    /**
     * metodo para cargar los generos y la lista de tempordas en la listas generos y temporadas que estan en el atributo serie
     * @param json string con el resultado de la consulta
     * */
    public void cargarGenerosTemporadas(String json)
    {
        try
        {
            JSONObject jsonSerie = new JSONObject(json);//pasando a formato JSON
            JSONArray jsonArrayGeneros = jsonSerie.getJSONArray(Serie.PARAMETRO_GENEROS_SERIE);//obteniendo ArrayJson que contiene la lista de generos de la serie
            int numeroTemporadas = jsonSerie.getInt(Serie.PARAMETRO_NUMERO_TEMPORADAS);//obteniendo el numero total de temporadas de la serie, este dato
                                                                                        //ya podemos llenar la listaTemporadas ya que siempre se llaman Temporada numerTemporada
            List<String> generos = new ArrayList();
            List<Temporada> temporadas = new ArrayList();

            for(int i = 0; i<jsonArrayGeneros.length();i++)
            {
                JSONObject jgenero = jsonArrayGeneros.getJSONObject(i);
                generos.add(jgenero.getString(Serie.PARAMETRO_NOMBRE_GENERO));
            }
            for(int i = 0; i<numeroTemporadas; i++)
            {
                Temporada t = new Temporada();
                t.setNumTemporada(i + 1);
                t.setNombre("Temporada " + (i + 1));//como el indice arranca en cero entonces se suma 1 ya que la temporada 0 no existe
                temporadas.add(t);
            }
            serie.setGeneros(generos);//agregando la lista de generos a la lista de generos que contiene el atributo serie
            serie.setTemporadas(temporadas);//agregando la lista de temporadas a la lista de temporadas que contiene el atributo serie
            if(numeroTemporadas > 0)
            {
                //con el condicional estamos seguros que se encontro almenos una temporada procedemos a cargar los episodios
                //de la primera temporada
                loadEpisodiosPrimeraTemporada();
            }
            else
            {
                //si no hay temporadas entonces procedesmos a cargar los valores correspondientes  la vista con los datos que se tienen
                cargarDatosVista();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();//en caso de que se encuentren errores leyendo los JSON
        }
    }

    /**
     * metodo para cargar los episodios de la primera temporada en la lista de episodios contenida en una temporada que
     * a su ves esta contenida en la lista de temporadas del atributo serie
     * @param json string con el resultado de la consulta
     * */
    public void cargarEpisodiosPrimeraTemporada(String json)
    {
        try
        {
            JSONObject jsonSerie = new JSONObject(json);//pasando a formato JSON
            JSONArray jsonArrayEpisodios = jsonSerie.getJSONArray(Serie.PARAMETRO_EPISODIOS);
            List<Episodio> episodios = new ArrayList();

            for(int i = 0 ; i< jsonArrayEpisodios.length();i++)
            {
                JSONObject jepisodio =  jsonArrayEpisodios.getJSONObject(i);
                Episodio e = new Episodio();
                e.setNumEpisodio(i + 1);
                e.setNombre(jepisodio.getString(Serie.PARAMETRO_NOMBRE_EPISODIO));
                episodios.add(e);
            }

            serie.getTemporadas().get(0).setEpisodios(episodios);//adicionando la lista de episodios en Temporada correspondiente que esta contenida en la lista de temporadas del atributo serie
            cargarDatosVista();//como ya se tiene los datos requeridos se procede a cargar la vista
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * metodo para cargar los episodios de una temporada en particular en la lista de episodios contenida en una temporada que
     * a su ves esta contenida en la lista de temporadas del atributo serie
     * @param json string con el resultado de la consulta
     * */
    public void cargarEpisodiosTemporada(String json)
    {
        try
        {
            JSONObject jsonSerie = new JSONObject(json);
            JSONArray jsonArrayEpisodios = jsonSerie.getJSONArray(Serie.PARAMETRO_EPISODIOS);
            List<Episodio> episodios = new ArrayList();

            for(int i = 0 ; i< jsonArrayEpisodios.length();i++)
            {
                JSONObject jepisodio =  jsonArrayEpisodios.getJSONObject(i);
                Episodio e = new Episodio();
                e.setNumEpisodio(i+1);
                e.setNombre(jepisodio.getString(Serie.PARAMETRO_NOMBRE_EPISODIO));
                episodios.add(e);
            }

            serie.getTemporadas().get(spinnerTemporadas.getSelectedItemPosition()).setEpisodios(episodios);
            cargarListaEpisodios();//como ya hay unos episodios en la lista del adaptadorepisodio estos se tiene que borrar y volver a cargar los que se pidieron
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * metodo que carga los datos en la vista correspondiente
     * para la carga de las imagen se usa la libreria de Picassso
     * */
    public void cargarDatosVista()
    {
        if(pd!=null)
        {
            pd.dismiss();
            pd=null;
        }
        txtTituloDetalleSerie.setText(serie.getNombre_serie());

        Picasso.with(this)
                .load(Serie.PATH_IMG_W154 + serie.getImagen_serie())
                .into(imgDetalleSerie);

        String actores = "Actores: ";
        String generos = "Generos: ";

        for(String actor : serie.getActores())
        {
            actores = actores + actor+", ";
        }

        for(String genero: serie.getGeneros())
        {
            generos = generos + genero + ", ";
        }
        adaptadorSpinnerTemporada = new AdaptadorSpinnerTemporada(this,R.layout.template_spinner_temporada,serie.getTemporadas());
        spinnerTemporadas.setAdapter(adaptadorSpinnerTemporada);

        if(serie.getTemporadas().size()>0)//si hay temporadas se procede a cargar los episodios en la listaEpisodios
        {
            /** se hace una copia de los episodios de la primera temporada para no tener problemas
             * de referencias cuando se soliciten otras temporadas y estos episodios tengan que ser borrados
             * no se borren en la lista original
             * */
            List<Episodio> episodios = new ArrayList();
            for(Episodio e : serie.getTemporadas().get(0).getEpisodios())
            {
                Episodio ep = new Episodio();
                ep.setNombre(e.getNombre());
                ep.setNumEpisodio(e.getNumEpisodio());
            }
            //se instancia adapterListaEpisodios pasandole la copia de los episodios de la primera temporda
            adaptadorListaEpisodios = new AdaptadorListaEpisodios(this,episodios);
            listaEpisodios.setAdapter(adaptadorListaEpisodios);
        }
        /**fijando los datos en sus respectivas vistas y habilitando el scrollview y listaEpisodios que teniamos ocultos
         * ya que ahora ya se pueden mostrar*/
        txtActoresDetalleSerie.setText(actores);
        txtGeneroDetalleSerie.setText(generos);
        scrollDetalleSerie.setVisibility(View.VISIBLE);
        listaEpisodios.setVisibility(View.VISIBLE);
    }


    /**este metodo carga la lista de episodios cuando esta ya tiene episodios contenidos
     * esta clase hace uso de set_data de la calse AdaptadorEpisodios ya que este metodo
     * realiza el borrado y saca una copia de la lista que se le envia para no tener problemas
     * de referencias cada ves que se haga este, habilita la vista listaEpisodios que estba oculta*/
    private void cargarListaEpisodios()
    {
        adaptadorListaEpisodios.set_data(serie.getTemporadas().get(spinnerTemporadas.getSelectedItemPosition()).getEpisodios());
        listaEpisodios.setVisibility(View.VISIBLE);
        listaEpisodios.setSelection(0);
        progressLayout.setVisibility(View.GONE);
    }


    /**
     * este metodo valida si hay o no algun error en el Response de la consulta al servicio remoto
     * @param  response instancia de la Clase Response donde viene el resultado de la consulta al servicio remoto
     */
    protected boolean validarError(Response response)
    {
        int error = response.getError();
        if(error == HttpError.NO_ERROR){
            int code = response.getCode();
            if(code == 200){
                return true;
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
     *metodo que continua el proceso para cargar los episodios de una temporada
     * seleccionada
     */
    public void cargarEpisodiosTemporadaSeleccionada(int posicion)
    {
        listaEpisodios.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        if(serie.getTemporadas().get(posicion).getEpisodios()== null )//si no hay episodios actualmente en la lista episodio de la temporada correspondiente
        {
            loadEpisodiosTemporada(posicion + 1);//llamamos al metodo que inicia la busqueda al servicio remoto
        }
        else
        {
            cargarListaEpisodios();//sino simplemente invocamos al metodo que carga la lista
        }
    }

    /**
     *implementacion del metodo AdapterView.OnItemSelectedListener
     * que esta escuchando cuando se selecciona una nueva temporada en el spinner de temporadas
     * invocando el metodo cargarEpisodiosTemporadaSeleccionada pasando como parametro la posicion del elemento que
     * selecciono
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Log.i("seleccion:", serie.getTemporadas().get(position).getNombre()+"");
        cargarEpisodiosTemporadaSeleccionada(position);
    }

    /** este metodo no lo implentaremos pereo era obligacion sobreescribirlo al implemtar
     * AdapterView.OnItemSelectedListener
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }


}//fin de Activity DetalleSerieActivity
