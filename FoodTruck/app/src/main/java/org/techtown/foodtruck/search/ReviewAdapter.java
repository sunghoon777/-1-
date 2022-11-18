package org.techtown.foodtruck.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.R;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private ArrayList<Review> items = new ArrayList<Review>();
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_review_item,parent,false);
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
        RatingBar ratingBar;
        ImageView reviewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.recyclerview_review_item_user_name);
            date = itemView.findViewById(R.id.recyclerview_review_item_date);
            userComment = itemView.findViewById(R.id.recyclerview_review_item_user_comment);
            ratingBar = itemView.findViewById(R.id.recyclerview_review_item_ratingbar);
            reviewImage = itemView.findViewById(R.id.recyclerview_review_item_review_image);
        }

        public void setItem(Review item){
            userName.setText(item.getUserName());
            date.setText(item.getDate());
            userComment.setText(item.getComment());
            ratingBar.setRating(Float.parseFloat(item.getRate()));
            if(item.getImage() != null && item.getImage().length() > 0){
                Glide.with(context).load(item.getImage()).into(reviewImage);
            }
            else{
                reviewImage.getLayoutParams().width = 0;
                reviewImage.getLayoutParams().height = 0;
            }
        }
    }

    public Review getItem(int position){
        return items.get(position);
    }

    public void setItems(ArrayList<Review> items){
        this.items = items;
    }

    public void addItem(Review review){
        items.add(review);
    }

}
