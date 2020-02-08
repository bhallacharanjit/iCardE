package com.aprosoftech.icarde;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class BloodRequestAdapter extends BaseAdapter {


    Context context;
    JSONArray jsonArray;


    public BloodRequestAdapter(Context context, JSONArray jsonArray) {

        this.context = context;
        this.jsonArray = jsonArray;

    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v =convertView;
        if (convertView==null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.bloodrequests,null,true);


            TextView bloodgroup = v.findViewById(R.id.bloodgroup);
            TextView phone = v.findViewById(R.id.phone);
            TextView address = v.findViewById(R.id.address);
            ImageView map = v.findViewById(R.id.map);

            map.setTag(position);

            try {
                bloodgroup.setText(jsonArray.getJSONObject(position).getString("bloodgroup"));
                phone.setText(jsonArray.getJSONObject(position).getString("reqMobile"));
                address.setText(jsonArray.getJSONObject(position).getString("requiredaddress"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        String lat = jsonArray.getJSONObject((Integer) view.getTag()).getString("reqlatitude");
                        String longi = jsonArray.getJSONObject((Integer) view.getTag()).getString("reqlongitude");


                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=31.2432,75.7441&daddr="+lat+","+longi));
                        context.startActivity(intent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });


        }


        return v;
    }
}
