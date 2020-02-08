package com.aprosoftech.icarde;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ShowBloodRequest extends AppCompatActivity {


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_blood_request);


        listView = findViewById(R.id.listview);


        showlist();






    }





    public void showlist()
    {
        final SweetAlertDialog pDialog = new SweetAlertDialog(ShowBloodRequest.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading....");
        pDialog.setCancelable(false);
        pDialog.show();








        RequestQueue requestQueue = Volley.newRequestQueue(ShowBloodRequest.this);
        String url = icardeSingleton.baseurl+icardeSingleton.ShowBloodRequest;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response", response.toString());
                pDialog.dismiss();

                try {
                    final JSONArray jsonArray = new JSONArray(response);



                    BloodRequestAdapter bloodRequestAdapter = new BloodRequestAdapter(ShowBloodRequest.this,jsonArray);
                    listView.setAdapter(bloodRequestAdapter);








                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
                pDialog.dismiss();
                Toast.makeText(ShowBloodRequest.this, "Check internet connection", Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(stringRequest);

    }


}
