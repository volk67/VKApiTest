package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 21.01.16.
 */
public class MessagesActivity extends Activity{
    Friend friend;
    List<List<String>> amList = new ArrayList<>();
    List<Map<String,Object>> messagesList = new ArrayList<>();
    MessageAdapter adapter;
    ListView listViewMessages;
    Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        friend = workApi.getUser().getMyFriends().get(position);
        listViewMessages =(ListView)findViewById(R.id.listViewMessages);
        new DownLoadMessages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        String[] from={"Photo","Message"};
        int[] to={R.id.image,R.id.Name};
        adapter = new MessageAdapter(this,messagesList,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new myBinder());
        listViewMessages.setAdapter(adapter);

        h = new Handler() {
            public void handleMessage(Message msg) {

                adapter.notifyDataSetChanged();

            };
        };


        new updateMessage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onSendButtonClick(View view) throws UnsupportedEncodingException {
        EditText editText = (EditText)findViewById(R.id.editText);
        String s = editText.getText().toString();

        String newS = new String(s.getBytes("us-ascii"), "UTF-8");
        Log.d("mess1", Charset.defaultCharset().name());

        new SendMessage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URLEncoder.encode(s,"UTF-8"));
        editText.setText("");
    }

    class SendMessage extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                Log.d("mess2",urls[0]);
                new workApi().sendMessage(friend.getUserId(),urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    class DownLoadMessages extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... urls)
        {
            try {
                amList.addAll(new workApi().downLoadMessages(0, friend.getUserId()));
                for (int i=0;i<20;i++)
                {
                    Map<String,Object> map= new HashMap<>();
                    if (amList.get(1).get(i).equals("0")) map.put("Photo",friend.getAvatar());
                    else map.put("Photo", workApi.getUser().getAvatar());
                    map.put("Message",amList.get(0).get(i));
                    messagesList.add(map);
                }
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
                    if (jsonArray.getJSONArray(i).getString(0).equals("4")&&jsonArray.getJSONArray(i).getString(3).equals(friend.getUserId()))
                    {
                        Map<String,Object> m = new HashMap<>();
                        int temp = Integer.valueOf(jsonArray.getJSONArray(i).getString(2));
                        Log.d("temp"," "+temp);
                        temp%=4;
                        temp/=2;
                        Log.d("temp", " " + temp);
                        if (temp==0) m.put("Photo", friend.getAvatar());
                        else m.put("Photo", workApi.getUser().getAvatar());
                        m.put("Message", jsonArray.getJSONArray(i).getString(6));
                        messagesList.add(0, m);
                        h.sendEmptyMessage(0);
                    }
                }
                return null;
            }
        }
    }


    class MessageAdapter extends SimpleAdapter
    {
        Context context;
        List<Map<String,Object>> data;
        ImageView imageView;
        public MessageAdapter(Context context, List<Map<String,Object>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context=context;
            this.data=data;
        }
        class ViewHolder
        {
            protected ImageView imageView;
            protected TextView textView;
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
            viewHolder.textView.setText(""+data.get(position).get("Message"));
            viewHolder.imageView.setImageBitmap((Bitmap) data.get(position).get("Photo"));
            return view;
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
