package tooshy.hufstalk.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Queue;

import tooshy.hufstalk.R;
import tooshy.hufstalk.adapter.ChatArrayAdapter;
import tooshy.hufstalk.helper.Global;
import tooshy.hufstalk.model.ChatMessage;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    PusherOptions options = new PusherOptions();
    LinearLayout container;

    Pusher pusher;
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private String chatTextOthers;
    private Button buttonSend;
    Intent intent;
    private boolean right = false;
    private boolean left = true;
    public boolean resive = false;
    private Handler mMainHandler;
    Global global = Global.getInstance();
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        mMainHandler = new Handler();
        buttonSend = (Button) findViewById(R.id.buttonSend);
        listView = (ListView) findViewById(R.id.listView1);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.chatText);
        container = (LinearLayout) findViewById(R.id.nav_report);

        pusher = global.pusher;



        String channel_name = this.getIntent().getStringExtra("channel_name");
        Log.v("Hufstalk", "current channel name : " + channel_name);
        // Pusher API 이용
        Channel channel = pusher.subscribe(channel_name);




        channel.bind("chat", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(data);
                try {
                    JSONObject message_data = new JSONObject(data);
                    String message_content = message_data.getString("message");
                    System.out.println("Received Data String: " + message_content);
                    chatTextOthers = message_content;
                    resive = false;
                    mMainHandler.post(mRunnable);

                    System.out.println("Received Data String: " + resive);

                    sendChatMessage(false);

                    // 리스트에 채팅 메시지 추가(상대방)
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Channel ban =pusher.subscribe(channel_name);
        ban.bind("ban", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, String token) {
                if (token == global.SESSION_TOKEN) {
                    Toast.makeText(getApplicationContext(), "당신은 금지어를입력해 채팅방에서 강퇴당하셨습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "상대방이 금지어를입력해 채팅이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                    startActivity(intent);
                    finish();
                }
            }


        });

        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage(true);
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage(true);
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            sendChatMessage(false);
        }
    };


    private boolean sendChatMessage(boolean a){
        if (a==true) {
            chatArrayAdapter.add(new ChatMessage(right, chatText.getText().toString()));
            chatText.setText("");
        }else{
            chatArrayAdapter.add(new ChatMessage(left, chatTextOthers));
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_report) {

            // Handle the camera action
        } else if (id == R.id.nav_qanda) {

        } else if (id == R.id.nav_logout){
            System.out.println("확인");
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
