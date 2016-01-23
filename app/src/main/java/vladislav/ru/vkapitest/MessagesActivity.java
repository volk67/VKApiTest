package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 21.01.16.
 */
public class MessagesActivity extends Activity{

    List<Map<String,Object>> messagesList = new ArrayList<>();
    mySimpleAdapter adapter;
    ListView listViewMessages;
    Handler h,h1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        listViewMessages =(ListView)findViewById(R.id.listViewMessages);
        new DownLoadMessages().execute();

        String[] from={"Photo","Message"};
        int[] to={R.id.image,R.id.Name};
        adapter = new mySimpleAdapter(this,messagesList,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new NewViewBinder());
        listViewMessages.setAdapter(adapter);

        h = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
                Log.d("Cignal","+");

            };
        };

        h1 = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
                Log.d("Cignal","+");

            };
        };
        new updateMessage().execute();
    }

    class DownLoadMessages extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                String current_user = UserData.getCurrentFriend();
                URL url = new URL("https://api.vk.com/method/messages.getHistory?user_id="+current_user+"&access_token="+UserData.getAccessToken());
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String s = reader.readLine();
                JSONObject jsonObject = new JSONObject(s);
                new Parse().parser(jsonObject);
                h.sendEmptyMessage(0);
                Log.d("messages=",s);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    class updateMessage extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                URL url = new URL("https://api.vk.com/method/messages.getLongPollServer?need_pts=1&access_token="+UserData.getAccessToken());
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String s = reader.readLine();
                JSONObject jsonObject = new JSONObject(s);
                String key = new Parse().getKey(jsonObject);
                String server = new Parse().getServer(jsonObject);
                String ts = new Parse().getTs(jsonObject);
                String pts = new Parse().getPts(jsonObject);
                //Log.d("update_messages",s+" "+key+" "+server+" "+ts+" "+pts);
                while(true)
                {
                    URL url1 = new URL("http://"+server+"?act=a_check&key="+key+"&ts="+ts+"&wait=25&mode=2");
                    HttpURLConnection urlConnection1 = (HttpURLConnection)url1.openConnection();
                    urlConnection1.setRequestMethod("GET");
                    urlConnection1.connect();
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(urlConnection1.getInputStream()));
                    String s1=reader1.readLine();
                    Log.d("Ответ от longpoll", s1);
                    JSONObject obj = new JSONObject(s1);
                    new Parse().updMessage(obj);

                    ts = new JSONObject(s1).getString("ts");


                }
                /*URL url2 = new URL("https://api.vk.com/method/messages.getLongPollHistory?ts="+ts+"&pts="+pts+"&access_token="+UserData.getAccessToken());
                HttpsURLConnection urlConnection2 = (HttpsURLConnection)url2.openConnection();
                urlConnection2.setRequestMethod("GET");
                urlConnection2.connect();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
                String s2 = reader2.readLine();
                Log.d("Ответ от longpoll",s1);*/
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        class Parse
        {
            public String getServer(JSONObject jsonObject) throws JSONException
            {
                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                String server = jsonObject1.getString("server");
                return server;
            }
            public String getKey(JSONObject jsonObject) throws JSONException
            {
                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                String key = jsonObject1.getString("key");
                return key;
            }
            public String getTs(JSONObject jsonObject) throws JSONException
            {
                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                String Ts = jsonObject1.getString("ts");
                return Ts;
            }
            public String getPts(JSONObject jsonObject) throws JSONException
            {
                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                String pts = jsonObject1.getString("pts");
                return pts;
            }
            public Void updMessage(JSONObject jsonObject) throws JSONException
            {
                JSONArray jsonArray = jsonObject.getJSONArray("updates");
                for (int i=0;i<jsonArray.length();i++)
                {
                    if (jsonArray.getJSONArray(i).getString(0)=="4")
                    {
                        Log.d("friend_id1=",jsonArray.getJSONArray(i).getString(3));
                        Log.d("friend_id2=",UserData.getCurrentFriend());
                    }
                }
                Log.d("Array",jsonArray.getJSONArray(0).getString(0));
                return null;
            }
        }
    }
    class mySimpleAdapter extends SimpleAdapter
    {
        public mySimpleAdapter(Context context, List<Map<String,Object>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public void setViewImage(ImageView view, int value)
        {
            super.setViewImage(view, value);
        }

        @Override
        public void setViewText(TextView view, String text)
        {
            super.setViewText(view,text);
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
    class Parse
    {
        public List<String> parser(JSONObject jsonObject) throws JSONException
        {
            JSONArray jsonArray = new JSONArray();
            jsonArray= jsonObject.getJSONArray("response");
            //List<String> strList = new ArrayList<>();
            for (int i=1;i<jsonArray.length();i++)
            {
                Map<String,Object> m = new HashMap<>();
                m.put("Photo",null);
                m.put("Message",jsonArray.getJSONObject(i).getString("body"));
                messagesList.add(m);
            }
            return null;
        }

    }
}
