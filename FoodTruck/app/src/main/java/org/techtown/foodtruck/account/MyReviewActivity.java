package org.techtown.foodtruck.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

public class MyReviewActivity extends AppCompatActivity {

    private MyReviewAdapter reviewAdapter;
    private DatabaseReference userReviewDatabase;
    private DatabaseReference truckDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReviewDatabase = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Review");
        truckDatabase = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        setRecycleView();
    }

    private void setRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.activity_my_review_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        reviewAdapter = new MyReviewAdapter(this);
        Query getMyReviewQuery = userReviewDatabase.orderByChild("userIdToken").equalTo(user.getUid());
        getMyReviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewAdapter.addItem(0,review);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(reviewAdapter);
    }
}