package com.hl3hl3.siaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by harjot on 29/9/18.
 */

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile","user_tagged_places"));
        // If you are using in a fragment, call loginButton.setFragment(this);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("Success ---------------------->",loginResult.getRecentlyGrantedPermissions().toString());
                String uid = loginResult.getAccessToken().getUserId();
                // App code
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/"+uid+"/tagged_places",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
            /* handle the result */
                                Log.v("res---=======>", response.toString());
                                try {
                                    JSONObject jsonfb = response.getJSONObject();

                                    JSONArray arrdata = jsonfb.getJSONArray("data");
                                    String r = arrdata.getJSONObject(0).getString("id");
                                    Log.v("array json ==========...",r);
                                }catch(Exception e){
                                    Log.v("ERROR !!!!!!!!",e.getMessage());
                                }
                            }
                        }
                ).executeAsync();
//                LoginManager.getInstance().logInWithReadPermissions(
//                        RecomActivity.this,
//                        Arrays.asList("user_posts"));
//                Log.v("Success ---------------------->",loginResult.getAccessToken().getPermissions().toString());// App code

            }
            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("error",exception.getMessage());// App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
