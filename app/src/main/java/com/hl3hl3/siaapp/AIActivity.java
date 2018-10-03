package com.hl3hl3.siaapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * Created by harjot on 1/10/18.
 */

public class AIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);

        final EditText edit_txt = (EditText) findViewById(R.id.editText);
        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v);
                    Log.v("text value",v.getText().toString());
//                    Log.v("text value",edit_txt.getText().toString());
                    String text_value=v.getText().toString();
                    callChatBot(text_value);
                    return true;
                }
                return false;
            }
        });

    }

    public void callChatBot(String text) {
        Log.v("chatbot called yay !",text);
        EditText txt = (EditText) findViewById(R.id.editText2);
        txt.setText("");
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "https://api.dialogflow.com/v1/query?v=20150910";
            JSONObject json = new JSONObject();
            JSONArray queryArr = new JSONArray();
            queryArr.put(text);
            json.put("query", queryArr);
            json.put("lang","en");
            json.put("sessionId","440b1563-0ba1-7dee-91d9-77a9ab2215f0");
            JsonObjectRequest strRequest = new JsonObjectRequest(url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.v("Res is ------------------->", response.toString());
                                //Call intents or show Response msg
                                JSONObject result = response.getJSONObject("result");
                                JSONObject ful = result.getJSONObject("fulfillment");
                                String speech =  ful.getString("speech");
                                Log.v("Speech ------>",ful.toString());
                                if(speech != null || speech.length()!=0){
                                    EditText txt = (EditText) findViewById(R.id.editText2);
                                    txt.setText(speech);
                                }
                                JSONObject meta=result.getJSONObject("metadata");
                                if(meta!=null){
                                    String intentName = meta.getString("intentName");
                                    if(intentName!=null ) {
                                        switch (intentName) {
                                            case "loginIntent":
                                                callLogin();
                                                break;
                                            case "liveCurrency":
                                                callTools();
                                                break;
                                            case "recommendationIntent":
                                                callPlaces();
                                                break;
                                            case "weatherIntent":
                                                callTools();
                                                break;
                                            case "ARMeasure":
                                                callAR();
                                                break;

                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Log.v("error", (e.getMessage() == null) ? "failed" : e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                    Log.v("error", (error.getMessage() == null) ? "failed" : error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer 1dead5cc9354468ea71b1dfa3d3660ee");
                    return params;
                }
            };
            queue.add(strRequest);
        }catch(Exception e){
            Log.e("exception aa gya",e.getMessage());
        }
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void callAR() {
            Intent intent=new Intent(AIActivity.this,ArMeasureActivity.class);
            startActivity(intent);
    }

    public void callPlaces() {
        Intent intent=new Intent(AIActivity.this,RecomActivity.class);
        startActivity(intent);
    }

    public void callLogin() {
        Intent intent=new Intent(AIActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void callTools(){
        Intent intent=new Intent(AIActivity.this,ToolsActivity.class);
        startActivity(intent);
    }
}
