package com.aprosoftech.icarde;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class AllSosAdapter extends BaseAdapter {


    Context context;
    JSONArray jsonArray;


    public AllSosAdapter(Context context, JSONArray jsonArray) {

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
            v = layoutInflater.inflate(R.layout.allsos,null,true);


            TextView name = v.findViewById(R.id.name);
            ImageView imageView = v.findViewById(R.id.imageview);
            ImageView map = v.findViewById(R.id.map);

            map.setTag(position);

            try {
                name.setText(jsonArray.getJSONObject(position).getString("Name"));



                Glide.with(context)
                        .load(icardeSingleton.imageurl+jsonArray.getJSONObject(position).getString("profilepic"))
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);


            } catch (JSONException e) {
                e.printStackTrace();
            }



            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        String lat = jsonArray.getJSONObject((Integer) view.getTag()).getString("latitude");
                        String longi = jsonArray.getJSONObject((Integer) view.getTag()).getString("longitude");
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
