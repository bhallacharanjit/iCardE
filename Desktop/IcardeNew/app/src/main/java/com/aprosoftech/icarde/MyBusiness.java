package com.aprosoftech.icarde;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MyBusiness extends AppCompatActivity {

    ListView listView;
    String data="",uuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business);

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



        final SweetAlertDialog pDialog = new SweetAlertDialog(MyBusiness.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading....");
        pDialog.setCancelable(false);
        pDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(MyBusiness.this);
        String url = icardeSingleton.baseurl + icardeSingleton.MyBusinesses;

        JSONObject params = new JSONObject();
        try {




            params.put("buserid", uuid);


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
                        Log.d("mybusiness", jsonArray.toString());




                        MyBusinessAdapter bloodRequestAdapter = new MyBusinessAdapter(MyBusiness.this,jsonArray);
                        listView.setAdapter(bloodRequestAdapter);


                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                                try {
                                    String bid = jsonArray.getJSONObject(i).getString("Businessid");
                                    Intent intent = new Intent(MyBusiness.this,ShowProducts.class);
                                    intent.putExtra("bid",bid);
                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });







                    }





                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("error", volleyError.toString());
                pDialog.dismiss();
                Toast.makeText(MyBusiness.this, "Check internet connection", Toast.LENGTH_LONG).show();


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
