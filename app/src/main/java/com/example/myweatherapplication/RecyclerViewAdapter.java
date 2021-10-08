package com.example.myweatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecyclerViewModelClass> recyclerViewModelArray;

    public RecyclerViewAdapter(Context context, ArrayList<RecyclerViewModelClass> recyclerViewModelArray) {
        this.context = context;
        this.recyclerViewModelArray = recyclerViewModelArray;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        RecyclerViewModelClass model = recyclerViewModelArray.get(position);
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTxt);

        holder.tempTV.setText(model.getTemperature() + "°c");
        holder.windspeedTV.setText(model.getWindSpeed() + "Km/h");

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try {
            Date t = input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }



    }


    @Override
    public int getItemCount() {
        return recyclerViewModelArray.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTV, tempTV, windspeedTV;
        private ImageView conditionTxt;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windspeedTV = itemView.findViewById(R.id.windspeedTV);
            tempTV = itemView.findViewById(R.id.tempTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            conditionTxt = itemView.findViewById(R.id.conditionTxt);

        }


    }




}
