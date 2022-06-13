package org.techtown.foodtruck.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.R;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    ArrayList<Review> items;
    Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.review_recycleview,parent,false);
        return new ReviewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review item = getItem(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        TextView date;
        TextView userComment;
        TextView order_list;
        RatingBar ratingBar;
        ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.review_recycleView_userName);
            date = itemView.findViewById(R.id.review_recycleView_date);
            userComment = itemView.findViewById(R.id.review_recycleView_userComment);
            order_list = itemView.findViewById(R.id.review_recycleView_order_list);
            ratingBar = itemView.findViewById(R.id.review_recycleView_ratingbar);
            foodImage = itemView.findViewById(R.id.review_recycleView_foodImage);
        }

        public void setItem(Review item){
            userName.setText(item.getUserName());
            date.setText(item.getDate());
            userComment.setText(item.getComment());
            String orders = "";
            order_list.setText(orders);
            ratingBar.setRating(Float.parseFloat(item.getRate()));
            if(item.getImage() != null){
                if(item.getImage().length() > 0){
                    Glide.with(context).load(item.getImage()).into(foodImage);
                }
                else{
                    foodImage.getLayoutParams().width = 0;
                    foodImage.getLayoutParams().height = 0;
                }
            }
            else{
                foodImage.getLayoutParams().width = 0;
                foodImage.getLayoutParams().height = 0;
            }
        }
    }

    public Review getItem(int position){
        return items.get(position);
    }

    public void setItems(ArrayList<Review> items){
        this.items = items;
    }

}
