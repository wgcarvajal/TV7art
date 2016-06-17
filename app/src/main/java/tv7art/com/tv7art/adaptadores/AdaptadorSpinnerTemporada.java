package tv7art.com.tv7art.adaptadores;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import tv7art.com.tv7art.R;
import tv7art.com.tv7art.modelo.Temporada;

/**
 * Esta clase es la encargada de mostrar el contenido (nombre de la temporada)
 * de las serie en el spinerTemporada del layout
 * DetalleSerieActivity.
 * @author: Wilson Geovanny Carvajal
 * @version: 15/06/2016.
 *
 */
public class AdaptadorSpinnerTemporada extends ArrayAdapter
{
    /**atributos de la clase*/
    private List<Temporada> data;
    private Context context;

    /**
     * Constructor parmetrizado para la clase adaptadorserie
     * @param context el contexto o entorno actual de la aplicacion
     * @param objects contiene la lista de datos de tipo Temporada con la informacion requerida para mostrar en el spinnerTemporada
     * @param resource numero identificador del layout que se instaciara
     */
    public AdaptadorSpinnerTemporada(Context context, int resource, List objects)
    {
        super(context, resource, objects);
        this.context=context;
        this.data=objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }


    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        View v=null;

        if(convertView == null)
        {
            v = View.inflate(context, R.layout.template_spinner_temporada ,null);

        }else
        {
            v = convertView;
        }
        TextView item=(TextView)v.findViewById(R.id.txt_item_spinner_temporada);

        item.setText(data.get(position).getNombre());

        return v;
    }
}
