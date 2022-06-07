package org.techtown.foodtruck.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TruckListAdapter extends RecyclerView.Adapter<TruckListAdapter.ViewHolder> {

    ArrayList<Truck> items = new ArrayList<Truck>();
    onTruckItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.truck_list_recycleview_layout,parent,false);
        return new TruckListAdapter.ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        TextView textView2;
        TextView textView3;
        Context context;


        public ViewHolder(@NonNull View itemView, onTruckItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.truck_list_recycleView_imageView);
            textView = itemView.findViewById(R.id.truck_list_recycleView_textView);
            textView2 = itemView.findViewById(R.id.truck_list_recycleView_textView2);
            textView3 = itemView.findViewById(R.id.truck_list_recycleView_textView3);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this,v,getAdapterPosition());
                    }
                }
            });
        }

        public void setItem(Truck truck){
            DecimalFormat f = new DecimalFormat("#.##");
            Glide.with(context).load(truck.getImage()).into(imageView);
            textView.setText(truck.getName());
            textView2.setText(truck.getRate()+"   "+f.format(truck.getDistance()/1000)+"km"+"  ");
            textView3.setText("  "+truck.getOrder_count());
        }
    }

    public void addItem(Truck truck){
        items.add(truck);
    }

    public Truck getItem(int position){
        return items.get(position);
    }

    public void setItems(ArrayList<Truck> items){
        this.items = items;
    }

    public ArrayList<Truck> getItems(){
        return  items;
    }

    public void setOnItemClickListener(onTruckItemClickListener listener){
        this.listener = listener;
    }
}
