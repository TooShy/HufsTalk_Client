package tooshy.hufstalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    String listStr="";
    private ListView  listView;
    private ArrayList<String> arraylist;
    private ArrayAdapter<String> adapter;
    String[] in = {"","","","","","","","","",""};
    int[] i= new int[10];


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_activity);
        Queue = Volley.newRequestQueue(getApplicationContext());
        pusher = global.pusher;
        final String[] topic = new String[10];







        startChatting = (Button) findViewById(R.id.chatting_start);
        progressLayout = (LinearLayout) findViewById(R.id.match_progress_layout);


        JsonObjectRequest listRequest = new JsonObjectRequest(Request.Method.GET,
                global.HOST_API_PREFIX + global.API_VERSION + "/topics/total_topic_list", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray topicList = data.getJSONArray("topic_list");

                    for(int i = 0; i<topicList.length(); i++){
                        JSONObject order = topicList.getJSONObject(i);
                        topic[i] = order.get("topic_name").toString();
                        in[i] = order.get("topic_name").toString();
                        System.out.println(topic[i]);
                        //System.out.println("asdf");
                    }
                    make();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Join Response Error", error.toString());
            }
        });
        Queue.add(listRequest);

        setResult(RESULT_OK);
        /*
        System.out.println("start");
        for (int i=0;i<10;i++){
            System.out.println(in[i]);
        }
*/









        //Topic Activity Working on..
        startChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String listStr = topicList.getText().toString();
                JSONObject setTopicParams = new JSONObject();
                try {
                    setTopicParams.put("token", global.SESSION_TOKEN);
                    setTopicParams.put("topic_list", listStr);
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
                        intent.putExtra("channel_name", channel_name);
                        startActivity(intent);
                    }
                });

            }
        });

    }

    public void make(){
        arraylist = new ArrayList<String>();
        for (int i=0;i<10;i++) {
            arraylist.add(in[i]);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arraylist);
        ListView listView = (ListView) findViewById(R.id.topic_list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String item = (String) adapter.getItem(position);
                if (listStr == "") {
                    listStr = item;
                } else {
                    listStr = listStr + "," + item;
                }
                if(i[position]==0) {
                    view.setBackgroundColor(Color.parseColor("#416BC1"));
                    i[position]=1;
                }
                else{
                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                    i[position]=0;
                }
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

