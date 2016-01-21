package vladislav.ru.vkapitest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    ImageView myImage;
    WebView webView;
    UserData myUserData;
    ListView listView;
    ArrayList<String> menuList;
    Bitmap photo50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        webView.loadUrl("https://oauth.vk.com/authorize?client_id=5217160&scope=messages,offline,photos,friends&response_type=token&v=5.40");

    }







    public void goToMenuLayout() throws ExecutionException, InterruptedException {
        setContentView(R.layout.menu);

        listView = (ListView) findViewById(R.id.listView);
        Menu m = new Menu();
        menuList = m.getMenuList();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menuList);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                    startActivity(intent);
                }
            }
        });
    }




    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url.startsWith("https://oauth.vk.com/blank.html")) {
                myUserData.setAccessToken(Parse.getAccessToken(url));
                myUserData.setUserId(Parse.getUserId(url));

                Log.d("TAG", myUserData.getAccessToken());
                Log.d("TAG", myUserData.getUserId());

                try {
                    goToMenuLayout();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Parse {
        public static String getAccessToken(String url) {
            if (url.startsWith("https://oauth.vk.com/blank.html")) {
                int startNumber = url.lastIndexOf("access_token");
                int finishNumber = url.indexOf("&");
                String s = url.substring(startNumber + 13, finishNumber);
                return s;
            }
            return "error";
        }

        public static String getUserId(String url) {
            if (url.startsWith("https://oauth.vk.com/blank.html")) {
                int startNumber = url.lastIndexOf("user_id");
                String s = url.substring(startNumber + 8);
                return s;
            }
            return "error";

        }
    }

    public class Menu {
        private ArrayList<String> menuList;

        public Menu() {
            Log.d("pf", "pfitk");
            menuList = new ArrayList<String>();
            menuList.add("Friends");
            Log.d("t", menuList.get(0));
        }

        public ArrayList<String> getMenuList() {
            return menuList;
        }
    }
}
