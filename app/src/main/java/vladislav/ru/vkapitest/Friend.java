package vladislav.ru.vkapitest;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
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
    private List<String> photosUrl = new ArrayList<>();
    private List<Bitmap> photos;
    private List<String> messages = new ArrayList<>();
    private List<String> fromTo = new ArrayList<>();


    Friend(){}

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

    public void downLoadAvatar() throws IOException {
        avatar = new NetWork().readBitmap(avatar_url);
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

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public List<String> getPhotosUrl() {
        return photosUrl;
    }

    public void setPhotosUrl(List<String> photosUrl) {
        this.photosUrl = photosUrl;
    }

    public void addPhotosUrl(List<String> photosUrl)
    {
        this.photosUrl.addAll(photosUrl);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessages(List<String> messages)
    {
        this.messages.addAll(messages);
    }

    public List<String> getFromTo() {
        return fromTo;
    }

    public void setFromTo(List<String> fromTo) {
        this.fromTo = fromTo;
    }
    public void addFromTo(List<String> messages)
    {
        this.fromTo.addAll(messages);
    }
}
