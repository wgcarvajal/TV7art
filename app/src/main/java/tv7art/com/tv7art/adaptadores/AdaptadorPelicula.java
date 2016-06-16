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
import tv7art.com.tv7art.modelo.Pelicula;

/**
 * Created by geovanny on 15/06/16.
 */
public class AdaptadorPelicula extends BaseAdapter
{
    private Context context;
    private LayoutInflater mInflater;
    private List<Pelicula> data;

    public class ViewHolder
    {
        public ImageView imgPelicula;
        public TextView txtTituloPelicula;
    }

    public AdaptadorPelicula(Context context, List<Pelicula> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount()
    {
        if(data!=null)
        {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position)
    {
        if(data != null && position >= 0 && position < getCount() )
        {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_pelicula,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.imgPelicula=(ImageView) v.findViewById(R.id.img_pelicula);
            viewHolder.txtTituloPelicula=(TextView) v.findViewById(R.id.txt_titulo_pelicula);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();
        }

        Pelicula pelicula = data.get(position);
        viewHolder.txtTituloPelicula.setText(pelicula.getNombrePelicula());

        Picasso.with(context)
                .load(Pelicula.path_imagen_w154 + pelicula.getImagenPelicula())
                .into(viewHolder.imgPelicula);

        return v;
    }
}
