package tv7art.com.tv7art.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tv7art.com.tv7art.R;
import tv7art.com.tv7art.modelo.Serie;


/**
 * Esta clase es la encargada de mostrar el contenido (nombre e imagen) de las series en el gridview del layout
 * activity_main , haciendo uso de una libreria llama Picasso para la carga de la imagenes en el ImageView
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */
public class AdaptadorSerie extends BaseAdapter
{
    /**atributos de la clase*/
    private Context context;
    private LayoutInflater mInflater;/** atributo que nos sirve para la creacion de instacias de template_serie.xml*/
    private List<Serie> data;

    /**
     * esta clase contine los atributos del template de la serie que sera mostrado en el gridview
     * patron usago en el metodo getView() para la reutilizacion de vista y no crear otras instancias
     */
    public class ViewHolder
    {
        public ImageView imgSerie;
        public TextView txtNombreSerie;
    }//Cierre de la clase ViewHolder


    /**
     * Constructor parmetrizado para la clase adaptadorserie
     * @param context el contexto actual de la aplicacion
     * @param data contiene la lista de datos de tipo Serie con la informacion requerida para mostrar en el gridview
     */
    public AdaptadorSerie(Context context, List<Serie> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
    }//cierre del constructor


    /**
     * Implementacion del Método necesrio de la clase BaseAdapter
     * @return El número total de ítems que contiene la lista data
     */
    @Override
    public int getCount()
    {
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
    public Object getItem(int position)
    {
        if(data != null && position >= 0 && position < getCount() )//comprueba que la lista data contenga elementos y la posicion recibida sea valida
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
    public long getItemId(int position)
    {
        return position;
    }


    /**
     * Implementacion del Método necesrio de la clase BaseAdapter
     * este metodo es el encargado de crear o reutilizar la instancia del template_serie.xml
     * fijando los valores de sus respectivos atributos obtenidos en la lista data apartir de una posicion
     *
     * @param position numero correspondiente a la posición del View nesecita mostrar en el gridview con
     *                 obteniendo los datos de la lista data
     * @param convertView contiene un elemento nulo o una instacia del template_serie.xml
     * @return View  instancia de template_serie.xml listo para ser mostrado en pantalla
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_serie,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.imgSerie=(ImageView) v.findViewById(R.id.img_serie);
            viewHolder.txtNombreSerie=(TextView) v.findViewById(R.id.txt_nombre_serie);
            v.setTag(viewHolder);//guardando la instancia del template.
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();//reutilizando  los elementos evitando tener que volerlos a instanciar
        }

        Serie serie = data.get(position);//obteniendo los datos correspondientes a la posicion
        viewHolder.txtNombreSerie.setText(serie.getNombre_serie());

        Picasso.with(context)/**Picasso encargada de cargar(comunicación Asincróna)  y fijar la imagen en el ImageView*/
                .load(Serie.PATH_IMG_W154 + serie.getImagen_serie())
                .into(viewHolder.imgSerie);

        return v;
    }
}