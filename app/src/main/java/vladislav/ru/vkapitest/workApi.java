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
import android.util.Log;

/**
 * Created by vladislav on 24.01.16.
 */
public class workApi
{
    private static UserData user = new UserData();

    public static UserData getUser() {
        return user;
    }

    public void setUserFriends() throws IOException, JSONException {
        user.setMyFriends(getFriendsList());
    }

    public void setUserFriendsOnline() throws IOException, JSONException {
        user.setMyFriendsOnline(getFriendsOnlineList());
    }

    public void downFriendUrls(int offset, Friend friend) throws IOException, JSONException {
        try {
            friend.addPhotosUrl(downLoadPhotosUrls(offset, friend.getUserId()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private List<Friend> getFriendsList() throws IOException, JSONException
    {
        List<Friend> friendsList = new ArrayList<>();
        String friends = new NetWork().readData("https://api.vk.com/method/friends.get?user_id=" + workApi.user.getCurrentUserId() + "&fields=nickname,photo_50,online&order=hints");
        Log.d("friends",friends);
        JSONObject object = new JSONObject(friends);
        JSONArray jsonArray = object.getJSONArray("response");
        for (int i=0;i<jsonArray.length();i++)
        {
            String name=jsonArray.getJSONObject(i).getString("first_name")+" "+jsonArray.getJSONObject(i).getString("last_name");
            String uid=jsonArray.getJSONObject(i).getString("uid");
            String avatarUrl=jsonArray.getJSONObject(i).getString("photo_50");
            String stringOnline = jsonArray.getJSONObject(i).getString("online");
            boolean online=stringOnline.equals("1")?true:false;
            friendsList.add(new Friend(uid,name,avatarUrl,online));
        }
        return friendsList;
    }
    private List<Friend> getFriendsOnlineList() throws IOException, JSONException {
        List<Friend> friendsList = new ArrayList<>();
        String friends = new NetWork().readData("https://api.vk.com/method/friends.get?user_id=" + workApi.user.getCurrentUserId() + "&fields=nickname,photo_50,online&order=hints");
        Log.d("friends",friends);
        JSONObject object = new JSONObject(friends);
        JSONArray jsonArray = object.getJSONArray("response");
        for (int i=0;i<jsonArray.length();i++)
        {
            String name=jsonArray.getJSONObject(i).getString("first_name")+" "+jsonArray.getJSONObject(i).getString("last_name");
            String uid=jsonArray.getJSONObject(i).getString("uid");
            String avatarUrl=jsonArray.getJSONObject(i).getString("photo_50");
            String online=jsonArray.getJSONObject(i).getString("online");
            if (online.equals("1"))
            friendsList.add(new Friend(uid,name,avatarUrl));
        }
        return friendsList;
    }


    public List<String> downLoadPhotosUrls(int offset, String uid) throws IOException, JSONException {
        List<String> urlList = new ArrayList<>();
        String urls = new NetWork().readData("https://api.vk.com/method/photos.getAll?owner_id="+uid+"&count="+20+"&offset="+offset+"&access_token="+UserData.getAccessToken());
        JSONObject object = new JSONObject(urls);
        JSONArray jsonArray = object.getJSONArray("response");
        for (int i=1;i<jsonArray.length();i++)
        {
            urlList.add(jsonArray.getJSONObject(i).getString("src"));
        }

        return urlList;
    }

    public List<List<String>> downLoadMessages(int offset, String uid) throws IOException, JSONException {
        List<String> messagesList = new ArrayList<>();
        List<String> messagesListFromTo = new ArrayList<>();
        String messages = new NetWork().readData("https://api.vk.com/method/messages.getHistory?user_id=" + uid + "&access_token=" + UserData.getAccessToken()+"&count=20");
        JSONObject jsonObject= new JSONObject(messages);
        JSONArray jsonArray= jsonObject.getJSONArray("response");
        Log.d("messages", messages);
        for (int i=1;i<jsonArray.length();i++)
        {
            messagesList.add(jsonArray.getJSONObject(i).getString("body"));
            messagesListFromTo.add(jsonArray.getJSONObject(i).getString("out"));
        }
        List<List<String>> list = new ArrayList<>();
        list.add(messagesList);
        list.add(messagesListFromTo);
        return list;
    }


    public void downLoadAvatarUrl() throws IOException, JSONException {
        String me = new NetWork().readData("https://api.vk.com/method/users.get?user_id="+user.getCurrentUserId()+"&fields=photo_50");
        JSONObject jsonObject = new JSONObject(me);
        String url = jsonObject.getJSONArray("response").getJSONObject(0).getString("photo_50");
        user.setAvatarUrl(url);
    }
    public void downLoadAvatar() throws IOException, JSONException {
        downLoadAvatarUrl();
        Bitmap avatar = new NetWork().readBitmap(user.getAvatarUrl());
        user.setAvatar(avatar);
    }

    public void sendMessage(String uid, String message) throws IOException {
        String mess = new NetWork().readData("https://api.vk.com/method/messages.send?uid="+uid+"&message="+message+"&access_token=" + UserData.getAccessToken());
        Log.d("mess",mess);
        String s="https://api.vk.com/method/messages.send?uid="+uid+"&message="+message+"&access_token=" + UserData.getAccessToken();
        Log.d("quest", s);
    }
}
