package vladislav.ru.vkapitest;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vladislav on 08.01.16.
 */
public class myAsyncTask<S, S1, J> extends AsyncTask<String, String, JSONObject> {

    private Exception exception;
    @Override
    protected JSONObject doInBackground(String... urls) {
        try
        {
            URL urlAPI = new URL(urls[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) urlAPI.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String f = bufReader.readLine();
            JSONObject jsonObject = new JSONObject(f);
            JSONArray jsonArray = new JSONArray();
            jsonArray = jsonObject.getJSONArray("response");
            return jsonObject;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exception = e;
        }

        return null;
    }
    protected void onPostExecute(JSONObject feed) {
        // TODO: check this.exception
        // TODO: do something with the feed

    }
}
