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
import android.widget.EditText;
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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Addbloodrequest extends AppCompatActivity {


    EditText phone,bloodgroup,address;
    Button send;
    String data="",uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbloodrequest);

        SharedPreferences editor = getSharedPreferences("detail",MODE_PRIVATE);
        data =  editor.getString("data","");
        try {
            JSONObject jsonObject = new JSONObject(data);
            uuid = jsonObject.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send = findViewById(R.id.send);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        bloodgroup = findViewById(R.id.bloodgroup);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (phone.getText().toString().equalsIgnoreCase("")||
                        address.getText().toString().equalsIgnoreCase("")||
                        bloodgroup.getText().toString().equalsIgnoreCase(""))
                {
                    Snackbar.make(view,"fill all the fields",Snackbar.LENGTH_LONG).show();
                    return;
                }

                final SweetAlertDialog pDialog = new SweetAlertDialog(Addbloodrequest.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading....");
                pDialog.setCancelable(false);
                pDialog.show();


                RequestQueue requestQueue = Volley.newRequestQueue(Addbloodrequest.this);
                String url = icardeSingleton.baseurl + icardeSingleton.AddBloodRequest;

                JSONObject params = new JSONObject();
                try {



                    params.put("bloodgroup", bloodgroup.getText().toString());
                    params.put("requiredaddress", address.getText().toString());
                    params.put("reqMobile", phone.getText().toString());
                    params.put("reqlatitude", "");
                    params.put("reqlongitude", "");
                    params.put("userid", uuid);


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
                                Log.d("blood request result", jsonArray.toString());


                                final AlertDialog.Builder builder = new AlertDialog.Builder(Addbloodrequest.this);
                                builder.setTitle("Request Sent");
                                builder.setCancelable(true);
                                builder.setMessage("People will contact you soon ");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {



                                        dialog.dismiss();
                                        Intent intent = new Intent(Addbloodrequest.this, MainActivity.class);
                                        startActivity(intent);
                                        Addbloodrequest.this.finish();




                                    }


                                });


                                builder.show();







                            }





                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("error", volleyError.toString());
                        pDialog.dismiss();
                        Toast.makeText(Addbloodrequest.this, "Check internet connection", Toast.LENGTH_LONG).show();


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
        });
    }
}
