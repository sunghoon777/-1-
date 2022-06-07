package org.techtown.foodtruck.search;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.R;

import java.util.ArrayList;

public class TruckListHorizontalAdapter extends RecyclerView.Adapter<TruckListHorizontalAdapter.ViewHolder> {

    ArrayList<Image> items = new ArrayList<Image>();
    onTruckHorizontalItemListener listener;
    //현재 content를 담는 변수
    boolean check = true;
    int row_index = -1;

    static  private RecyclerView recyclerView = null;

    public TruckListHorizontalAdapter(String content) {
        switch(content){
            case "인기 푸드트럭" : row_index = 0;
                break;
            case "신규 푸드트럭" : row_index = 1;
                break;
            case "햄버거" : row_index = 2;
                break;
            case "디저트" : row_index = 3;
                break;
            case "닭꼬치" : row_index = 4;
                break;
            case "스테이크" : row_index = 5;
                break;
            case "분식" : row_index = 6;
                break;
            case "일식" : row_index = 7;
                break;
            case "피자" : row_index = 8;
                break;
            case "치킨" : row_index = 9;
                break;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.food_search_tab_item,parent,false);
        return new TruckListHorizontalAdapter.ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = items.get(position);
        holder.setItem(image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder,v,position);
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if(check){
            if(row_index == position){
                holder.cardView.setBackgroundResource(R.drawable.change_bg);
            }
            else{
                holder.cardView.setBackgroundResource(R.drawable.default_bg);
            }
            check = false;
        }
        else{
            if(row_index == position){
                holder.cardView.setBackgroundResource(R.drawable.change_bg);
            }
            else{
                holder.cardView.setBackgroundResource(R.drawable.default_bg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView, onTruckHorizontalItemListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.food_search_tab_item_textView);
            imageView = itemView.findViewById(R.id.food_search_tab_item_imageView);
            cardView = itemView.findViewById(R.id.food_search_tab_item_cardView);
        }

        public void setItem(Image item){
            textView.setText(item.getContent());
            imageView.setImageDrawable(item.getDrawable());
        }
    }

    public void addItem(Image item){
        items.add(item);
    }

    public void setItems(ArrayList<Image> items){
        this.items = items;
    }

    public ArrayList<Image> getItems() {
        return items;
    }

    public Image getItem(int position){
        return items.get(position);
    }

    public  void setItem(int position, Image item){
        items.set(position, item);
    }

    //item click 리스너 로직
    public void setOnItemClickListener(onTruckHorizontalItemListener listener){
        this.listener = listener;
    }

}
