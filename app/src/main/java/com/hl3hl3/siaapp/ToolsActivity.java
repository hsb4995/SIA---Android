package com.hl3hl3.siaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harjot on 2/10/18.
 */

public class ToolsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText dest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);
        this.dest = (EditText) findViewById(R.id.textView);
        this.dest.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        this.dest.setText("");
    }

    public void getRate(View view){
        final EditText origin = (EditText) findViewById(R.id.textView2);
        Log.v("vll-->",origin.getText().toString());
        final EditText destN = (EditText) findViewById(R.id.textView);
        final EditText amountN = (EditText) findViewById(R.id.textView4);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String obj =origin.getText()+"_"+destN.getText();
        final String amountVal = amountN.getText().toString();
        String url = "http://free.currencyconverterapi.com/api/v5/convert?q="+obj+"&compact=y";
        Log.v("url is ",url);
        JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("Res is ------------------->", response.toString());
                            Log.v("Res is ------------------->", response.getJSONObject(obj).getString("val"));
                            String val =response.getJSONObject(obj).getString("val");
                            String result =val;
                            Log.v("amt",""+amountVal.length()+"");
                            if(amountVal!=null &&amountVal.length()!=0 && amountVal!=""){
                                Float prod=Float.parseFloat(val)*Float.parseFloat(amountVal);
                                result=""+prod+"";
                            }
                            Log.v("prod",result);
                            EditText edt = (EditText) findViewById(R.id.textView5);
                            edt.setText(result);
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

    }
}
