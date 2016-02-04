package vladislav.ru.vkapitest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by vladislav on 08.01.16.
 */
public class FriendActivity extends Activity {

    FriendsActivityAdapter adapter;
    ListView listViewFriends;
    Handler handler;
    UserData user = workApi.getUser();

    public static ArrayList<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listViewFriends = (ListView)findViewById(R.id.listViewFriends);

        String[] from = { "Photo", "Name" };
        int[] to = { R.id.image, R.id.Name};
        adapter = new FriendsActivityAdapter(this,temp,R.layout.forsimpleadapter,from,to);
        adapter.setViewBinder(new myBinder());
        new DownLoadFriendsList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        listViewFriends.setAdapter(adapter);


        handler = new Handler() {
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();

            };
        };

        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=Long.toString(id);
                Log.d("id=",s+" "+position+view.toString());
            }
        });

        listViewFriends.setOnScrollListener(new AbsListView.OnScrollListener() {
            int first,count;
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                if (arg1==0)
                    for (int i=first;i<first+count;i++)
                    {
                        new DownLoadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,i);
                    }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                first=firstVisibleItem;
                count=visibleItemCount;
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
                return null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                exception = e;
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            for (int i=0;i<user.getMyFriends().size();i++)
            {
                Map m = new HashMap<>();
                m.put("Photo", null);
                m.put("Name", user.getMyFriends().get(i).getFullName());
                temp.add(m);
            }

            adapter.notifyDataSetChanged();
        }
    }


    class DownLoadImage extends AsyncTask<Integer, Void, Void> {
        private Exception exception;
        @Override
        protected Void doInBackground(Integer[] nums) {
            try {
                if (user.getMyFriends().get(nums[0]).getAvatar()==null)
                {
                    user.getMyFriends().get(nums[0]).downLoadAvatar();
                    Map m = new HashMap<>();
                    m.put("Photo", user.getMyFriends().get(nums[0]).getAvatar());
                    m.put("Name", user.getMyFriends().get(nums[0]).getFullName());
                    temp.set(nums[0],m);
                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            super.onPostExecute(feed);
            adapter.notifyDataSetChanged();
        }
    }

    class FriendsActivityAdapter extends SimpleAdapter
    {
        Context context;
        List<Map<String,Object>> data;
        ImageView imageView;
        public FriendsActivityAdapter(Context context, List<Map<String,Object>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context=context;
            this.data=data;
        }
        class ViewHolder
        {
            protected ImageView imageView;
            protected TextView textView;
        }

        @Override
        public void setViewImage(ImageView view, int value)
        {
            super.setViewImage(view, value);
        }

        @Override
        public void setViewText(TextView view, String text)
        {
            super.setViewText(view, text);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.forsimpleadapter,null);
            }else {
                view = convertView;
            }
            final ViewHolder viewHolder = new ViewHolder();



            viewHolder.imageView=(ImageView)view.findViewById(R.id.image);
            viewHolder.textView=(TextView)view.findViewById(R.id.Name);
            viewHolder.textView.setText(data.get(position).get("Name").toString());
            viewHolder.imageView.setImageBitmap((Bitmap) data.get(position).get("Photo"));
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoIntent = new Intent(FriendActivity.this, PhotoFriends.class);
                    photoIntent.putExtra("position", position);
                    startActivity(photoIntent);
                }
            });
            viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent messagesIntent = new Intent(FriendActivity.this, MessagesActivity.class);
                    messagesIntent.putExtra("position", position);
                    startActivity(messagesIntent);
                }
            });

            return view;
        }
    }
}
