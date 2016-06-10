package com.example.keerthana.cricscoreslive;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by keerthana on 6/6/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<String> mItems;

    public RecyclerViewAdapter(ArrayList<String> data){
        mItems = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject json = new JSONObject(mItems.get(position));
            holder.t1.setText(json.getString("team-1"));
            holder.t2.setText(json.getString("team-2"));
            holder.req.setText(json.getString("innings-requirement"));
            holder.score.setText(json.getString("score"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView t1;
        TextView t2;
        TextView score;
        TextView req;

        public ViewHolder(View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.t1);
            t2 = (TextView) itemView.findViewById(R.id.t2);
            score = (TextView) itemView.findViewById(R.id.score);
            req = (TextView) itemView.findViewById(R.id.req);
        }
    }
}
