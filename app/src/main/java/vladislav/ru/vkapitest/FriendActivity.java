package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by vladislav on 08.01.16.
 */
public class FriendActivity extends Activity {

    ArrayList<Bitmap> iconBitmap;
    List<String> friendsId;
    List<String> iconLink;
    ListView listViewFriends;
    JSONObject jsonObject;
    int x;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listViewFriends = (ListView) findViewById(R.id.listViewFriends);

        /*myAsyncTask<String, String, JSONObject> obj = new myAsyncTask<String,String, JSONObject>();
        try {
            jsonObject = obj.execute("https://api.vk.com/method/friends.get?user_id="+UserData.getUserId()+"&fields=nickname,photo_50").get();
            friendsNamesList = new Parse().getFriendsFullNamesfromJSONObjects(jsonObject);

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,friendsNamesList);
            //gridView.add;
            iconLink = new Parse().getFriendsPhotoLink(jsonObject);
            friendsId = new Parse().getFriendsId(jsonObject);
            //listViewFriends.setAdapter(adapter);
            Log.d("iii",iconLink.get(3));
            iconBitmap=new ArrayList<Bitmap>(friendsNamesList.size());


            } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        myAS<String,String,JSONObject> my= new myAS();
        my.execute(" ");

        String[] from = { "Photo", "Name" };

// Массив идентификаторов компонентов, в которые будем вставлять данные

        //Log.d("MAIN",iconLink.get(0)+UserData.friendsNamesList.get(0));
        int[] to = { R.id.image, R.id.Name};

        MySimpleAdapter adapter = new MySimpleAdapter(this,UserData.temp,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new NewViewBinder());
        listViewFriends.setAdapter(adapter);
        //Log.d("MAIN1", my.iconLink.get(1) + my.friendsNamesList.get(1));

        /*for (int i =0;i<friendsNamesList.size();i++)
        {
            Map<String,Object> m = new HashMap<String,Object>();

            m.put("Photo", iconBitmap.get(i));
            m.put("Name", friendsNamesList.get(i));
            array.add(m);
        }
        myAsyncTaskImage<String, Void, Bitmap> objImage = new myAsyncTaskImage<String,Void,Bitmap>();
        for (int i=0;i<x;i++)
        try
        {

                iconBitmap.add(new myAsyncTaskImage<String, Void, Bitmap>().execute(iconLink.get(i)).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/






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
            // метод супер-класса, который вставляет текст
            super.setViewText(v, text);
            //if (v.getId() == R.id.image)
             //   v.setText();
/*            v.setText(friendsNamesList.get(count));
            count++;
*/
        }

        @Override
        public void setViewImage(ImageView v, int value) {
            // метод супер-класса
            super.setViewImage(v, value);
            //Bitmap map = (Bitmap)data.get(0).get("Friends");
            //v.setImageBitmap(iconBitmap.get(count));
            // разрисовываем ImageView

        }
        /*@Override
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
