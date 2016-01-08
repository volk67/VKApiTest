package vladislav.ru.vkapitest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by vladislav on 08.01.16.
 */
public class FriendActivity extends Activity {

    ListView listViewFriends;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listViewFriends = (ListView) findViewById(R.id.listViewFriends);

        myAsyncTask<String, String, JSONObject> obj = new myAsyncTask<String,String, JSONObject>();
        try {
            jsonObject = obj.execute("https://api.vk.com/method/friends.get?user_id="+UserData.getUserId()+"&fields=nickname,photo_50").get();
            List<String> friendsNamesList = new Parse().getFriendsFullNamesfromJSONObjects(jsonObject);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,friendsNamesList);
            //gridView.add;

            listViewFriends.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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
            for (int i=0;i<jsonArray.length();i++)
            {
                friendsNames.add(jsonArray.getJSONObject(i).getString("first_name")+" "+jsonArray.getJSONObject(i).getString("last_name"));
            }
            return friendsNames;
        }
    }

}
