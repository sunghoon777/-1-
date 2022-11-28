package org.techtown.foodtruck.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.R;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    //order 객체 저장 리스트
    private ArrayList<Order> order_list;
    private PaymentActivity paymentActivity;

    public PaymentAdapter(PaymentActivity paymentActivity) {
        this.paymentActivity = paymentActivity;
    }

    @NonNull
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_payment_item,parent,false);
        return new PaymentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.ViewHolder holder, int position) {
        holder.setItem(getItem(position));
    }

    @Override
    public int getItemCount() {
        return order_list.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        //food 이름
        TextView foodName;
        //food 총 합 가격
        TextView foodCost;
        //food 갯수
        TextView foodCount;
        //x 버튼
        ImageView deleteButton;
        //- 버튼
        ImageView minusButton;
        //+ 버튼
        ImageView plusButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.recyclerview_payment_item_food_name);
            foodCost = itemView.findViewById(R.id.recyclerview_payment_item_food_cost);
            foodCount = itemView.findViewById(R.id.recyclerview_payment_item_food_count);
            deleteButton = itemView.findViewById(R.id.recyclerview_payment_item_delete_button);
            minusButton = itemView.findViewById(R.id.recyclerview_payment_item_minus_button);
            plusButton = itemView.findViewById(R.id.recyclerview_payment_item_plus_button);
            //x버튼 눌렀을시 리스너 설정 해당 item을 삭제한다.
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    paymentActivity.totalCostValue = paymentActivity.totalCostValue - (Integer.parseInt(getItem(position).getFood_cost()) * getItem(position).getFood_number());
                    order_list.remove(position);
                    notifyDataSetChanged();
                    paymentActivity.totalCost.setText(Integer.toString(paymentActivity.totalCostValue)+"원");
                }
            });
            //- + 버튼 눌렀을시 리스너 설정
            //- 버튼 해당 item의 개수를 하나 줄임
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Order order = getItem(position);
                    if(order.getFood_number() > 1){
                        //1감소 시킴
                        order.setFood_number( order.getFood_number() - 1);
                        //textview 에 개수와 총 합을 최신화
                        foodCount.setText(Integer.toString(order.getFood_number())+"개");
                        paymentActivity.totalCostValue = paymentActivity.totalCostValue - Integer.parseInt(order.getFood_cost());
                        paymentActivity.totalCost.setText(Integer.toString(paymentActivity.totalCostValue)+"원");
                    }
                }
            });
            //+ 버튼
            // + 버튼 해당 item의 개수를 하나 늘림
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Order order = getItem(position);
                    order.setFood_number( order.getFood_number() + 1);
                    foodCount.setText(Integer.toString(order.getFood_number())+"개");
                    paymentActivity.totalCostValue = paymentActivity.totalCostValue + Integer.parseInt(order.getFood_cost());
                    paymentActivity.totalCost.setText(Integer.toString(paymentActivity.totalCostValue)+"원");
                }
            });

        }

        public void setItem(Order order){
            foodName.setText(order.getFood_name());
            foodCost.setText(order.getFood_cost()+"원");
            foodCount.setText(Integer.toString(order.getFood_number())+"개");
        }
    }

    public Order getItem(int position){
        return order_list.get(position);
    }

    public void setItems(ArrayList<Order> order_list){
        this.order_list = order_list;
    }

}
