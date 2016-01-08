package vladislav.ru.vkapitest;

import org.json.JSONArray;

/**
 * Created by vladislav on 08.01.16.
 */
public class UserData
{
    private static String accessToken;
    private static String userId;
    private static JSONArray friends;

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
}
