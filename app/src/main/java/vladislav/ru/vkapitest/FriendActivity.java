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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 08.01.16.
 */
public class FriendActivity extends Activity {

    ArrayList<Bitmap> iconBitmap;
    List<String> friendsId;
    List<String> iconLink;
    MySimpleAdapter adapter;
    List<String> FriendsNames;
    ListView listViewFriends;
    JSONObject jsonObject;
    Handler h;
    int x;
    int count=0;

    public static ArrayList<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconBitmap=new ArrayList<Bitmap>();
        setContentView(R.layout.activity_friends);
        listViewFriends = (ListView) findViewById(R.id.listViewFriends);


        //listViewFriends.setAdapter(adapter);

       // listViewFriends.addFooterView(listViewFriends);
        //myAsyncTaskImage<String,Void, Bitmap> my= new myAsyncTask<>();


        String[] from = { "Photo", "Name" };
        int[] to = { R.id.image, R.id.Name};
        adapter = new MySimpleAdapter(this,temp,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new NewViewBinder());

        JSONObject jsono = null;
        myAsyncTaskImage my=new myAsyncTaskImage();
        my.execute("https://api.vk.com/method/friends.get?user_id="+UserData.getUserId()+"&fields=nickname,photo_50");
        try {
            jsono = my.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        listViewFriends.setAdapter(adapter);
        try {
            FriendsNames=new Parse().getFriendsFullNamesfromJSONObjects(jsono);
            iconLink = new Parse().getFriendsPhotoLink(jsono);
            friendsId = new Parse().getFriendsId(jsono);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0;i<FriendsNames.size();i++)
        {
            Map m = new HashMap<>();
            m.put("Photo", null);
            m.put("Name", FriendsNames.get(i));
            temp.add(m);
        }




        new myAsyncTaskImage2().execute();

        adapter.notifyDataSetChanged();


        h = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();

            };
        };


        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=Long.toString(id);
                Log.d("id=", view.toString());
                if (position<10)
                {
                    UserData.setCurrentFriend(friendsId.get(position));
                    Intent intent2 = new Intent(FriendActivity.this, PhotoFriends.class);
                    startActivity(intent2);
                }
                else
                {
                    UserData.setCurrentFriend(friendsId.get(position));
                    Intent messagesIntent = new Intent(FriendActivity.this, MessagesActivity.class);
                    startActivity(messagesIntent);
                }


            }
        });
    }
    class myAsyncTaskImage extends AsyncTask<String, Void, JSONObject> {

        private Exception exception;
        @Override
        protected JSONObject doInBackground(String[] urls) {
            try
            {
                URL urlAPI = new URL((java.lang.String)urls[0]);
                HttpsURLConnection urlConnection;
                urlConnection = (HttpsURLConnection) urlAPI.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                BufferedReader reader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                JSONObject obj = new JSONObject(reader.readLine());
                return obj;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                exception = e;
            }

            return null;
        }
        protected void onPostExecute(Bitmap feed) {
            // TODO: check this.exception
            // TODO: do something with the feed

        }
    }
    class myAsyncTaskImage2 extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;

        @Override
        protected Bitmap doInBackground(String[] urls) {
            try {
                Log.d("LOG", iconLink.get(0));


                for (int i=0;i<FriendsNames.size()/10;i++)
                {
                    URL urlAPI = new URL(iconLink.get(i));
                    HttpURLConnection urlConnection;
                    urlConnection = (HttpURLConnection) urlAPI.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    BufferedInputStream IS = new BufferedInputStream(urlConnection.getInputStream());

                    Bitmap map=(Bitmap) BitmapFactory.decodeStream(IS);
                    iconBitmap.add(map);
                    Map m = new HashMap<String,Object>();
                    m.put("Photo", iconBitmap.get(i));
                    m.put("Name", FriendsNames.get(i));

                    temp.remove(i);
                    temp.add(i, m);
                    h.sendEmptyMessage(0);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }

            return null;
        }

        protected void onPostExecute(Bitmap feed) {
            // TODO: check this.exception
            // TODO: do something with the feed

        }
    }
    class MySimpleAdapter extends SimpleAdapter {
        Context context;
        public MySimpleAdapter(Context context,
                               List<Map<String,Object>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context=context;
        }


        @Override
        public void setViewText(TextView v, String text) {
            super.setViewText(v, text);
        }

        @Override
        public void setViewImage(ImageView v, int value) {
            // метод супер-класса
            super.setViewImage(v, value);
            //Bitmap map = (Bitmap)data.get(0).get("Friends");
            //v.setImageBitmap(iconBitmap.get(count));
            // разрисовываем ImageView

        }
       /* @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view

            return convertView;
        }*/
    }

    public class NewViewBinder implements SimpleAdapter.ViewBinder {
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

    public class Parse
    {
        public List<String> getFriendsNamesfromJSONObjects(JSONObject jsonObject) throws JSONException {
            JSONArray jsonArray = new JSONArray();
            List<String> friendsNames = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsNames.add(jsonArray.getJSONObject(i).getString("first_name"));
            }
            return friendsNames;
        }
        public List<String> getFriendsLastNamesfromJSONObjects(JSONObject jsonObject) throws JSONException {
            JSONArray jsonArray = new JSONArray();
            List<String> friendsNames = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsNames.add(jsonArray.getJSONObject(i).getString("last_name"));
            }
            return friendsNames;
        }
        public List<String> getFriendsFullNamesfromJSONObjects(JSONObject jsonObject) throws JSONException {

            JSONArray jsonArray = new JSONArray();
            List<String> friendsNames = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            x=jsonArray.length();
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsNames.add(jsonArray.getJSONObject(i).getString("first_name")+" "+jsonArray.getJSONObject(i).getString("last_name"));
            }
            return friendsNames;
        }
        public List<String> getFriendsPhotoLink(JSONObject jsonObject) throws JSONException {
            JSONArray jsonArray = new JSONArray();
            List<String> friendsPhotoLink = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsPhotoLink.add(jsonArray.getJSONObject(i).getString("photo_50"));
            }
            return friendsPhotoLink;
        }
        public List<String> getFriendsId(JSONObject jsonObject) throws JSONException {
            JSONArray jsonArray = new JSONArray();
            List<String> friendsPhotoLink = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsPhotoLink.add(jsonArray.getJSONObject(i).getString("uid"));
            }
            return friendsPhotoLink;
        }

    }

}
