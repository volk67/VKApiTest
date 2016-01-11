package vladislav.ru.vkapitest;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class myAsyncTaskImage<String, Void, Bitmap> extends AsyncTask<String, Void, Bitmap> {

    private Exception exception;
    @Override
    protected Bitmap doInBackground(String... urls) {
        try
        {
            URL urlAPI = new URL((java.lang.String) urls[0]);
            HttpURLConnection urlConnection;
            urlConnection = (HttpURLConnection) urlAPI.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            BufferedInputStream reader =new BufferedInputStream(urlConnection.getInputStream());

            Bitmap map = (Bitmap) BitmapFactory.decodeStream(reader);
            return map;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exception = e;
        }

        return null;
    }
    protected void onPostExecute(Bitmap feed) {
        // TODO: check this.exception
        // TODO: do something with the feed

    }
}

