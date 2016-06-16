package tv7art.com.tv7art.modelo;

/**
 * Created by geovanny on 15/06/16.
 */
public class Pelicula
{
    /* url base de las imagenes con distintos anchos*/
        // url base de las imagenes con un ancho w154
        public static String path_imagen_w154 = "http://image.tmdb.org/t/p/w154";
        // url base de las imagenes con un ancho w92
        public static String path_imagen_w92 = "http://image.tmdb.org/t/p/w92";
        // url base de las imagenes con un ancho w185
        public static String path_imagen_w185 = "http://image.tmdb.org/t/p/w185";
        // url base de las imagenes con un ancho w500
        public static String path_imagen_w500 = "http://image.tmdb.org/t/p/w500";
        // url base de las imagenes con el ancho original
        public static String path_imagen_woriginal = "http://image.tmdb.org/t/p/original";
    /* parametros */
        //titulo original
        public static String titulo_pelicula = "original_title";
        //imagen principal
        public static String imagen_pelicula = "poster_path";


    private String nombrePelicula;
    private String imagenPelicula;

    public String getNombrePelicula()
    {
        return nombrePelicula;
    }

    public void setNombrePelicula(String nombrePelicula)
    {
        this.nombrePelicula = nombrePelicula;
    }

    public String getImagenPelicula()
    {
        return imagenPelicula;
    }

    public void setImagenPelicula(String imagenPelicula)
    {
        this.imagenPelicula = imagenPelicula;
    }
}
