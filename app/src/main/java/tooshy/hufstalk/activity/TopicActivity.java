package tooshy.hufstalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParser;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import tooshy.hufstalk.R;
import tooshy.hufstalk.helper.Global;

/**
 * Created by USER on 2016-04-09.
 */
public class TopicActivity extends Activity{
    Button startChatting;
    EditText topicList;
    LinearLayout progressLayout;
    RequestQueue Queue;
    Global global = Global.getInstance();
    Pusher pusher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_activity);
        Queue = Volley.newRequestQueue(getApplicationContext());
        pusher = global.pusher;

        topicList = (EditText) findViewById(R.id.topic_list);
        startChatting = (Button) findViewById(R.id.chatting_start);
        progressLayout = (LinearLayout) findViewById(R.id.match_progress_layout);

        startChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String listStr = topicList.getText().toString();
                JSONObject setTopicParams = new JSONObject();
                try {
                    setTopicParams.put("token",global.SESSION_TOKEN);
                    setTopicParams.put("topic_list",listStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest setTopicRequest = new JsonObjectRequest(Request.Method.POST,
                        global.HOST_API_PREFIX + global.API_VERSION + "/topics/set", setTopicParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code") == 200) {
                                sendMatchRequest();

                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Hufstalk", error.getMessage().toString());

                    }
                });
                Queue.add(setTopicRequest);
            }
        });

        Channel channel = pusher.subscribe(global.SESSION_TOKEN);
        channel.bind("matched", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressLayout.setVisibility(View.INVISIBLE);
                        String channel_name = "";
                        try {
                            JSONObject channel_data = new JSONObject(data);
                            channel_name = channel_data.getString("channel_name");
                            Log.v("Hufstalk", "Received Channel Name : " + channel_name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("channel_name",channel_name);
                        startActivity(intent);
                    }
                });

            }
        });

    }

    public void sendMatchRequest(){

        JSONObject matchParams = new JSONObject();
        try {
            matchParams.put("token",global.SESSION_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest matchRequest = new StringRequest(Request.Method.GET,
                global.HOST_API_PREFIX + global.API_VERSION + "/chat/match?token=" + global.SESSION_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("Hufstalk", "Start Matching");
                progressLayout.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Hufstalk", error.getMessage().toString());
            }
        });
        Queue.add(matchRequest);
    }
}

