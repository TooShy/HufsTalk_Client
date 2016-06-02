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
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import tooshy.hufstalk.R;
import tooshy.hufstalk.helper.Global;

/**
 * Created by tokirin on 16. 5. 29.
 */
public class FilterActivity extends Activity {

    Button filterNextBtn;
    EditText filterList;
    LinearLayout progressLayout;
    RequestQueue Queue;
    Global global = Global.getInstance();
    Pusher pusher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        Queue = Volley.newRequestQueue(getApplicationContext());
        pusher = global.pusher;

        filterList = (EditText) findViewById(R.id.filter_list);
        filterNextBtn = (Button) findViewById(R.id.filter_next_btn);
        progressLayout = (LinearLayout) findViewById(R.id.filter_progress_layout);

        filterNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLayout.setVisibility(View.VISIBLE);
                String listStr = filterList.getText().toString();
                JSONObject setTopicParams = new JSONObject();
                try {
                    setTopicParams.put("token",global.SESSION_TOKEN);
                    setTopicParams.put("filter_string",listStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest setTopicRequest = new JsonObjectRequest(Request.Method.POST,
                        global.HOST_API_PREFIX + global.API_VERSION + "/users/set_filter" ,setTopicParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code") == 200) {
                                progressLayout.setVisibility(View.INVISIBLE);
                                finish();
                                Intent intent = new Intent(getApplicationContext(),TopicActivity.class);
                                startActivity(intent);
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
    }
}
