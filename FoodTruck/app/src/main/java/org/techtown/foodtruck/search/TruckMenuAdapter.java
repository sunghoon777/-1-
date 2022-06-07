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

import org.techtown.foodtruck.DO.Food;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.util.ArrayList;

public class TruckMenuAdapter extends RecyclerView.Adapter<TruckMenuAdapter.ViewHolder> {

    ArrayList<Food> items = new ArrayList<>();
    onMenuItemClickListener listener;
    Context context;

    public TruckMenuAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.detailed_truck_menu_recycleview_layout,parent,false);
        return new TruckMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = items.get(position);
        holder.setItem(food);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView textView3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detailed_truck_menu_recyclerView_layout_imageview);
            textView1 = itemView.findViewById(R.id.detailed_truck_menu_recyclerView_layout_textview1);
            textView2 = itemView.findViewById(R.id.detailed_truck_menu_recyclerView_layout_textview2);
            textView3 = itemView.findViewById(R.id.detailed_truck_menu_recyclerView_layout_textview3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this,v,getAdapterPosition());
                    }
                }
            });
        }

        public void setItem(Food food){
            Glide.with(context).load(food.getImage()).into(imageView);
            textView1.setText(food.getName());
            textView2.setText(food.getContent());
            textView3.setText(food.getCost()+"원");
        }
    }

    public void addItem(Food food){
        items.add(food);
    }

    public Food getItem(int position){
        return items.get(position);
    }

    public void setItems(ArrayList<Food> items){
        this.items = items;
    }

    public ArrayList<Food> getItems(){
        return  items;
    }

    public void setOnItemClickListener(onMenuItemClickListener listener){
        this.listener = listener;
    }
}
