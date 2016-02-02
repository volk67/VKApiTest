package vladislav.ru.vkapitest;


import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Handler;

/**
 * Created by vladislav on 24.01.16.
 */
public class workApi
{
    private static UserData user = new UserData();
    Handler handler;

    public static UserData getUser() {
        return user;
    }


    public void setUserFriends() throws IOException, JSONException {
        user.setMyFriends(getFriendsList());
    }

    private void connect()
    {

    }

    public void downLoadPhotos() throws IOException {
        for (int i=0;i<user.getMyFriends().size();i++)
        {
            user.getMyFriends().get(i).addAvatar(downLoadPhoto(user.getMyFriends().get(i).getAvatar_url()));
        }
    }
    public Bitmap downLoadPhoto(String url) throws IOException {
        return new NetWork().readBitmap(url);
    }

    private List<Friend> getFriendsList() throws IOException, JSONException
    {
        List<Friend> friendsList = new ArrayList<>();
        String friends = new NetWork().readData("https://api.vk.com/method/friends.get?user_id="+workApi.user.getCurrentUserId()+"&fields=nickname,photo_50&order=hints");
        JSONObject object = new JSONObject(friends);
        List<String> friendsNameList = getFriendsFullNames(object);
        List<String> friendUiList = getFriendsId(object);
        List<String> friendsPhotoLink = getFriendsPhotoLink(object);

        for (int i=0;i<friendsNameList.size();i++)
        {
            friendsList.add(new Friend(friendUiList.get(i),friendsNameList.get(i),friendsPhotoLink.get(i)));
        }
        return friendsList;
    }

    private List<String> getFriendsFullNames(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray = new JSONArray();
        List<String> friendsNames = new ArrayList<>();
        jsonArray = jsonObject.getJSONArray("response");
        for (int i=0;i<jsonArray.length();i++)
        {
            friendsNames.add(jsonArray.getJSONObject(i).getString("first_name")+" "+jsonArray.getJSONObject(i).getString("last_name"));
        }
        return friendsNames;
    }
    private List<String> getFriendsId(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        List<String> friendsPhotoLink = new ArrayList<String>();
        jsonArray = jsonObject.getJSONArray("response");
        for (int i=0;i<jsonArray.length();i++)
        {
            friendsPhotoLink.add(jsonArray.getJSONObject(i).getString("uid"));
        }
        return friendsPhotoLink;
    }
    private List<String> getFriendsPhotoLink(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        List<String> friendsPhotoLink = new ArrayList<String>();
        jsonArray = jsonObject.getJSONArray("response");
        for (int i=0;i<jsonArray.length();i++)
        {
            friendsPhotoLink.add(jsonArray.getJSONObject(i).getString("photo_50"));
        }
        return friendsPhotoLink;
    }

}
