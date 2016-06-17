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
 * Esta clase es la encargada de mostrar el contenido (numero de episodio e nombre del episodio) de la temporada en
 * el ListView del layout  DetalleSerieActivity.
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */

public class MainActivity extends AppCompatActivity implements HttpAsyncTask.OnHttpResponse, AbsListView.OnScrollListener, View.OnKeyListener, AdapterView.OnItemClickListener {

    private GridView gridSeries;
    private EditText editTextBuscar;

    private static RelativeLayout progressLayout;
    private ProgressDialog pd = null;

    private AdaptadorSerie adaptadorSerie;
    private List<Serie> data = new ArrayList();
    private boolean userScrolled = false;
    private int totalResultados = 0;

    private int itemCount = 0;
    private boolean isLoading = true;
    private String busqueda ="";

    private int tamMaximoSeries = 2000;
    private int tamMaximoConsulta = 9;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressLayout = (RelativeLayout)findViewById(R.id.load_progress_layout);
        editTextBuscar = (EditText)findViewById(R.id.edit_text_buscar);
        editTextBuscar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editTextBuscar.setOnKeyListener(this);
        gridSeries = (GridView)findViewById(R.id.grid_series);
        gridSeries.setOnItemClickListener(this);
        adaptadorSerie = new AdaptadorSerie(this,data);
        gridSeries.setAdapter(adaptadorSerie);
        gridSeries.setOnScrollListener(this);
        loadSeries();

    }
    public void loadSeries()
    {
        pd = ProgressDialog.show(this,getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere), true, false);
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SIN_ESPECIFICAR);
        task.execute(Serie.query_buscar_series + "?language=es&api_key=" + Serie.API_KEY);
    }

    public void loadSeriesConBusqueda()
    {
        //Toast.makeText(this,busqueda,Toast.LENGTH_SHORT).show();
        pd = ProgressDialog.show(this,getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere), true, false);
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SIN_ESPECIFICAR);
        task.execute(Serie.query_buscar_series_titulo + "?query=" + busqueda + "&language=es&api_key=" + Serie.API_KEY);
    }

    @Override
    public void onResponse(Response response)
    {
        if(pd!=null)
        {
            pd.dismiss();
            pd=null;
        }
        progressLayout.setVisibility(View.GONE);
        if(validateError(response))
        {
                //logLargeString(response.getMsg());
                if(this.data.isEmpty())
                {
                    cargarDatos(response.getMsg());
                }
                else
                {
                    cargarMasDatos(response.getMsg());
                }
        }
        else
        {

        }
    }

    private void cargarDatos(String json)
    {
        try
        {
            JSONObject jsonPeliculas = new JSONObject(json);
            int tresultados = jsonPeliculas.getInt("total_results");
            if(tresultados == 0)
            {
                Toast.makeText(this,getResources().getString(R.string.no_se_encontraron_resultados),Toast.LENGTH_LONG).show();
            }
            else
            {
                JSONArray jsonArray = jsonPeliculas.getJSONArray("results");
                int tamano = 0;
                if (tresultados > this.tamMaximoSeries)
                {
                    this.totalResultados = this.tamMaximoSeries;
                    tamano = 9;
                }
                else
                {
                    this.totalResultados = tresultados;
                    if(tresultados > tamMaximoConsulta)
                    {
                        tamano = tamMaximoConsulta;
                    }
                    else
                    {
                        tamano = tresultados;
                    }
                }
                for(int i = 0;i< tamano; i++)
                {
                    JSONObject jso = jsonArray.getJSONObject(i);
                    Serie s = new Serie();
                    s.setImagen_serie(jso.getString(Serie.PARAMETRO_IMG_PRINCIPAL_SERIE));
                    s.setNombre_serie(jso.getString(Serie.PARAMETRO_NOMBRE_SERIE));
                    s.setId_serie(jso.getInt(Serie.PARAMETRO_ID_SERIE));
                    data.add(s);
                }
                adaptadorSerie.notifyDataSetChanged();
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    private void cargarMasDatos(String json)
    {
        try
        {
            JSONObject jsonPeliculas = new JSONObject(json);
            JSONArray jsonArray = jsonPeliculas.getJSONArray("results");
            int tamanoActual = data.size();
            int modulo = tamanoActual % 20;

            int tamanoRestante = 20 -modulo;
            int tamano = 0;
            if(tamanoRestante > 9)
            {
                tamano = modulo+9;
            }
            else
            {
                tamano = modulo +tamanoRestante;
            }

            for(int i = modulo;i< tamano; i++)
            {
                JSONObject jso = jsonArray.getJSONObject(i);
                Serie s = new Serie();
                s.setImagen_serie(jso.getString(Serie.PARAMETRO_IMG_PRINCIPAL_SERIE));
                s.setNombre_serie(jso.getString(Serie.PARAMETRO_NOMBRE_SERIE));
                s.setId_serie(jso.getInt(Serie.PARAMETRO_ID_SERIE));
                data.add(s);
            }
            adaptadorSerie.notifyDataSetChanged();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    protected boolean validateError(Response response)
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

    @Override
    public void onScrollStateChanged(AbsListView arg0, int scrollState)
    {

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

        if (isLoading && (totalItemCount > itemCount)) {
            isLoading = false;
            itemCount = totalItemCount;
        }

        if (!isLoading && (totalItemCount - visibleItemCount)<= firstVisibleItem) {
            actualizarListaPeliculas();
            isLoading = true;
        }
    }



    private void actualizarListaPeliculas()
    {
        int tamano = data.size();
        progressLayout.setVisibility(View.VISIBLE);
        if(tamano < this.totalResultados)
        {
            int pagina = (tamano / 20)+1;
            String query;
            if(busqueda== "")
            {
                query= Serie.query_buscar_series+"?page="+pagina+"&language=es&api_key="+Serie.API_KEY;
            }
            else
            {
                query= Serie.query_buscar_series_titulo+"?query="+busqueda+"&page="+pagina+"&language=es&api_key="+Serie.API_KEY;
            }

            HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this,Serie.CONSULTA_SIN_ESPECIFICAR);
            task.execute(query);

        }
        else {
            progressLayout.setVisibility(View.GONE);
            Log.i("total:", data.size() + "");
            //Toast.makeText(this,getResources().getString(R.string.no_hay_mas_datos),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextBuscar.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            buscar();
            return true;
        }
        return false;
    }

    private void buscar()
    {

        if(editTextBuscar.getText().toString().isEmpty())
        {
            data.removeAll(data);
            this.totalResultados=0;
            this.itemCount=0;
            this.userScrolled= false;
            adaptadorSerie.notifyDataSetChanged();

            busqueda = "";
            loadSeries();
        }
        else
        {
            this.busqueda = editTextBuscar.getText().toString();
            if(!validarBusqueda())
            {
                this.busqueda = editTextBuscar.getText().toString().replace(" ","%20");
                this.totalResultados=0;
                this.itemCount=0;
                this.userScrolled= false;
                data.removeAll(data);
                adaptadorSerie.notifyDataSetChanged();
                loadSeriesConBusqueda();
            }
            else
            {
                this.busqueda = "";
                Toast.makeText(this,getResources().getString(R.string.caracteres_especiales_no_permitidos),Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean validarBusqueda()
    {
        String PATTERN_BUSQUEDA = "[^A-Za-z0-9 ]";
        Pattern pattern = Pattern.compile(PATTERN_BUSQUEDA);

        Matcher matcher = pattern.matcher(this.busqueda);
        return matcher.find();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this,DetalleSerieActivity.class);
        intent.putExtra("serie",data.get(position));
        startActivity(intent);
    }
}
