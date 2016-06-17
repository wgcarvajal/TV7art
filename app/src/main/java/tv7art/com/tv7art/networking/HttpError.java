package tv7art.com.tv7art.networking;

/**
 * Esta clase define unos atributos estaticos con un indice
 * que representa los distintos tipos de error que se pueden
 * presentar en una consulta a un servicio remoto
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */
public class HttpError
{
    public static final int NO_ERROR = -1;
    public static final int NO_INTERNET = 0;
    public static final int TIMEOUT = 1;
    public static final int SERVER = 2;
}
