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

import java.util.ArrayList;

public class CategoryImageAdapter extends RecyclerView.Adapter<CategoryImageAdapter.ViewHolder>  {

    ArrayList<Image> items = new ArrayList<Image>();
    onCategoryItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_category_image_item,parent,false);
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
        TextView categoryName;
        ImageView categoryImage;

        public ViewHolder(@NonNull View itemView, onCategoryItemClickListener listener) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.recyclerview_category_image_item_category_name);
            categoryImage = itemView.findViewById(R.id.recyclerview_category_image_item_category_image);
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
            categoryName.setText(item.getContent());
            categoryImage.setImageDrawable(item.getDrawable());
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
    public void setOnItemClickListener(onCategoryItemClickListener listener){
        this.listener = listener;
    }

}
