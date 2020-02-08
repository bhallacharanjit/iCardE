package com.aprosoftech.icarde;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AddBusiness extends AppCompatActivity {


    EditText name,type,category,address;
    Button add;
    ImageView imageView;
    String playerid="",imageurl="";
    String data="",uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);


        SharedPreferences editor = getSharedPreferences("detail",MODE_PRIVATE);
        data =  editor.getString("data","");
        try {
            JSONObject jsonObject = new JSONObject(data);
            uuid = jsonObject.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        name = findViewById(R.id.name);
        type = findViewById(R.id.type);
        category = findViewById(R.id.category);
        address = findViewById(R.id.address);
        add = findViewById(R.id.add);
        imageView = findViewById(R.id.imageview);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (name.getText().toString().equalsIgnoreCase("") ||
                        type.getText().toString().equalsIgnoreCase("") ||
                        category.getText().toString().equalsIgnoreCase("") ||
                        address.getText().toString().equalsIgnoreCase("")
                ) {
                    Toast.makeText(AddBusiness.this, "Fill all the fields", Toast.LENGTH_LONG).show();
                    return;
                }

                if (imageurl.equalsIgnoreCase(""))
                {
                    Toast.makeText(AddBusiness.this, "upload Business pic", Toast.LENGTH_LONG).show();
                    return;
                }


                final SweetAlertDialog pDialog = new SweetAlertDialog(AddBusiness.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading....");
                pDialog.setCancelable(false);
                pDialog.show();

                RequestQueue requestQueue = Volley.newRequestQueue(AddBusiness.this);
                String url = icardeSingleton.baseurl + icardeSingleton.AddBusiness;

                JSONObject params = new JSONObject();
                try {


                    params.put("bname", name.getText().toString());
                    params.put("bcategory", category.getText().toString());
                    params.put("bType", type.getText().toString());
                    params.put("bAddress", address.getText().toString());
                    params.put("blatitude", "");
                    params.put("blongitude", "");
                    params.put("buserid", uuid);
                    params.put("bimage", imageurl);


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
                                Log.d("AddBusiness result", jsonArray.toString());

                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddBusiness.this);
                                builder.setTitle("Success");
                                builder.setCancelable(true);
                                builder.setMessage("Business Added ");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {



                                        dialog.dismiss();
                                        Intent intent = new Intent(AddBusiness.this, MainActivity.class);
                                        startActivity(intent);
                                        AddBusiness.this.finish();




                                    }


                                });


                                builder.show();




                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("error", volleyError.toString());

                        pDialog.dismiss();
                        Toast.makeText(AddBusiness.this, "Check internet connection", Toast.LENGTH_LONG).show();


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
            imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence options[] = {"Take Photo","Choose from Gallery","Cancel"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddBusiness.this);
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
        });

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
                imageView.setImageBitmap(bitmap);
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
                    imageView.setImageBitmap(bitmap);
                    byte[] bytearray = baos.toByteArray();
                    imageurl = Base64.encodeToString(bytearray,Base64.DEFAULT);



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
