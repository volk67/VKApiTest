package vladislav.ru.vkapitest;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.io.IOException;
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
    private String avatarUrl;
    private Bitmap avatar;
    private static List<Friend> myFriends;
    private static List<Friend> myFriendsOnline;
    //public static ArrayList<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();
    private static String currentFriend;
    private static ArrayList<Bitmap> iconBitmap=new ArrayList<Bitmap>();

    public void setMyFriends(List<Friend> myFriends) {
        UserData.myFriends = myFriends;
    }

    public List<Friend> getMyFriends() {
        return myFriends;
    }

    public static List<Friend> getMyFriendsOnline() {
        return myFriendsOnline;
    }

    public static void setMyFriendsOnline(List<Friend> myFriendsOnline) {
        UserData.myFriendsOnline = myFriendsOnline;
    }

    public String getCurrentUserId() {
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

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public void downLoadAvatar() throws IOException {
        this.avatar=new NetWork().readBitmap(avatarUrl);

    }


}
