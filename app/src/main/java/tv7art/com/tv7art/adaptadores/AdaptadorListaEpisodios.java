package tv7art.com.tv7art.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import tv7art.com.tv7art.R;
import tv7art.com.tv7art.modelo.Episodio;

/**
 * Esta clase es la encargada de mostrar el contenido (numero de episodio e nombre del episodio) de la temporada en
 * el ListView del layout  DetalleSerieActivity.
 * @author: Wilson Geovanny Carvajal
 * @version: 16/06/2016.
 *
 */
public class AdaptadorListaEpisodios  extends BaseAdapter
{
    /**atributos de la clase*/
    List <Episodio> data;
    Context context;
    private LayoutInflater mInflater;

    /**
     * Constructor parmetrizado de la clase AdaptadorListaEpisodios
     * @param context el contexto o entorno actual de la aplicacion
     * @param data contiene la lista de datos de tipo Episodio con la informacion requerida para mostrar en el Listview
     */
    public AdaptadorListaEpisodios(Context context, List<Episodio> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
    }//cierre del constructor

    /**
     * esta clase contine los atributos del template de la serie que sera mostrado en el listview
     * patron usago en el metodo getView() para la reutilizacion de vista y no crear otras instancias
     */
    public class ViewHolder
    {
        public TextView txtNumeroEpisodio;
        public TextView txtNombreEpisodio;
    }

    /**
     * Implementacion del Método necesrio de la clase BaseAdapter
     * @return El número total de ítems que contiene la lista data
     */
    @Override
    public int getCount() {
        if(data!=null)
        {
            return data.size();
        }
        return 0;
    }

    /**
     * Implementacion del Método necesrio de la clase BaseAdapter
     * @return el objeto  en la posicion position de la lista data
     */
    @Override
    public Object getItem(int position) {
        if(data != null && position >= 0 && position < getCount() )
        {
            return data.get(position);
        }
        return null;
    }

    /**
     * Implementacion del Método necesrio de la clase BaseAdapter
     * @return la misma posición recibida
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Implementacion del Método necesrio de la clase BaseAdapter
     * este metodo es el encargado de crear o reutilizar la instancia del template_episodio.xml
     * fijando los valores de sus respectivos atributos obtenidos en la lista data apartir de una posicion
     *
     * @param position numero correspondiente a la posición del View nesecita mostrar en el gridview con
     *                 obteniendo los datos de la lista data
     * @param convertView contiene un elemento nulo o una instacia del template_episodio.xml
     * @return View  instancia de template_episodio.xml listo para ser mostrado en pantalla
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_episodio,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.txtNumeroEpisodio=(TextView) v.findViewById(R.id.txt_numero_episodio);
            viewHolder.txtNombreEpisodio=(TextView) v.findViewById(R.id.txt_nombre_episodio);

            v.setTag(viewHolder);//guardando la instancia del template.
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();//reutilizando  los elementos evitando tener que volerlos a instanciar
        }

        viewHolder.txtNumeroEpisodio.setText(data.get(position).getNumEpisodio() +".");
        viewHolder.txtNombreEpisodio.setText(data.get(position).getNombre());

        return v;
    }



    /**
     * este metodo remueve los datos actualiza y vuelve y carga nuevos datos en la lista data vacia
     * @param  data Lista de Episodios que se mostraran en la lista
     * */
    public void set_data(List<Episodio> data)
    {
        this.data.removeAll(this.data);
        notifyDataSetChanged();

        for (Episodio e : data)
        {
            Episodio ep = new Episodio();
            ep.setNombre(e.getNombre());
            ep.setNumEpisodio(e.getNumEpisodio());
            this.data.add(ep);

        }
        notifyDataSetChanged();
    }
}
