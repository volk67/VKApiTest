package vladislav.ru.vkapitest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
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
    ImageAdapter adapter;
    ListView listViewMessages;
    Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        listViewMessages =(ListView)findViewById(R.id.listViewMessages);
        new DownLoadMessages().execute();

        String[] from={"Photo","Message"};
        int[] to={R.id.image,R.id.Name};
        adapter = new ImageAdapter(this,messagesList,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new myBinder());
        listViewMessages.setAdapter(adapter);

        h = new Handler() {
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
                String s = new NetWork().readData("https://api.vk.com/method/messages.getHistory?user_id=" + current_user + "&access_token=" + UserData.getAccessToken());
                JSONObject jsonObject = new JSONObject(s);
                new Parse().parser(jsonObject);
                h.sendEmptyMessage(0);
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
                String s = new NetWork().readData("https://api.vk.com/method/messages.getLongPollServer?need_pts=1&access_token=" + UserData.getAccessToken());
                JSONObject jsonObject = new JSONObject(s);
                String key = new Parse().getKey(jsonObject);
                String server = new Parse().getServer(jsonObject);
                String ts = new Parse().getTs(jsonObject);
                String pts = new Parse().getPts(jsonObject);
                while(true)
                {
                    String s1=new NetWork().readData("http://" + server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25&mode=2");
                    JSONObject obj = new JSONObject(s1);
                    new Parse().updMessage(obj);
                    ts = new JSONObject(s1).getString("ts");
                }

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
                    if (jsonArray.getJSONArray(i).getString(0).equals("4")&&jsonArray.getJSONArray(i).getString(3).equals(UserData.getCurrentFriend()))
                    {
                        Map<String,Object> m = new HashMap<>();
                        m.put("Photo",null);
                        m.put("Message", jsonArray.getJSONArray(i).getString(6));
                        messagesList.add(0, m);
                        h.sendEmptyMessage(0);
                    }
                }
                return null;
            }
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
