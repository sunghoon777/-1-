package org.techtown.foodtruck.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.OrderHistory;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.util.ArrayList;


public class OrderHistoryFragment extends Fragment {

    //데이터베이스 참조 변수
    private DatabaseReference orderHistoryDatabase;
    private DatabaseReference truckDatabase;
    //파이어베이스 인증
    private FirebaseUser user;
    private ArrayList<OrderHistory> orderHistoryList;
    private ArrayList<Truck> myTruckList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_order_history, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        //데이터저장소 참조
        orderHistoryDatabase =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("OrderHistory");
        orderHistoryList = new ArrayList<OrderHistory>();
        myTruckList = new ArrayList<Truck>();
        setRecycelView(rootView);
        return rootView;
    }


    //리싸이클러뷰 설정
    private void setRecycelView(ViewGroup rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_order_history_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(getContext(),getActivity());
        orderHistoryAdapter.setItems(orderHistoryList);
        orderHistoryAdapter.setItems2(myTruckList);
        Query query = orderHistoryDatabase.orderByChild("userId").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderHistoryList.clear();
                myTruckList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrderHistory orderHistory = dataSnapshot.getValue(OrderHistory.class);
                    String state = orderHistory.getOrderState();
                    if(state.equals("완료")){
                        orderHistoryList.add(0,orderHistory);
                        FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck").child(orderHistory.getTruckId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Truck truck = snapshot.getValue(Truck.class);
                                myTruckList.add(0,truck);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                orderHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(orderHistoryAdapter);
    }
}