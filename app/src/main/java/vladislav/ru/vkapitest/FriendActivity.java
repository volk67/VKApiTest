package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by vladislav on 08.01.16.
 */
public class FriendActivity extends Activity {

    ArrayList<Bitmap> iconBitmap=new ArrayList<Bitmap>();
    ImageAdapter adapter;
    ListView listViewFriends;
    Handler handler,handlerFriendsDownLoad;
    UserData user = workApi.getUser();

    public static ArrayList<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listViewFriends = (ListView)findViewById(R.id.listViewFriends);

        String[] from = { "Photo", "Name" };
        int[] to = { R.id.image, R.id.Name};
        adapter = new ImageAdapter(this,temp,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new myBinder());
        new DownLoadFriendsList().execute();


        listViewFriends.setAdapter(adapter);


        handler = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();

            };
        };
        handlerFriendsDownLoad = new Handler() {
            public void handleMessage(Message msg) {
                for (int i=0;i<user.getMyFriends().size();i++)
                {
                    Map m = new HashMap<>();
                    m.put("Photo", null);
                    m.put("Name", user.getMyFriends().get(i).getFullName());
                    temp.add(m);
                }

                adapter.notifyDataSetChanged();

                new DownLoadImages().execute();

            };
        };
        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=Long.toString(id);
                Log.d("id=",s+" "+position+view.toString());
                /*if (position<10)
                {
                    UserData.setCurrentFriend(friendsId.get(position));
                    Intent intent2 = new Intent(FriendActivity.this, PhotoFriends.class);
                    startActivity(intent2);
                }
                else
                {
                    UserData.setCurrentFriend(friendsId.get(position));
                    Intent messagesIntent = new Intent(FriendActivity.this, MessagesActivity.class);
                    startActivity(messagesIntent);
                }*/
            }
        });
    }


    class DownLoadFriendsList extends AsyncTask<String, Void, Void> {

        private Exception exception;
        @Override
        protected Void doInBackground(String[] urls) {
            try
            {
                new workApi().setUserFriends();
                handlerFriendsDownLoad.sendEmptyMessage(0);
                return null;
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

    class DownLoadImages extends AsyncTask<String, Void, Bitmap> {
        private Exception exception;
        @Override
        protected Bitmap doInBackground(String[] urls) {
            try {
                int i=0;
                for (Friend friend:user.getMyFriends())
                {

                    friend.downLoadAvatar();
                    Map m = new HashMap<>();
                    m.put("Photo", friend.getAvatar());
                    m.put("Name", friend.getFullName());
                    temp.remove(i);
                    temp.add(i, m);
                    i++;
                    handler.sendEmptyMessage(0);
                }
                return null;
            } catch (Exception e) {
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
}
