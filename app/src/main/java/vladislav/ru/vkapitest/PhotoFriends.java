package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 11.01.16.
 */
public class PhotoFriends extends Activity {

    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    List<String> strList = new ArrayList<>();
    String CurrentId;
    GridView gridView;
    ImageAdapter adapter;
    Handler h;
    boolean flag=true;
    int offset=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        DownLoadPhotos photos = new DownLoadPhotos();
        photos.execute();

        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setNumColumns(4);
        String[] from = { "Photo" };
        int[] to = { R.id.image2};
        adapter = new ImageAdapter(this,list,R.layout.forimageadapter,from,to);
        adapter.setViewBinder(new NewViewBinder());

        gridView.setAdapter(adapter);
        h = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
                Log.d("Cignal","+");

            };
        };

        DownLoadImage DImage = new DownLoadImage();
        DImage.execute();

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount&&flag) {
                    flag=false;
                    Log.d("LIst","END");
                    new DownLoadPhotos().execute();
                    new DownLoadImage().execute();
                }
            }
        });
    }


    class ImageAdapter extends SimpleAdapter
    {
        Context context;
        public ImageAdapter(Context context,
                               List<Map<String,Object>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context=context;
        }

        @Override
        public void setViewImage(ImageView v, int value) {
            // метод супер-класса
            super.setViewImage(v, value);
            Log.d("setViewImage","ghb[jl");
            //Bitmap map = (Bitmap)data.get(0).get("Friends");
            //v.setImageBitmap(iconBitmap.get(count));
            // разрисовываем ImageView

        }
    }
    public class NewViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if ((view instanceof ImageView)& (data instanceof Bitmap)) {
                Log.d("Binder","ghb[jl");
                ImageView iv = (ImageView) view;
                Bitmap bm = (Bitmap) data;
                iv.setImageBitmap(bm);
                return true;
            }
            return false;
        }
    }



    class DownLoadPhotos extends AsyncTask<String,Void,Void>
    {
        private Exception exception;
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                CurrentId=UserData.getCurrentFriend();
                URL url = new URL("https://api.vk.com/method/photos.getAll?owner_id="+CurrentId+"&count="+20+"&offset="+offset+"&access_token="+UserData.getAccessToken());
                offset+=20;

                HttpsURLConnection urlConnection;
                urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                BufferedReader reader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String str = reader.readLine();
                Log.d("preParse", str);
                JSONObject obj = new JSONObject(str);
                new Parse().parser(obj);

                for(int i=offset-20;i<strList.size();i++)
                    list.add(null);


            }
            catch (Exception e)
            {
                e.printStackTrace();
                exception = e;
            }
            return null;
        }
    }

    class DownLoadImage extends AsyncTask<String,Void,Void>
    {
        private Exception exception;
        @Override
        protected Void doInBackground(String... urls)
        {
            for (int i=offset-20;i<strList.size();i++)
            {
                try {
                    URL url = new URL(strList.get(i));
                    Log.d("URL_PHOTO",strList.get(i));
                    HttpURLConnection urlConnection;
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    BufferedInputStream IS = new BufferedInputStream(urlConnection.getInputStream());

                    Bitmap map=(Bitmap) BitmapFactory.decodeStream(IS);
                    Map<String,Object> m= new HashMap<>();
                    m.put("Photo", map);

                    list.remove(i);
                    list.add(i, m);
                    h.sendEmptyMessage(0);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            flag=true;
            return null;
        }
    }

    class Parse
    {
        public List<String> parser(JSONObject jsonObject) throws JSONException
        {
            JSONArray jsonArray = new JSONArray();
            jsonArray= jsonObject.getJSONArray("response");
            //List<String> strList = new ArrayList<>();
            for (int i=1;i<jsonArray.length();i++)
            {
                strList.add(jsonArray.getJSONObject(i).getString("src"));
            }
            return null;
        }
    }
}
