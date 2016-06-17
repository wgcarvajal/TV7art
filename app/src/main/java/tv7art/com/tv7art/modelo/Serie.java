package tv7art.com.tv7art.modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Esta clase define los atributos de una serie de televisión, que son el nombre de la serie,
 * una imagen representativa, lista de temporadas , lista de generos y lista de actores
 * clase propia de la lógica de negocio,
 * tambien contiene atributos con los parametros  que se necesitan para poder interactuar con el api de themoviebd
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */
public class Serie implements Serializable
{
    /**apikey Themoviebd*/
    public static String API_KEY = "91439354c42883bccf13ceddb12844bc";

    /**consultas Themmoviebd*/
    public static String query_buscar_series = "http://api.themoviedb.org/3/discover/tv";
    public static String query_buscar_series_titulo = "http://api.themoviedb.org/3/search/tv";
    public static String query_buscar_serie_id = "http://api.themoviedb.org/3/tv";

    /** url base de las imagenes con un ancho w# */
    public static String PATH_IMG_W154 = "http://image.tmdb.org/t/p/w154";
    public static String PATH_IMG_W92 = "http://image.tmdb.org/t/p/w92";
    public static String PATH_IMG_W185 = "http://image.tmdb.org/t/p/w185";
    public static String PATH_IMG_W500 = "http://image.tmdb.org/t/p/w500";
    public static String PATH_IMG_W_ORIGINAL = "http://image.tmdb.org/t/p/original";

    /** parmetros de busqueda JSON */
    public static String PARAMETRO_NOMBRE_SERIE = "name";
    public static String PARAMETRO_IMG_PRINCIPAL_SERIE = "poster_path";
    public static String PARAMETRO_ID_SERIE = "id";
    public static String PARAMETRO_CAST = "cast";
    public static String PARAMETRO_NOMBRE_ACTOR = "name";
    public static String PARAMETRO_GENEROS_SERIE = "genres";
    public static String PARAMETRO_NOMBRE_GENERO = "name";
    public static String PARAMETRO_NUMERO_TEMPORADAS = "number_of_seasons";
    public static String PARAMETRO_EPISODIOS = "episodes";
    public static String PARAMETRO_NOMBRE_EPISODIO = "name";

    /** consultas propias */

    public static  int CONSULTA_SIN_ESPECIFICAR = 1;
    public static  int CONSULTA_SERIE = 2;
    public static  int CONSULTA_EPISODIOS = 3;
    public static  int CONSULTA_ACTORES = 4;
    public static  int CONSULTA_EPISODIOS_PRIMER_TEMPORADA = 5;




    /**atributos de la clase*/
    private String nombre_serie;
    private String imagen_serie;
    private int id_serie;
    private List<Temporada> temporadas;
    private List<String> generos;
    private List<String> actores;

    /**
      *Getters and Setters
     */
    public String getNombre_serie()
    {
        return nombre_serie;
    }

    public void setNombre_serie(String nombre_serie)
    {
        this.nombre_serie = nombre_serie;
    }

    public String getImagen_serie()
    {
        return imagen_serie;
    }

    public void setImagen_serie(String imagen_serie)
    {
        this.imagen_serie = imagen_serie;
    }

    public int getId_serie() {
        return id_serie;
    }

    public void setId_serie(int id_serie) {
        this.id_serie = id_serie;
    }

    public List<Temporada> getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(List<Temporada> temporadas) {
        this.temporadas = temporadas;
    }

    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    public List<String> getActores() {
        return actores;
    }

    public void setActores(List<String> actores) {
        this.actores = actores;
    }
}//fin de la clase Serie
