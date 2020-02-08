package com.aprosoftech.icarde;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;

public class MyBusinessAdapter extends BaseAdapter {


    Context context;
    JSONArray jsonArray;


    public MyBusinessAdapter(Context context, JSONArray jsonArray) {

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
            v = layoutInflater.inflate(R.layout.mybusiness,null,true);


            TextView name = v.findViewById(R.id.name);
            TextView address = v.findViewById(R.id.address);
            TextView category = v.findViewById(R.id.category);
            ImageView imageView = v.findViewById(R.id.imageview);


            try {
                name.setText(jsonArray.getJSONObject(position).getString("bname"));
                category.setText(jsonArray.getJSONObject(position).getString("bcategory"));
                address.setText(jsonArray.getJSONObject(position).getString("bAddress"));



                Glide.with(context)
                        .load(icardeSingleton.imageurl+jsonArray.getJSONObject(position).getString("bimage"))
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        return v;
    }
}
