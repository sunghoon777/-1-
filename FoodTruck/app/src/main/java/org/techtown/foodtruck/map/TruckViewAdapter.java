package org.techtown.foodtruck.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.foodtruck.DO.Notice;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TruckViewAdapter extends RecyclerView.Adapter<TruckViewAdapter.MyViewHolder> {

    Context context;

    ArrayList<Truck> list;


    public TruckViewAdapter(Context context, ArrayList<Truck> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.truck_info_detail_viewpager, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Truck truck = list.get(position);
        holder.title.setText(truck.getTitle());
        holder.type.setText(truck.getType());
        holder.ratingBar.setRating(Float.parseFloat(truck.getRate()));
        Glide.with(context)
                .load(truck.getImage())
                .into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, type, distance;
        RatingBar ratingBar;
        ImageView thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            type = itemView.findViewById(R.id.typeTextView);
            thumbnail = itemView.findViewById(R.id.thumbnailImageView);

        }
    }

    public void addItem(Truck item) {
        list.add(item);
    }

    public void setItem(ArrayList<Truck> list) {
        this.list = list;
    }

    public Truck getItem(int position) {
        return list.get(position);
    }

    public ArrayList<Truck> getItems(){
        return  list;
    }


}