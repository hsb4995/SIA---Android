package com.hl3hl3.siaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harjot on 27/9/18.
 */

public class RecomActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    String searchResults ="";
    int i=0;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recom_activity);
        callbackManager = CallbackManager.Factory.create();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        String val = (isLoggedIn) ? "true" : "false";
        Log.v("login ===>",val);
        // User is Logged in to FB
        if(isLoggedIn){
            String uid = accessToken.getUserId();
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
                                String r = arrdata.getJSONObject(0).getJSONObject("place").getJSONObject("location").get("city").toString();
                                Log.v("array json ==========...",r);
                                JSONArray pushdata = new JSONArray();
                                for(int i=0;i<arrdata.length();++i){
                                    JSONObject pushobj = new JSONObject();
                                    pushobj.put("created_time",arrdata.getJSONObject(i).getString("created_time"));
                                    pushobj.put("name",arrdata.getJSONObject(i).getJSONObject("place").getString("name"));
                                    pushobj.put("city",arrdata.getJSONObject(i).getJSONObject("place").getJSONObject("location").get("city"));
                                    pushdata.put(pushobj);
                                }
                                JSONObject serverdata = new JSONObject();
                                serverdata.put("id", accessToken.getUserId());
                                serverdata.put("data",pushdata);
                                // Sending this Response to Server of Statistics
                                Log.v("prepared json",serverdata.toString());

                                // Request to server:
                                String url_pnr = "http://18.188.174.242:8080/saveCheckin";


                                JsonObjectRequest strRequest = new JsonObjectRequest(url_pnr,serverdata,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    Log.v("Res is ------------------->", response.getString("status"));
                                                }catch(Exception e){
                                                    Log.v("error", (e.getMessage()==null)?"failed":e.getMessage());
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //mTextView.setText("That didn't work!");
                                        Log.v("error",(error.getMessage()==null)?"failed":error.getMessage());
                                    }
                                }){
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("Content-Type", "application/json");
                                        return params;
                                    }
                                };

                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                queue.add(strRequest);
                            }catch(Exception e){
                                Log.v("ERROR !!!!!!!!",e.getMessage());
                            }
                            }
                        }
                ).executeAsync();
        }

    }

    public void loadLogin(View view) {
        Intent intent = new Intent(RecomActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void callPlaces(View view) throws  Exception{
//      /  final TextView mTextView = (TextView) findViewById(R.id.textView);
//  Find based on Pnr and last name
        searchResults="";
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn) {
            getImages();
        }
        final EditText pnr = (EditText) findViewById(R.id.pnr);
        final EditText lastname = (EditText) findViewById(R.id.lastname);

        String url_pnr = "https://apigw.singaporeair.com/appchallenge/api/pax/pnr";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("bookingReference", pnr.getText().toString());
        params.put("bookingLastName", lastname.getText().toString());
        Log.v("values",pnr.getText().toString()+' '+ lastname.getText().toString());

        final JsonObjectRequest strRequest = new JsonObjectRequest(url_pnr,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String country=response.getJSONObject("responseBody").getJSONArray("flights").getJSONObject(0).getJSONObject("destination").getString("countryCode");
                            Log.v("Res is ------------------->", country);

                            // Get Country Name from rest api

                            String restCountryUrl = "https://restcountries.eu/rest/v2/alpha/"+country;
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                    (Request.Method.GET, restCountryUrl, null, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                Log.v("Response: ", response.getString("name"));
                                                final String country=response.getString("name");

                                                if(isLoggedIn) {
                                                    String url = "http://18.188.174.242:8080/topic";
                                                    // Request a string response from the provided URL.

                                                    StringRequest analyticsReq = new StringRequest(Request.Method.GET, url,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    // Display the first 500 characters of the response string.
                                                                    //mTextView.setText("Response is: "+ response);
                                                                    Log.v("aya aya aya", response);
                                                                    String[] elements = response.split(",");
                                                                    List<String> fixedLenghtList = Arrays.asList(elements);
                                                                    ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);

                                                                    for (i = 0; i < listOfString.size(); i++) {
                                                                        makeRequest(listOfString, country);
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.v("nhi aya aya aya", error.getMessage());
                                                            //mTextView.setText("That didn't work!");
                                                        }
                                                    });
                                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                                    queue.add(analyticsReq);
                                                } else {
                                                    List<String> fixedLenghtList = Arrays.asList("places");
                                                    ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);
                                                    makeRequest(listOfString,country);
                                                }

                                                //queue.add(stringRequest);
                                            } catch (Exception e) {
                                                Log.v("error",e.getMessage());
                                            }
                                        }

                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO: Handle error

                                        }
                                    });
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(jsonObjectRequest);
                        }catch(Exception e){
                            Log.v("error",e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.v("error",error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("apikey", "aghk73f4x5haxeby7z24d2rc");

                return params;
            }
        };


// Add the request to the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(strRequest);
    }

    public void getImages(){
        final ImageView mImageView;
        String url = "http://18.188.174.242:8080/Image/1";
         mImageView = (ImageView) findViewById(R.id.imageView);

// Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mImageView.setImageResource(R.drawable.ic_cube_disable);
                    }
                });
// Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }


    public void makeRequest(final ArrayList<String> obj,final String country){
        String placeurl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+obj.get(i)+"+to+go+in+"+country+"&key=AIzaSyCP-FfCs2PYzYflGfoPeMe3fq8z9XsLR1Q";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(placeurl,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response "+ i, response.toString());
                try {
                    JSONArray res = response.getJSONArray("results");
                    for (int j = 0; j < 12/obj.size(); ++j) {
                        JSONObject tmp = res.getJSONObject(j);
                        String name =tmp.getString("name");
                        searchResults+=name+",";
                    }
                    Log.v("Seacrh ---------------------->",searchResults);
                }catch(Exception e){
                    Log.v("Json exe",e.getMessage());
                }
                i++;
                if(i < 3 && i < obj.size()){
                    // make a recursive function
                    makeRequest(obj,country);
                } else {
                    // Completed
                    ListView listView = (ListView) findViewById(R.id.list_view);

                    List<String> items = new ArrayList<>();

                    // Generate list from searchResult String
                    String[] elements = searchResults.split(",");
                    List<String> fixedLenghtList = Arrays.asList(elements);
                    ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);
                    for (int i = 0; i < listOfString.size() && i < 10; i++) {
                        items.add(listOfString.get(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, items);

                    if (listView != null) {
                        listView.setAdapter(adapter);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }

}
