package org.techtown.foodtruck.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.OrderHistory;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.search.DetailedTruckMenuActivity;

import java.util.ArrayList;

public class OrderReadyListAdapter extends RecyclerView.Adapter<OrderReadyListAdapter.ViewHolder>{


    private ArrayList<OrderHistory> orderHistoryList;
    private ArrayList<Truck> truckList;
    private Context context;
    private Activity activity;

    public OrderReadyListAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public OrderReadyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_order_history_item2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderReadyListAdapter.ViewHolder holder, int position) {
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
        Button commitButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            truckName = itemView.findViewById(R.id.recyclerview_order_history_item2_truck_name);
            orderDate = itemView.findViewById(R.id.recyclerview_order_history_item2_order_date);
            orderList = itemView.findViewById(R.id.recyclerview_order_history_item2_order_list);
            totalCost = itemView.findViewById(R.id.recyclerview_order_history_item2_total_cost);
            truckImage =  itemView.findViewById(R.id.recyclerview_order_history_item2_truck_image);
            commitButton = itemView.findViewById(R.id.recyclerview_order_history_item2_commit_button);
            commitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, OrderStateActivity.class);
                    Truck truck = truckList.get(position);
                    intent.putExtra("Truck", truck );
                    OrderHistory orderHistory = orderHistoryList.get(position);
                    intent.putExtra("OrderHistory",orderHistory);
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
