package org.techtown.foodtruck.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TruckReviewFragment extends Fragment {

    //리뷰 리스트
    private ArrayList<Review> items;
    //트럭 데이터
    private DatabaseReference databaseReference;
    private Truck truck;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_truck_review, container, false);
        //액티비티에서 bundle 받아오기
        Bundle bundle = getArguments();
        truck = (Truck) bundle.getSerializable("Truck");
        items = new ArrayList<>();
        setRecycleView(rootView);
        return  rootView;
    }

    //리싸이클러뷰 설정
    private void setRecycleView(ViewGroup rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_truck_review_recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(linearLayoutManager);
        ReviewAdapter reviewAdapter = new ReviewAdapter(rootView.getContext());
        reviewAdapter.setItems(items);
        //리뷰 데이터 받아오기
        Query query = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Review").orderByChild("truckId").equalTo(truck.getId());
        //트럭 데이터 받아오기
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck").child(truck.getId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                //데이터 받아오고 리스트에 추가
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Review review = dataSnapshot.getValue(Review.class);
                    items.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
                /*
                사용자가 TruckReviewFragment를 포함하는 DetailedTruckMenuActivity에서 리뷰를 하게되면 리뷰가 갱신되면서
                TruckReviewFragment에서 다시 변경된 데이터를 감지하여 리뷰평점을 계산을 하여 트럭의 리뷰평점을 갱신하게된다.
                 */
                float sum = 0;
                for(Review review:items){
                    sum += Float.parseFloat(review.getRate());
                }
                float average = sum/items.size();
                HashMap hashMap = new HashMap();
                hashMap.put("rate",Float.toString(average));
                databaseReference.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(reviewAdapter);
    }

}