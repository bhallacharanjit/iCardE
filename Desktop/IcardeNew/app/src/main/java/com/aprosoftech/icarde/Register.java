package com.aprosoftech.icarde;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Register extends AppCompatActivity implements View.OnClickListener {


    EditText name,email,phone,password;
    Button register;
    String playerid="",imageurl="";
    ImageView profilepic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                playerid =userId;
                if (registrationId != null)

                    Log.d("debug", "registrationId:" + registrationId);

            }
        });


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        profilepic = findViewById(R.id.profilepic);

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        profilepic.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode==RESULT_OK )
        {
            if (requestCode==1)
            {

                Bitmap bitmap =(Bitmap)data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,75,baos);
                profilepic.setImageBitmap(bitmap);

                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilepic);

                byte[] images = baos.toByteArray();
                imageurl = Base64.encodeToString(images,Base64.DEFAULT);




            }

            else if (requestCode==2)
            {
                Uri uri = data.getData();


                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    Glide.with(this)
                            .load(bitmap)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilepic);
                    byte[] bytearray = baos.toByteArray();
                    imageurl = Base64.encodeToString(bytearray,Base64.DEFAULT);



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }




    @Override
    public void onClick(View v) {


        if (v.getId()==R.id.profilepic)
        {
            final CharSequence options[] = {"Take Photo","Choose from Gallery","Cancel"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
            builder.setTitle("Select Image");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (options[which].equals("Take Photo"))
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,1);
                    }
                    else
                    if (options[which].equals("Choose from Gallery"))
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,2);
                    }
                    else
                    {
                        dialog.dismiss();
                    }


                }


            });


            builder.show();

        }


        if (v.getId()==R.id.register) {
            if (name.getText().toString().equalsIgnoreCase("") ||
                    email.getText().toString().equalsIgnoreCase("") ||
                    phone.getText().toString().equalsIgnoreCase("") ||
                    password.getText().toString().equalsIgnoreCase("")
            ) {
                Toast.makeText(Register.this, "Fill all the fields", Toast.LENGTH_LONG).show();
                return;
            }

            if (imageurl.equalsIgnoreCase(""))
            {
                Toast.makeText(Register.this, "upload profile pic", Toast.LENGTH_LONG).show();
                return;
            }


                final SweetAlertDialog pDialog = new SweetAlertDialog(Register.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading....");
                pDialog.setCancelable(false);
                pDialog.show();

                RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                String url = icardeSingleton.baseurl + icardeSingleton.RegisterUser;

                JSONObject params = new JSONObject();
                try {


                    params.put("Name", name.getText().toString());
                    params.put("Password", password.getText().toString());
                    params.put("Phone", phone.getText().toString());
                    params.put("Email", email.getText().toString());
                    params.put("playerid", playerid);
                    params.put("profilepic", imageurl);


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
                                Log.d("Signup result", jsonArray.toString());


                                try {
                                    if (jsonArray.getJSONObject(0).getString("Msg").equalsIgnoreCase("success")) {

                                        SharedPreferences.Editor editor = getSharedPreferences("detail", MODE_PRIVATE).edit();
                                        editor.putString("data", jsonArray.getJSONObject(0).toString());
                                        editor.apply();

                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        Toast.makeText(Register.this, "Email already exist", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(Register.this, "Check internet connection", Toast.LENGTH_LONG).show();


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
