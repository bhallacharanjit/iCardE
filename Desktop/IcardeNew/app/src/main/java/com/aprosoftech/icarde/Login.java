package com.aprosoftech.icarde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText email,password;
    Button register,login;
    String data,playerid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                playerid =userId;

                Log.d("playerid",userId);
                if (registrationId != null)

                    Log.d("debug", "registrationId:" + registrationId);

            }
        });


        if (!ispermissionsallowed())
        {
            requestpermissions();
        }


        SharedPreferences editor = getSharedPreferences("detail",MODE_PRIVATE);
        data =  editor.getString("data","");
        if (!data.equalsIgnoreCase(""))
        {

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    Login.this.finish();
        }
        else
        {

        }


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.signup);
        login = findViewById(R.id.login);




        register.setOnClickListener(this);
        login.setOnClickListener(this);


    }




    public boolean ispermissionsallowed()
    {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int readexternLpermission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

        if (camerapermission== PackageManager.PERMISSION_GRANTED && readexternLpermission== PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }
    public void requestpermissions()
    {

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},1001);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1001)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();

            }
            else {
                requestpermissions();
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View v) {


        if (v.getId()==R.id.signup)
        {
            Intent intent = new Intent(Login.this,Register.class);
            startActivity(intent);

        }

        if (v.getId()==R.id.login) {

            if (email.getText().toString().equalsIgnoreCase("") ||
                    password.getText().toString().equalsIgnoreCase("")
            ) {
                Toast.makeText(Login.this, "Fill Email and Password", Toast.LENGTH_LONG).show();
                return;
            }


            final SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading....");
            pDialog.setCancelable(false);
            pDialog.show();


            RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
            String url = icardeSingleton.baseurl + icardeSingleton.LoginUser;

            JSONObject params = new JSONObject();
            try {



                params.put("Password", password.getText().toString());
                params.put("Email", email.getText().toString());
                params.put("playerid", playerid);


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
                            Log.d("Login result", jsonArray.toString());


                            try {
                                if (jsonArray.getJSONObject(0).getString("Msg").equalsIgnoreCase("success")) {

                                    SharedPreferences.Editor editor = getSharedPreferences("detail", MODE_PRIVATE).edit();
                                    editor.putString("data", jsonArray.getJSONObject(0).toString());
                                    editor.apply();

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    Login.this.finish();
                                }
                                else{
                                    Toast.makeText(Login.this, "Invalid UserName or Password", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }





                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("error", volleyError.toString());
                    pDialog.dismiss();
                    Toast.makeText(Login.this, "Check internet connection", Toast.LENGTH_LONG).show();


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
}
