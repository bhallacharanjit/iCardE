package com.aprosoftech.icarde;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AllSos extends AppCompatActivity {

    ListView listView;
    String data="",uuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sos);

        listView = findViewById(R.id.listview);

        SharedPreferences editor = getSharedPreferences("detail",MODE_PRIVATE);
        data =  editor.getString("data","");
        try {
            JSONObject jsonObject = new JSONObject(data);
            uuid = jsonObject.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showlist();



    }



    public void showlist()
    {



        final SweetAlertDialog pDialog = new SweetAlertDialog(AllSos.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading....");
        pDialog.setCancelable(false);
        pDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(AllSos.this);
        String url = icardeSingleton.baseurl + icardeSingleton.ShowHelpUsers;

        JSONObject params = new JSONObject();
        try {




            params.put("uuid", uuid);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonRequest<JSONArray> jsonArrayJsonRequest = new JsonRequest<JSONArray>(Request.Method.POST,
                url,
                params.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray jsonArray) {


                        pDialog.dismiss();
                        Log.d("allsos", jsonArray.toString());




                        AllSosAdapter bloodRequestAdapter = new AllSosAdapter(AllSos.this,jsonArray);
                        listView.setAdapter(bloodRequestAdapter);







                    }





                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("error", volleyError.toString());
                pDialog.dismiss();
                Toast.makeText(AllSos.this, "Check internet connection", Toast.LENGTH_LONG).show();


            }
        }) {


            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse) {


                try {
                    String jsonString = new String(networkResponse.data,
                            HttpHeaderParser
                                    .parseCharset(networkResponse.headers));
                    return Response.success(new JSONArray(jsonString),
                            HttpHeaderParser
                                    .parseCacheHeaders(networkResponse));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }

            }
        };

        jsonArrayJsonRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayJsonRequest);







    }


}
