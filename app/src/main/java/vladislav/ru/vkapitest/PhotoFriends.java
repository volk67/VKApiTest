package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Intent;
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

    Friend friend;
    List<String> urlsList=new ArrayList<>();
    UserData user = workApi.getUser();
    List<Map<String,Object>> list;
    GridView gridView;
    ImageAdapter adapter;
    Handler handler, handlerDidFinished;
    boolean flag=true;
    int offset=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        list =new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        friend = user.getMyFriends().get(position);

        new DownLoadURLS().execute();

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
            };
        };

        handlerDidFinished = new Handler() {
            public void handleMessage(Message msg) {
                new DownLoadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            };
        };

        /*gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount&&flag) {
                    flag=false;
                    new DownLoadURLS().execute();
                    new DownLoadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });*/
    }





    class DownLoadURLS extends AsyncTask<String,Void,Void>
    {
        private Exception exception;
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                urlsList.addAll(new workApi().downLoadPhotosUrls(offset, friend.getUserId()));
                offset+=20;
                handlerDidFinished.sendEmptyMessage(0);

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
            for (int i=offset-20;i<urlsList.size();i++)
            {
                try {
                    Bitmap map=new NetWork().readBitmap(urlsList.get(i));
                    Map<String,Object> m= new HashMap<>();
                    m.put("Photo", map);
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
}
