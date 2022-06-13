package org.techtown.foodtruck.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
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

import org.techtown.foodtruck.DO.Order_history;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.MainActivity;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.search.DetailedTruckMenu;


import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{

    //order_history 객체 저장 리스트
    ArrayList<Order_history> order_history_list;
    ArrayList<Truck> truck_list;
    Context context;
    Activity activity;
    //다이얼로그 관련 뷰
    Dialog dialog;
    //레이팅바
    RatingBar rb;

    public OrderHistoryAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_history_recycleview_layout,parent,false);
        return new OrderHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, int position) {
        holder.setItem(getItem(position));
    }

    @Override
    public int getItemCount() {
        return order_history_list.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        
        TextView truckName;
        TextView orderDate;
        TextView orderList;
        TextView sum;
        ImageView truckImage;
        Button reorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            truckName = itemView.findViewById(R.id.order_history_recycleView_truckName);
            orderDate = itemView.findViewById(R.id.order_history_recycleView_orderDate);
            orderList = itemView.findViewById(R.id.order_history_recycleView_orderList);
            sum = itemView.findViewById(R.id.order_history_recycleView_sum);
            truckImage =  itemView.findViewById(R.id.order_history_recycleView_truckImage);
            reorder =  itemView.findViewById(R.id.order_history_recycleView_reorder);

            reorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context,DetailedTruckMenu.class);
                    Truck truck = truck_list.get(position);
                    intent.putExtra("Truck", truck );
                    activity.startActivity(intent);
                }
            });

        }

        public void setItem(Order_history order_history){
            truckName.setText(order_history.getTruck_name());
            orderDate.setText(order_history.getDate());
            String order_list = "";
            int sum_data = 0;
            for(Order order : order_history.getOrders()){
                sum_data += Integer.parseInt(order.getFood_cost()) * order.getFood_number();
                order_list += order.getFood_name()+" x"+Integer.toString(order.getFood_number())+"   ";
            }
            orderList.setText(order_list);
            sum.setText(Integer.toString(sum_data));
            Glide.with(context).load(order_history.getImage()).into(truckImage);
        }
    }

    public Order_history getItem(int position){
        return order_history_list.get(position);
    }

    public void setItems(ArrayList<Order_history> order_history_list){
        this.order_history_list = order_history_list;
    }

    public void setOrder_history_list(ArrayList<Order_history> order_history_list) {
        this.order_history_list = order_history_list;
    }

    public void setTruck_list(ArrayList<Truck> truck_list) {
        this.truck_list = truck_list;
    }
}
