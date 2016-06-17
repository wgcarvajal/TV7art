package tv7art.com.tv7art.modelo;

import java.util.List;

/**
 * Esta clase define los atributos de un Temporada de una serie de  televisión ,clase propia de la lógica de negocio
 * compuesta por el nombre de la temporada y numero de la temporada y una lista de episodios correspondientes a la
 * temporada
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */
public class Temporada
{
    /**atributos de la clase*/
    private int numTemporada;
    private String nombre;
    private List<Episodio> episodios;


    /**
     *Getters and Setters
     */
    public int getNumTemporada() {
        return numTemporada;
    }

    public void setNumTemporada(int numTemporada) {
        this.numTemporada = numTemporada;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public List<Episodio> getEpisodios()
    {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }
}// fin de la clase Temporada
