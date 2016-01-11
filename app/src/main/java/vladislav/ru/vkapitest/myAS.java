package vladislav.ru.vkapitest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 11.01.16.
 */
public class myAS<S, S1, J> extends AsyncTask<String, String, JSONObject> {
    ArrayList<Bitmap> mass;
    JSONObject jsonObject;
    List<String> friendsNamesList;


    List<String> friendsId;
    List<Map<String,Object>> strr=new ArrayList<Map<String,Object>>();
    private Exception exception;
    @Override
    protected JSONObject doInBackground(String... urls) {
        //strr=new ArrayList<Map<String,Object>>();
        try
        {
            URL urlAPI = new URL("https://api.vk.com/method/friends.get?user_id="+UserData.getUserId()+"&fields=nickname,photo_50");
            HttpsURLConnection urlConnection = (HttpsURLConnection) urlAPI.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            //urlConnection.disconnect();

            String f = bufReader.readLine();
            JSONObject jsonObject = new JSONObject(f);
            UserData.setFriendNames(new Parse().getFriendsFullNamesfromJSONObjects(jsonObject));

            UserData.setIconLink(new Parse().getFriendsPhotoLink(jsonObject));
            friendsId = new Parse().getFriendsId(jsonObject);

            //iconBitmap=new ArrayList<Bitmap>();

           // Log.d("THREAD",FriendActivity.iconLink.get(0)+friendsNamesList.get(0));

            for (int i =0;i<UserData.getFriendNamesList().size();i++)
            {
                URL urlAPI2 = new URL( UserData.getIconLink().get(i));
                HttpURLConnection urlConnection2;
                urlConnection2 = (HttpURLConnection) urlAPI2.openConnection();
                urlConnection2.setRequestMethod("GET");
                urlConnection2.connect();
                BufferedInputStream reader2 =new BufferedInputStream(urlConnection2.getInputStream());

                Bitmap map = (Bitmap) BitmapFactory.decodeStream(reader2);
                Map<String,Object> m = new HashMap<String,Object>();

                m.put("Photo", map);
                m.put("Name", UserData.getFriendNamesList().get(i));
                UserData.temp.add(m);
                urlConnection2.disconnect();

            }
            Log.d("THREAD",UserData.getIconLink().get(1)+UserData.getFriendNamesList().get(1));

            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exception = e;
        }

        return null;
    }
    protected void onPostExecute(JSONObject feed) {
        /*// TODO: check this.exception
        // TODO: do something with the feed*/

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
        public ArrayList<String> getFriendsFullNamesfromJSONObjects(JSONObject jsonObject) throws JSONException {

            JSONArray jsonArray = new JSONArray();
            ArrayList<String> friendsNames = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsNames.add(jsonArray.getJSONObject(i).getString("first_name")+" "+jsonArray.getJSONObject(i).getString("last_name"));
            }
            return friendsNames;
        }
        public ArrayList<String> getFriendsPhotoLink(JSONObject jsonObject) throws JSONException {
            JSONArray jsonArray = new JSONArray();
            ArrayList<String> friendsPhotoLink = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsPhotoLink.add(jsonArray.getJSONObject(i).getString("photo_50"));
            }
            return friendsPhotoLink;
        }
        public ArrayList<String> getFriendsId(JSONObject jsonObject) throws JSONException {
            JSONArray jsonArray = new JSONArray();
            ArrayList<String> friendsPhotoLink = new ArrayList<String>();
            jsonArray = jsonObject.getJSONArray("response");
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsPhotoLink.add(jsonArray.getJSONObject(i).getString("uid"));
            }
            return friendsPhotoLink;
        }

    }
}

