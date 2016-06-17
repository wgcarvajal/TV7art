package tv7art.com.tv7art.modelo;

/**
 * Esta clase define los atributos de un Episodio de television clase propia de la logica de negocio
 * compuesta por el nombre del episodio y numero del episodio
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */
public class Episodio
{
    /**atributos de la clase*/
    private int numEpisodio;
    private String nombre;


    /**
     *Getters and Setters
     */
    public int getNumEpisodio() {
        return numEpisodio;
    }

    public void setNumEpisodio(int numEpisodio) {
        this.numEpisodio = numEpisodio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}//fin de la clase episodio
