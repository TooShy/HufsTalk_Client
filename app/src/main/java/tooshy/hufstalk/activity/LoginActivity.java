package tooshy.hufstalk.activity;

/**
 * Created by USER on 2016-04-30.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.util.Arrays;

import tooshy.hufstalk.helper.Global;

/**
 * Created by USER on 2016-04-23.
 */
public class LoginActivity extends Activity{
    private CallbackManager callbackManager;
    Global global = Global.getInstance();
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String[] joinResponseData = new String[2];
    RequestQueue Queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        editor = prefs.edit();
        Queue = Volley.newRequestQueue(getApplicationContext());

        /**
         * 기존 세션 토큰이 존재하지 않는다면
         */

        if(prefs.getString("token","").equals("")){
            Log.v("Hufstalk", "First Login!");

            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,Arrays.asList("public_profile", "email"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(final LoginResult result) {

                    GraphRequest request;
                    request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject user, GraphResponse response) {

                            if (response.getError() == null) {
                                Log.v("Hufstalk", "Facebook Login Success");
                                try {
                                    joinResponseData[0] = user.get("id").toString();
                                    joinResponseData[1] = user.get("gender").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                /*
                                 * POST: /users/join 가입
                                 */

                                JSONObject joinParams = new JSONObject();
                                Boolean gender;
                                if (joinResponseData[1] == "male") {
                                    gender = true;
                                } else {
                                    gender = false;
                                }
                                try {
                                    joinParams.put("uid", joinResponseData[0]);
                                    joinParams.put("gender", gender);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.v("Hufstalk","My UID is " + joinResponseData[0]);

                                JsonObjectRequest joinRequest = new JsonObjectRequest(Request.Method.POST,
                                        global.HOST_API_PREFIX + global.API_VERSION + "/users/join", joinParams, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.v("Hufstalk", "Join Success");
                                        showLoginDialog();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Join Response Error", error.toString());
                                    }
                                });
                                Queue.add(joinRequest);
                                setResult(RESULT_OK);
                            }else{
                                Log.v("Hufstalk", "Facebook Login Failed");
                                Log.v("Hufstalk", response.getError().toString());
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

        }else{
            global.SESSION_TOKEN = prefs.getString("token","");
            Log.v("Hufstalk", "Current Token is " + global.SESSION_TOKEN);
            // 필터 액티비티 시작
            Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
            finish();
            startActivity(intent);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showLoginDialog(){
        Queue = Volley.newRequestQueue(getApplicationContext());
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("로그인 하시겠습니까?").setCancelable(
                false).setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        Log.v("Hufstalk","로그인 시퀀스 시작");
                        JSONObject loginParams = new JSONObject();
                        try {
                            loginParams.put("uid", joinResponseData[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                                global.HOST_API_PREFIX + global.API_VERSION + "/users/login", loginParams, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.v("Hufstalk", "Login Response : " + response.toString());
                                    if (response.getInt("code") == 200) {
                                        Log.v("Hufstalk", "Login Success");
                                        String token = response.getJSONObject("data").getJSONObject("session").getString("token");
                                        Log.v("Hufstalk", "My token is " + token);
                                        editor.putString("token", token);
                                        editor.commit();
                                        global.SESSION_TOKEN = token;
                                        // 토픽 액티비티 시작
                                        finish();
                                        Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Log.v("Hufstalk", "Login Success");
                                        Log.v("Hufstalk", response.toString());
                                    }
                                }catch(JSONException e){}
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        Queue.add(loginRequest);
                    }
                }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("회원가입 성공");
        alert.show();
    }
}




