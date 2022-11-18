package org.techtown.foodtruck.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.foodtruck.DO.OrderHistory;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.search.DetailedTruckMenuActivity;


import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{

    private ArrayList<OrderHistory> orderHistoryList;
    private ArrayList<Truck> truckList;
    private Context context;
    private Activity activity;

    public OrderHistoryAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_order_history_item,parent,false);
        return new OrderHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, int position) {
        holder.setItem(getItem(position));
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        
        TextView truckName;
        TextView orderDate;
        TextView orderList;
        TextView totalCost;
        ImageView truckImage;
        Button reorderButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            truckName = itemView.findViewById(R.id.recyclerview_order_history_truck_name);
            orderDate = itemView.findViewById(R.id.recyclerview_order_history_order_date);
            orderList = itemView.findViewById(R.id.recyclerview_order_history_order_list);
            totalCost = itemView.findViewById(R.id.recyclerview_order_history_total_cost);
            truckImage =  itemView.findViewById(R.id.recyclerview_order_history_truck_image);
            reorderButton =  itemView.findViewById(R.id.recyclerview_order_history_reorder_button);
            reorderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, DetailedTruckMenuActivity.class);
                    Truck truck = truckList.get(position);
                    intent.putExtra("Truck", truck );
                    activity.startActivity(intent);
                }
            });
        }

        public void setItem(OrderHistory order_history){
            truckName.setText(order_history.getTruckName());
            orderDate.setText(order_history.getDate());
            String order_list = "";
            int sum_data = 0;
            for(Order order : order_history.getOrders()){
                sum_data += Integer.parseInt(order.getFood_cost()) * order.getFood_number();
                order_list += order.getFood_name()+" x"+Integer.toString(order.getFood_number())+"   ";
            }
            orderList.setText(order_list);
            totalCost.setText(Integer.toString(sum_data));
            Glide.with(context).load(order_history.getImage()).into(truckImage);
        }
    }

    public OrderHistory getItem(int position){
        return orderHistoryList.get(position);
    }

    public void setItems(ArrayList<OrderHistory> orderHistoryList){
        this.orderHistoryList = orderHistoryList;
    }

    public void setItems2(ArrayList<Truck> truckList){
        this.truckList = truckList;
    }

}
