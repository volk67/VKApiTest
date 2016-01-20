package vladislav.ru.vkapitest;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vladislav on 08.01.16.
 */
public class UserData
{
    private static String currentFriend;
    private static String accessToken;
    private static String userId;
    private static JSONArray friends;
    private static ArrayList<String> friendNamesList= new ArrayList<String>();
    private static ArrayList<String> iconLink=new ArrayList<String>();
    public static ArrayList<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();

    private static ArrayList<Bitmap> iconBitmap=new ArrayList<Bitmap>();

    public static String getUserId() {
        return userId;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setUserId(String userId) {
        UserData.userId = userId;
    }

    public static void setAccessToken(String accessToken) {
        UserData.accessToken = accessToken;
    }

    public static void setFriends(JSONArray friends) {
        UserData.friends = friends;
    }

    public static void setFriendNames(ArrayList<String> friendNames) {
        UserData.friendNamesList = friendNames;
    }

    public static ArrayList<String> getFriendNamesList() {
        return friendNamesList;
    }


    public static ArrayList<String> getIconLink() {
        return iconLink;
    }

    public static void setIconLink(ArrayList<String> iconLink) {
        UserData.iconLink = iconLink;
    }

    public static void setIconBitmap(ArrayList<Bitmap> iconBitmap) {
        UserData.iconBitmap = iconBitmap;
    }

    public static ArrayList<Bitmap> getIconBitmap() {
        return iconBitmap;
    }

    public static String getCurrentFriend() {
        return currentFriend;
    }

    public static void setCurrentFriend(String currentFriend) {
        UserData.currentFriend = currentFriend;
    }
}
