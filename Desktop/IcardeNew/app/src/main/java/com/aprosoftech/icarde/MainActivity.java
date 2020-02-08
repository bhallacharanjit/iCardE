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
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button sos,allsos,mysos,addbusiness,mybusiness,favbusiness,addbloodrequest,allbloodrequest;
    String data="",uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences editor = getSharedPreferences("detail",MODE_PRIVATE);
        data =  editor.getString("data","");
        try {
            JSONObject jsonObject = new JSONObject(data);
            uuid = jsonObject.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        sos = findViewById(R.id.sos);
        allsos = findViewById(R.id.allsos);
        mysos = findViewById(R.id.mysos);
        addbusiness = findViewById(R.id.addbusiness);
        mybusiness = findViewById(R.id.mybusiness);
        favbusiness = findViewById(R.id.favbusiness);
        addbloodrequest = findViewById(R.id.addbloodrequest);
        allbloodrequest = findViewById(R.id.allbloodrequest);


        sos.setOnClickListener(this);
        allsos.setOnClickListener(this);
        mysos.setOnClickListener(this);
        addbusiness.setOnClickListener(this);
        mybusiness.setOnClickListener(this);
        favbusiness.setOnClickListener(this);
        addbloodrequest.setOnClickListener(this);
        allbloodrequest.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.sos)
        {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Confirmation");
            builder.setCancelable(true);
            builder.setMessage("Are you sure to send SOS ! ");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                    sendsos();




                }


            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.show();

        }
     if (view.getId()==R.id.allsos)
        {
            Intent intent = new Intent(MainActivity.this, AllSos.class);
            startActivity(intent);

        }
     if (view.getId()==R.id.mysos)
        {

        }
     if (view.getId()==R.id.addbloodrequest)
        {
            Intent intent = new Intent(MainActivity.this, Addbloodrequest.class);
            startActivity(intent);
        }
     if (view.getId()==R.id.allbloodrequest)
        {
            Intent intent = new Intent(MainActivity.this, ShowBloodRequest.class);
            startActivity(intent);

        }
     if (view.getId()==R.id.addbusiness)
        {

            Intent intent = new Intent(MainActivity.this, AddBusiness.class);
            startActivity(intent);

        }
     if (view.getId()==R.id.mybusiness)
        {
            Intent intent = new Intent(MainActivity.this, MyBusiness.class);
            startActivity(intent);
        }
     if (view.getId()==R.id.favbusiness)
        {

            SharedPreferences.Editor editor = getSharedPreferences("detail", MODE_PRIVATE).edit();
            editor.putString("data","");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
    }

    public void sendsos() {



        final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading....");
        pDialog.setCancelable(false);
        pDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = icardeSingleton.baseurl + icardeSingleton.AddHelpUser;

        JSONObject params = new JSONObject();
        try {



            params.put("helpuserid", uuid);
            params.put("userlatitude", "");
            params.put("userlongitude","");


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
                        Log.d("sos result", jsonArray.toString());


                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Request Send");
                        builder.setCancelable(true);
                        builder.setMessage("People help you soon. Don't worry ");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {



                               dialog.dismiss();




                            }


                        });


                        builder.show();


                    }





                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("error", volleyError.toString());
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Check internet connection", Toast.LENGTH_LONG).show();


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
