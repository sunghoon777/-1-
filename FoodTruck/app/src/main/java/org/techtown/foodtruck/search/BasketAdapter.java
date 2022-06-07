package org.techtown.foodtruck.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.util.ArrayList;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {

    //order 객체 저장 리스트
    ArrayList<Order> order_list;
    Basket basket;

    public BasketAdapter(Basket basket) {
        this.basket = basket;
    }

    @NonNull
    @Override
    public BasketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.basket_recycleview_layout,parent,false);
        return new BasketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketAdapter.ViewHolder holder, int position) {
        holder.setItem(getItem(position));
    }

    @Override
    public int getItemCount() {
        return order_list.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        //food 이름
        TextView textView1;
        //food 총 합 가격
        TextView textView2;
        //food 갯수
        TextView textView3;
        //x 버튼
        ImageView imageView1;
        //- 버튼
        ImageView imageView2;
        //+ 버튼
        ImageView imageView3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.basket_recyclerView_textview);
            textView2 = itemView.findViewById(R.id.basket_recyclerView_textview2);
            textView3 = itemView.findViewById(R.id.basket_recyclerView_textview3);
            imageView1 = itemView.findViewById(R.id.basket_recyclerView_imageview);
            imageView2 = itemView.findViewById(R.id.basket_recyclerView_imageview2);
            imageView3 = itemView.findViewById(R.id.basket_recyclerView_imageview3);
            //x버튼 눌렀을시 리스너 설정 해당 item을 삭제한다.
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    basket.total_cost = basket.total_cost - (Integer.parseInt(getItem(position).getFood_cost()) * getItem(position).getFood_number());
                    order_list.remove(position);
                    notifyDataSetChanged();
                    basket.textView4.setText(Integer.toString(basket.total_cost)+"원");
                }
            });
            //- + 버튼 눌렀을시 리스너 설정
            //- 버튼 해당 item의 개수를 하나 줄임
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Order order = getItem(position);
                    if(order.getFood_number() > 1){
                        //1감소 시킴
                        order.setFood_number( order.getFood_number() - 1);
                        //textview 에 개수와 총 합을 최신화
                        textView3.setText(Integer.toString(order.getFood_number())+"개");
                        basket.total_cost = basket.total_cost - Integer.parseInt(order.getFood_cost());
                        basket.textView4.setText(Integer.toString(basket.total_cost)+"원");
                    }
                }
            });
            //+ 버튼
            // + 버튼 해당 item의 개수를 하나 늘림
            imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Order order = getItem(position);
                    order.setFood_number( order.getFood_number() + 1);
                    textView3.setText(Integer.toString(order.getFood_number())+"개");
                    basket.total_cost = basket.total_cost + Integer.parseInt(order.getFood_cost());
                    basket.textView4.setText(Integer.toString(basket.total_cost)+"원");
                }
            });

        }

        public void setItem(Order order){
            textView1.setText(order.getFood_name());
            textView2.setText(order.getFood_cost()+"원");
            textView3.setText(Integer.toString(order.getFood_number())+"개");
        }
    }

    public Order getItem(int position){
        return order_list.get(position);
    }

    public void setItems(ArrayList<Order> order_list){
        this.order_list = order_list;
    }

}
