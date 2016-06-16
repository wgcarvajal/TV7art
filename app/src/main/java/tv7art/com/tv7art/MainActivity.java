package tv7art.com.tv7art;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tv7art.com.tv7art.adaptadores.AdaptadorPelicula;
import tv7art.com.tv7art.modelo.Pelicula;
import tv7art.com.tv7art.networking.HttpAsyncTask;
import tv7art.com.tv7art.networking.HttpError;
import tv7art.com.tv7art.networking.Response;

import android.widget.AbsListView.OnScrollListener;

public class MainActivity extends AppCompatActivity implements HttpAsyncTask.OnHttpResponse, AbsListView.OnScrollListener {
    private GridView gridPeliculas;


    private AdaptadorPelicula adaptadorPelicula;
    private List<Pelicula> data = new ArrayList();
    private boolean userScrolled = false;
    private int totalResultados = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridPeliculas = (GridView)findViewById(R.id.grid_Peliculas);
        adaptadorPelicula = new AdaptadorPelicula(this,data);
        gridPeliculas.setAdapter(adaptadorPelicula);
        gridPeliculas.setOnScrollListener(this);
        HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this);
        task.execute("https://api.themoviedb.org/3/discover/movie?language=es&api_key=91439354c42883bccf13ceddb12844bc");
    }

    @Override
    public void onResponse(Response response)
    {
        if(validateError(response))
        {
            try {

                JSONObject jsonPeliculas = new JSONObject(response.getMsg());
                JSONArray jsonArray = jsonPeliculas.getJSONArray("results");
                if(this.data.isEmpty())
                {
                    int tresultados = jsonPeliculas.getInt("total_results");
                    int tamano = 0;
                    if (tresultados > 100)
                    {
                        this.totalResultados = 100;
                        tamano = 9;
                    }
                    else
                    {
                        this.totalResultados = tresultados;
                        if(tresultados > 9)
                        {
                            tamano = 9;
                        }
                        else
                        {
                            tamano = tresultados;
                        }
                    }


                    for(int i = 0;i< tamano; i++)
                    {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        Pelicula p = new Pelicula();
                        p.setImagenPelicula(jso.getString(Pelicula.imagen_pelicula));
                        p.setNombrePelicula(jso.getString(Pelicula.titulo_pelicula));
                        data.add(p);
                    }



                }

                else
                {
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
                        Pelicula p = new Pelicula();
                        p.setImagenPelicula(jso.getString(Pelicula.imagen_pelicula));
                        p.setNombrePelicula(jso.getString(Pelicula.titulo_pelicula));
                        data.add(p);
                    }

                }




                adaptadorPelicula.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        // If scroll state is touch scroll then set userScrolled
        // true
        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
        {
            this.userScrolled = true;
        }

    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // Now check if userScrolled is true and also check if
        // the item is end then update list view and set
        // userScrolled to false
        if (this.userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {

            this.userScrolled = false;
            actualizarListaPeliculas();
        }
    }

    private void actualizarListaPeliculas()
    {
        int tamano = data.size();

        if(tamano < this.totalResultados)
        {
            int pagina = (tamano / 20)+1;

            HttpAsyncTask task = new HttpAsyncTask(HttpAsyncTask.GET, this,this);
            task.execute("https://api.themoviedb.org/3/discover/movie?page="+pagina+"&language=es&api_key=91439354c42883bccf13ceddb12844bc");

        }
        else
        {
            Toast.makeText(this,"no hay mas datos",Toast.LENGTH_SHORT).show();
        }

    }
}
