package tooshy.hufstalk.activity;

/**
 * Created by USER on 2016-04-30.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.Arrays;

import tooshy.hufstalk.R;

/**
 * Created by USER on 2016-04-23.
 */
public class LoginActivity extends Activity {
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String[] id = new String[2];

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {
                        } else {
                            try {
                                id[0] = user.get("id").toString();
                                id[1] = user.get("gender").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());
                            setResult(RESULT_OK);
                            Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
        //POST
        RequestQueue Queue = Volley.newRequestQueue(this);
        JSONObject param = new JSONObject();
        Boolean gender;
        if (id[1]=="male"){
            gender=true;
        }
        else{
            gender=false;
        }
        try {
            param.put("uid", id[0]);
            param.put("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://125.209.199.214:3000/api/client/v1/users/join", param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Response1", response.toString());
                try {
                    System.out.println(response.getJSONObject("data").getJSONObject("session").get("token").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Response2", error.toString());
            }
        });
        Queue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}




