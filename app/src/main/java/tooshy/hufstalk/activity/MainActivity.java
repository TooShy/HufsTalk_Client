package tooshy.hufstalk.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.FacebookSdk;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import tooshy.hufstalk.R;
import tooshy.hufstalk.adapter.ChatArrayAdapter;
import tooshy.hufstalk.model.ChatMessage;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    PusherOptions options = new PusherOptions();

    Pusher pusher;
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private String chatText1;
    private Button buttonSend;
    Intent intent;
    private boolean right = false;
    private boolean left = true;
    public boolean resive = false;
    private Handler mMainHandler;
    @Override
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

        options.setCluster("ap1");
        pusher = new Pusher("f5ad826261aeb8068be6", options);
        // Pusher API 이용
        Channel channel = pusher.subscribe("test_channel");
        channel.bind("my_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(data);

                try {
                    JSONObject message_data = new JSONObject(data);
                    String message_content = message_data.getString("message");
                    System.out.println("Received Data String: " + message_content);
                    chatText1 = message_content;
                    resive = false;
                    mMainHandler.post(mRunnable);

                    System.out.println("Received Data String: " + resive);

                    // sendChatMessage(false);

                    // 리스트에 채팅 메시지 추가(상대방)
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                System.out.println("Connection State changed!");
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                System.out.println(s);
                System.out.println(s1);
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
    }
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            sendChatMessage(false);
        }
    };


    private boolean sendChatMessage( boolean a){
        if (a==true) {
            chatArrayAdapter.add(new ChatMessage(right, chatText.getText().toString()));
            chatText.setText("");
        }else{
            chatArrayAdapter.add(new ChatMessage(left, chatText1));
        }
        return true;
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
