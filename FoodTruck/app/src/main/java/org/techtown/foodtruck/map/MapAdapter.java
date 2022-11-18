package org.techtown.foodtruck.map;

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

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private ArrayList<Truck> items = new ArrayList<Truck>();
    private onMapItemClickListener listener;
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_map_marker_description_item,parent,false);
        return new ViewHolder(view);
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

        private ImageView truckImage;
        private TextView truckName;
        private TextView rate;
        private TextView orderCount;
        private TextView waitTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            truckImage = itemView.findViewById(R.id.recyclerview_map_marker_description_item_truck_image);
            truckName= itemView.findViewById(R.id.recyclerview_map_marker_description_item_truck_name);
            rate = itemView.findViewById(R.id.recyclerview_map_marker_description_item_rate);
            orderCount = itemView.findViewById(R.id.recyclerview_map_marker_description_item_order_count);
            waitTime = itemView.findViewById(R.id.recyclerview_map_marker_description_item_wait_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(MapAdapter.ViewHolder.this,view,getAdapterPosition());
                }
            });
        }

        public void setItem(Truck truck){
            DecimalFormat f = new DecimalFormat("#.##");
            Glide.with(context).load(truck.getImage()).into(truckImage);
            truckName.setText(truck.getName());
            rate.setText(f.format(Float.parseFloat(truck.getRate()))+"   "+f.format(truck.getDistance()/1000)+"km"+"  ");
            orderCount.setText("  "+truck.getOrder_count());
            waitTime.setText(truck.getWait_time()+"분");
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

    public void setListener(onMapItemClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
