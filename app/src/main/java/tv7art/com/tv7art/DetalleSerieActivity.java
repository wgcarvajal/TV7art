package tv7art.com.tv7art;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import tv7art.com.tv7art.adaptadores.AdaptadorListaEpisodios;
import tv7art.com.tv7art.adaptadores.AdaptadorSpinnerTemporada;
import tv7art.com.tv7art.modelo.Episodio;
import tv7art.com.tv7art.modelo.Serie;
import tv7art.com.tv7art.modelo.Temporada;
import tv7art.com.tv7art.networking.HttpAsyncTask;
import tv7art.com.tv7art.networking.HttpError;
import tv7art.com.tv7art.networking.Response;

public class DetalleSerieActivity extends AppCompatActivity implements View.OnClickListener ,HttpAsyncTask.OnHttpResponse, AdapterView.OnItemSelectedListener {
    private Serie serie;
    private Button btnAtras;
    private ImageView imgDetalleSerie;
    private TextView txtTituloDetalleSerie;
    private TextView txtGeneroDetalleSerie;
    private TextView txtActoresDetalleSerie;
    private Spinner spinnerTemporadas;
    private AdaptadorSpinnerTemporada adaptadorSpinnerTemporada;
    private AdaptadorListaEpisodios adaptadorListaEpisodios;
    private ListView listaEpisodios;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_serie);
        serie= (Serie)getIntent().getExtras().getSerializable("serie");
        imgDetalleSerie = (ImageView)findViewById(R.id.img_detalle_serie);
        txtTituloDetalleSerie = (TextView)findViewById(R.id.txt_titulo_detalle_serie);
        txtGeneroDetalleSerie = (TextView)findViewById(R.id.txt_genero_detatalle_serie);
        txtActoresDetalleSerie = (TextView)findViewById(R.id.txt_actores_detalle_serie);
        spinnerTemporadas = (Spinner)findViewById(R.id.spin_temporada_detalle_serie);
        listaEpisodios = (ListView)findViewById(R.id.list_view_episodios);
        btnAtras = (Button)findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(this);
        spinnerTemporadas.setOnItemSelectedListener(this);

        loadDatos();
    }

    public void loadDatos()
    {
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_ACTORES);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "/credits?language=es&api_key=" + Serie.API_KEY);
    }

    public void loadGenerosTemporadas()
    {
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SERIE);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "?language=es&api_key=" + Serie.API_KEY);
    }

    public void loadEpisodiosPrimeraTemporada()
    {
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_EPISODIOS_PRIMER_TEMPORADA);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "/season/1?language=es&api_key=" + Serie.API_KEY);
    }

    public void loadEpisodiosTemporada(int temporada)
    {
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_EPISODIOS);
        task.execute(Serie.query_buscar_serie_id + "/" + serie.getId_serie() + "/season/"+temporada+"?language=es&api_key=" + Serie.API_KEY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_atras:
                    finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResponse(Response response)
    {
        if(validarError(response))
        {
            if(response.getConsulta() == Serie.CONSULTA_ACTORES)
            {
                cargarActores(response.getMsg());
            }
            else if (response.getConsulta() == Serie.CONSULTA_SERIE)
            {
                cargarGenerosTemporadas(response.getMsg());

            }
            else if (response.getConsulta() == Serie.CONSULTA_EPISODIOS_PRIMER_TEMPORADA)
            {
                cargarEpisodiosPrimeraTemporada(response.getMsg());
            }
            else if (response.getConsulta() == Serie.CONSULTA_EPISODIOS)
            {
                cargarEpisodiosTemporada(response.getMsg());
            }
        }
    }

   public void cargarActores(String json)
   {
       try
       {
           JSONObject jsonSerie = new JSONObject(json);
           JSONArray jsonArrayCast = jsonSerie.getJSONArray(Serie.PARAMETRO_CAST);
           List<String> actores = new ArrayList();

           for(int i = 0; i<jsonArrayCast.length();i++)
           {
               JSONObject jactor = jsonArrayCast.getJSONObject(i);
               actores.add(jactor.getString(Serie.PARAMETRO_NOMBRE_ACTOR));
           }
           serie.setActores(actores);
           this.loadGenerosTemporadas();
       }
       catch (JSONException e)
       {
           e.printStackTrace();
       }
   }
    public void cargarGenerosTemporadas(String json)
    {
        try
        {
            JSONObject jsonSerie = new JSONObject(json);
            JSONArray jsonArrayGeneros = jsonSerie.getJSONArray(Serie.PARAMETRO_GENEROS_SERIE);
            int numeroTemporadas = jsonSerie.getInt(Serie.PARAMETRO_NUMERO_TEMPORADAS);
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
                t.setNombre("Temporada " + (i + 1));
                temporadas.add(t);
            }

            serie.setGeneros(generos);
            serie.setTemporadas(temporadas);
            if(numeroTemporadas > 0)
            {
                loadEpisodiosPrimeraTemporada();
            }
            else
            {
                cargarDatosVista();
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void cargarEpisodiosPrimeraTemporada(String json)
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
                e.setNumEpisodio(i + 1);
                e.setNombre(jepisodio.getString(Serie.PARAMETRO_NOMBRE_EPISODIO));
                episodios.add(e);
            }

            serie.getTemporadas().get(0).setEpisodios(episodios);
            cargarDatosVista();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

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
            cargarListaEpisodios();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void cargarDatosVista()
    {
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

        if(serie.getTemporadas().size()>0)
        {
            List<Episodio> episodios = new ArrayList();
            for(Episodio e : serie.getTemporadas().get(0).getEpisodios())
            {
                Episodio ep = new Episodio();
                ep.setNombre(e.getNombre());
                ep.setNumEpisodio(e.getNumEpisodio());
            }
            adaptadorListaEpisodios = new AdaptadorListaEpisodios(this,episodios);
            listaEpisodios.setAdapter(adaptadorListaEpisodios);
        }
        txtActoresDetalleSerie.setText(actores);
        txtGeneroDetalleSerie.setText(generos);
    }

    private void cargarListaEpisodios()
    {
        adaptadorListaEpisodios.set_data(serie.getTemporadas().get(spinnerTemporadas.getSelectedItemPosition()).getEpisodios());
        listaEpisodios.setVisibility(View.VISIBLE);
    }


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

    public void cargarEpisodiosTemporadaSeleccionada(int posicion)
    {
        listaEpisodios.setVisibility(View.GONE);
        if(serie.getTemporadas().get(posicion).getEpisodios()== null )
        {
            loadEpisodiosTemporada(posicion + 1);
        }
        else
        {
            cargarListaEpisodios();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Log.i("seleccion:", serie.getTemporadas().get(position).getNombre()+"");
        cargarEpisodiosTemporadaSeleccionada(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }


}
