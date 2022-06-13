package org.techtown.foodtruck.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Order_history;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.util.ArrayList;
import java.util.Collections;


public class Order_history_fragment extends Fragment {

    //데이터베이스 참조 변수
    DatabaseReference order_history_database;
    DatabaseReference truck_database;
    //파이어베이스 인증
    FirebaseAuth mAuth;
    FirebaseUser user;
    ArrayList<Order_history> order_history_list;
    ArrayList<Truck> my_truck_list;
    ArrayList<Truck> all_truck_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_order_history_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        order_history_database =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Order_history").child(user.getUid());
        truck_database =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        order_history_list = new ArrayList<>();
        my_truck_list = new ArrayList<>();
        all_truck_list = new ArrayList<>();
        setRecycelView(rootView);
        return rootView;
    }

    private void setRecycelView(ViewGroup rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.order_history_recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(getContext(),getActivity());
        orderHistoryAdapter.setOrder_history_list(order_history_list);
        orderHistoryAdapter.setTruck_list(my_truck_list);
        truck_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                all_truck_list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Truck truck = dataSnapshot.getValue(Truck.class);
                    all_truck_list.add(truck);
                }
                order_history_database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        order_history_list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Order_history order_history = dataSnapshot.getValue(Order_history.class);
                            order_history_list.add(order_history);
                            for(Truck truck : all_truck_list){
                                if(truck.getId().equals(order_history.getTruck_id())){
                                    my_truck_list.add(truck);
                                }
                            }
                        }
                        Collections.reverse(order_history_list);
                        Collections.reverse(my_truck_list);
                        orderHistoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(orderHistoryAdapter);
    }
}