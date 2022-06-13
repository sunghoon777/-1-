package org.techtown.foodtruck.Favorite;

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
import org.techtown.foodtruck.search.TruckListAdapter;
import org.techtown.foodtruck.search.onTruckItemClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    ArrayList<Truck> items = new ArrayList<Truck>();
    onFavoriteItemClickListener listener;
    Context context;

    public FavoriteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.favorite_recycleview_layout,parent,false);
        return new FavoriteAdapter.ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        
        public ViewHolder(@NonNull View itemView, onFavoriteItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.favorite_recycleView_truckImage);
            textView = itemView.findViewById(R.id.favorite_recycleView_truckName);
            textView2 = itemView.findViewById(R.id.favorite_recycleView_rate);
            textView3 = itemView.findViewById(R.id.favorite_recycleView_order_count);
            textView4 = itemView.findViewById(R.id.favorite_recycleView_wait_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onItemClick(FavoriteAdapter.ViewHolder.this,v,getAdapterPosition());
                    }
                }
            });
        }

        public void setItem(Truck truck){
            DecimalFormat f = new DecimalFormat("#.##");
            Glide.with(context).load(truck.getImage()).into(imageView);
            textView.setText(truck.getName());
            textView2.setText(f.format(Float.parseFloat(truck.getRate()))+"   "+f.format(truck.getDistance()/1000)+"km"+"  ");
            textView3.setText("  "+truck.getOrder_count());
            textView4.setText(truck.getWait_time()+"분");
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

    public void setOnItemClickListener(onFavoriteItemClickListener listener){
        this.listener = listener;
    }
    
    
}
