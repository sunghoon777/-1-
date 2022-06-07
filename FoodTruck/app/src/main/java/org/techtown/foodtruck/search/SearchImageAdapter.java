package org.techtown.foodtruck.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.account.AccountAdapter;
import org.techtown.foodtruck.account.OnAccountItemClickListener;

import java.util.ArrayList;

public class SearchImageAdapter extends RecyclerView.Adapter<SearchImageAdapter.ViewHolder>  {

    ArrayList<Image> items = new ArrayList<Image>();
    onSearchItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cardimage_layout,parent,false);
        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = items.get(position);
        holder.setItem(image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView, onSearchItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.card_textview);
            imageView = itemView.findViewById(R.id.card_imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this,v,position);
                    }
                }
            });
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

    public Image getItem(int position){
        return items.get(position);
    }

    public  void setItem(int position, Image item){
        items.set(position, item);
    }

    //item click 리스너 로직
    public void setOnItemClickListener(onSearchItemClickListener listener){
        this.listener = listener;
    }

}
