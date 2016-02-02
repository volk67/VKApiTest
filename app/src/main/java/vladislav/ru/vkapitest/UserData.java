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
    private static String accessToken;
    private static String currentUserId;
    private static List<Friend> myFriends;
    //public static ArrayList<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();
    private static String currentFriend;
    private static ArrayList<Bitmap> iconBitmap=new ArrayList<Bitmap>();

    public void setMyFriends(List<Friend> myFriends) {
        UserData.myFriends = myFriends;
    }

    public List<Friend> getMyFriends() {
        return myFriends;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setCurrentUserId(String userId) {
        UserData.currentUserId = userId;
    }

    public static void setAccessToken(String accessToken) {
        UserData.accessToken = accessToken;
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
