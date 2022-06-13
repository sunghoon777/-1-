package org.techtown.foodtruck.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Truck_review extends Fragment {

    ArrayList<Review> items;
    DatabaseReference databaseReference;
    DatabaseReference reviewDatabase;
    Truck truck;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_truck_review_fragment, container, false);
        //액티비티에서 bundle 받아오기
        Bundle bundle = getArguments();
        truck = (Truck) bundle.getSerializable("Truck");
        items = new ArrayList<>();
        setRecycleView(rootView);
        return  rootView;
    }

    private void setRecycleView(ViewGroup rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.review_recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(linearLayoutManager);
        ReviewAdapter reviewAdapter = new ReviewAdapter(rootView.getContext());
        reviewAdapter.setItems(items);
        databaseReference =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Review").child(truck.getId());
        reviewDatabase = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck").child(truck.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Review review = dataSnapshot.getValue(Review.class);
                    items.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
                float sum = 0;
                for(Review review:items){
                    sum += Float.parseFloat(review.getRate());
                }
                float average = sum/items.size();
                HashMap hashMap = new HashMap();
                hashMap.put("rate",Float.toString(average));
                reviewDatabase.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(reviewAdapter);
    }

}