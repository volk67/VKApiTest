package vladislav.ru.vkapitest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by vladislav on 11.01.16.
 */
public class PhotoFriends extends Activity {

    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    List<String> strList = new ArrayList<>();
    String CurrentId;
    GridView gridView;
    ImageAdapter adapter;
    Handler handler;
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
        adapter.setViewBinder(new myBinder());

        gridView.setAdapter(adapter);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
                Log.d("Cignal","+");

            };
        };

        DownLoadImage downLoadImage = new DownLoadImage();
        downLoadImage.execute();

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount&&flag) {
                    flag=false;
                    new DownLoadPhotos().execute();
                    new DownLoadImage().execute();
                }
            }
        });
    }





    class DownLoadPhotos extends AsyncTask<String,Void,Void>
    {
        private Exception exception;
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                CurrentId=UserData.getCurrentFriend();
                String str = new NetWork().readData("https://api.vk.com/method/photos.getAll?owner_id="+CurrentId+"&count="+20+"&offset="+offset+"&access_token="+UserData.getAccessToken());
                offset+=20;
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
                    Bitmap map=new NetWork().readBitmap(strList.get(i));
                    Map<String,Object> m= new HashMap<>();
                    m.put("Photo", map);

                    list.remove(i);
                    list.add(i, m);
                    handler.sendEmptyMessage(0);

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
            for (int i=1;i<jsonArray.length();i++)
            {
                strList.add(jsonArray.getJSONObject(i).getString("src"));
            }
            return null;
        }
    }
}
