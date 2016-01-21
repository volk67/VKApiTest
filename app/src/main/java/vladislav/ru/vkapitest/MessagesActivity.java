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
        adapter = new mySimpleAdapter(this,messagesList,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new NewViewBinder());
        listViewMessages.setAdapter(adapter);

        h = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
                Log.d("Cignal","+");

            };
        };
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
