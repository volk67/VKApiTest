package vladislav.ru.vkapitest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vladislav on 24.01.16.
 */


class ImageAdapter extends SimpleAdapter
{
    Context context;
    List<Map<String,Object>> data;
    ImageView imageView;
    public ImageAdapter(Context context, List<Map<String,Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context=context;
        this.data=data;
    }
    static class ViewHolder
    {
        protected ImageView imageView;
        protected TextView textView;
    }



    @Override
    public int getCount() {
        return data.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public void setViewImage(ImageView view, int value)
    {
        super.setViewImage(view, value);
    }

    @Override
    public void setViewText(TextView view, String text)
    {
        super.setViewText(view, text);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.forsimpleadapter,null);
        }else {
            view = convertView;
        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView=(ImageView)view.findViewById(R.id.image);
        viewHolder.textView=(TextView)view.findViewById(R.id.Name);
        viewHolder.textView.setText(""+data.get(position).get("Name"));
        viewHolder.imageView.setImageBitmap((Bitmap) data.get(position).get("Photo"));
        viewHolder.textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d("click","Well");

            }
        });
        return view;
    }
}


class myBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data,
                                String textRepresentation) {
        if ((view instanceof ImageView)& (data instanceof Bitmap)) {
            ImageView iv = (ImageView) view;
            Bitmap bm = (Bitmap) data;
            iv.setImageBitmap(bm);
            return true;
        }
        return false;
    }
}