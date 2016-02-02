package vladislav.ru.vkapitest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 24.01.16.
 */
public class NetWork
{
    public Bitmap readBitmap(String urls) throws IOException
    {
        BufferedInputStream IS = new BufferedInputStream(setConnectWithHttp(urls));
        Bitmap map=(Bitmap) BitmapFactory.decodeStream(IS);
        return map;
    }
    public String readData(String url) throws IOException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(setConnect(url)));
        return reader.readLine();
    }


    private InputStream setConnect(String url) throws IOException
    {
        if (url.startsWith("http")) return setConnectWithHttp(url);
        else if (url.startsWith("https")) return setConnectWithHttps(url);
        else return null;
    }

    private InputStream setConnectWithHttp(String urls) throws IOException
    {
        URL url = new URL(urls);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
    private InputStream setConnectWithHttps(String urls) throws IOException
    {
        URL url = new URL(urls);
        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
}
