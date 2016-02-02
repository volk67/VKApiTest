package vladislav.ru.vkapitest;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by vladislav on 24.01.16.
 */
public class Friend
{
    private String userId;
    private String fullName;
    private String avatar_url;
    private Bitmap avatar;
    private List<Bitmap> photos;

    Friend()
    {

    }

    Friend(String userId)
    {
        this.userId=userId;
    }
    Friend(String userId,String fullName)
    {
        this.userId=userId;
        this.fullName=fullName;
    }
    Friend(String userId, String fullName, String avatar_url)
    {
        this.userId=userId;
        this.fullName=fullName;
        this.avatar_url=avatar_url;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void addAvatar(Bitmap bitmap)
    {
        this.avatar=bitmap;
    }



}
