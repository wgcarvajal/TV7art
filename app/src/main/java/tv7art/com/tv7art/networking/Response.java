package tv7art.com.tv7art.networking;

/**
 * Esta clase define los atributos correspondientes a la respuesta esperada a una solicitud de servicio remoto
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */
public class Response
{
    /** atributos de la clase*/
    String msg;
    int code;
    int error;
    int consulta;/** este atributo servira para poder diferenciar a que tipo de consulta corresponde ya
                    que todas las consultas retornaran en un activity por la implementación de un único
                    metodo.
                    */

    /**
     * Constructor parmetrizado para la clase Response
     * @param msg cadena de texto con la respuesta de un servicio remoto
     * @param code numero correspondiente al mensajes enviado por un servicio remoto
     */
    public Response(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    /**
     *Getters and Setters
     */
    public Response(int error) {
        this.error = error;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getConsulta() {
        return consulta;
    }

    public void setConsulta(int consulta) {
        this.consulta = consulta;
    }
}//fin de la clase Response
