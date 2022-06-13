package org.techtown.foodtruck.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Favorite;
import org.techtown.foodtruck.DO.Food;
import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.DO.Location;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.login.LoginActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Truck_menu extends Fragment {

    ArrayList<Food> items;
    DatabaseReference databaseReference;
    Truck truck;
    TruckMenuAdapter truckMenuAdapter;
    TextView textView;
    ImageView imageView;
    FirebaseAuth auth;
    DatabaseReference FavoriteDatabase;
    FirebaseUser user;
    Boolean favoriteCheck = false;
    DatabaseReference favorite_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_truck_menu_fragment, container, false);
        //액티비티에서 bundle 받아오기
        Bundle bundle = getArguments();
        truck = (Truck) bundle.getSerializable("Truck");
        //truck 위치를 주소로 변환
        textView = rootView.findViewById(R.id.truck_menu_fragment_textview);
        convertLocationToAddress();
        //지도 아이콘 클릭시 지도 보여주기
        imageView = rootView.findViewById(R.id.truck_menu_fragment_favorite);
        //리싸이클뷰 설정
        //로그인 인증을 위한 객체 생성
        auth = FirebaseAuth.getInstance();
        setRecycleView(rootView);
        return  rootView;
    }

    private void convertLocationToAddress(){
        DecimalFormat f = new DecimalFormat("#.##");
        Location location = truck.getLocation();
        List<Address> list= null;
        Geocoder g = new Geocoder(getActivity());
        try{
            list = g.getFromLocation(Double.parseDouble(location.getLatitude()),Double.parseDouble(location.getLongitude()),10);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"실패",Toast.LENGTH_SHORT).show();
        }
        String address = list.get(0).getAddressLine(0).substring(5);
        String distance = f.format(truck.getDistance()/1000);
        textView.setText(address+" (트럭위치로부터 "+distance+"km)");
    }

    private void setRecycleView(ViewGroup rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_truck_menu_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        truckMenuAdapter = new TruckMenuAdapter(getContext());
        items = truckMenuAdapter.getItems();
        databaseReference =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Food").child(truck.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Food food = dataSnapshot.getValue(Food.class);
                    items.add(food);
                }
                user = auth.getCurrentUser();
                if(user != null){
                    FavoriteDatabase = FirebaseDatabase.getInstance().getReference("FoodTruck").child("UserAccount").child(user.getUid()).child("favorites");
                    FavoriteDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                                    Favorite favorite = dataSnapshot.getValue(Favorite.class);
                                    if(favorite.getTruckId().equals(truck.getId())){
                                        favorite_data = dataSnapshot.getRef();
                                        imageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                                        favoriteCheck = true;
                                        break;
                                    }
                                }
                                if(favoriteCheck == false){
                                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24_false);
                                }
                            }
                            else{
                                imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24_false);
                            }
                            truckMenuAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    truckMenuAdapter.notifyDataSetChanged();
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24_false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(truckMenuAdapter);
        truckMenuAdapter.setOnItemClickListener(new onMenuItemClickListener() {
            @Override
            public void onItemClick(TruckMenuAdapter.ViewHolder holder, View view, int position) {
                if(auth.getCurrentUser() == null) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(view.getContext(),Food_Detailed_activity.class);
                    intent.putExtra("Food",truckMenuAdapter.getItem(position));
                    intent.putExtra("Truck",truck);
                    startActivity(intent);
                }
            }
        });
        //즐겨찾기 클릭 리스너 설정
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(user == null){
                   Intent intent = new Intent(getContext(), LoginActivity.class);
                   startActivity(intent);
               }
               else{
                   //즐겨찾기가 되있을때
                   if(favoriteCheck == true){
                       favoriteCheck = false;
                       favorite_data.removeValue();
                   }
                   //즐겨찾기가 안되있을떄
                   else{
                       favoriteCheck = true;
                       FavoriteDatabase.push().setValue(new Favorite(truck.getName(), truck.getId()));
                   }
               }
            }
        });
    }
}