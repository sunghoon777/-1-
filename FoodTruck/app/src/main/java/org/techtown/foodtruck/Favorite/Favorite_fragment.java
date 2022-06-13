package org.techtown.foodtruck.Favorite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Favorite;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.search.DetailedTruckMenu;
import org.techtown.foodtruck.search.TruckListAdapter;
import org.techtown.foodtruck.search.onTruckItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Favorite_fragment extends Fragment {

    //트럭 아이템들을 담는 리스트 변수
    ArrayList<Truck> items;
    //파이어베이스 데이터를 받아오기위한 변수
    //트럭 데이터
    DatabaseReference truckData;
    //즐겨찾기 데이터
    DatabaseReference favoriteData;
    //파이어 베이스 인증을 위한 변수
    FirebaseAuth auth;
    //현재 파이어베이스 유저를 위한 변수
    FirebaseUser user;
    SimpleDateFormat dateFormat;
    ArrayList<DataSnapshot> favorites = new ArrayList<>();
    //전 액티비티에서 받아온 위도 경도를 받아오기 위한 변수
    Double latitude;
    Double longitude;
    //자기 자신의 위치와 트럭의 위치를 저장하기 위한 변수
    android.location.Location my_location;
    android.location.Location truck_location;
    //자기 자신의 위치와 트럭의 위치 사이의 거리를 저장하기 위한 변수
    double distance;
    //즐겨찾기 총 개수 저장 변수
    int sum = 0;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_favorite, container, false);
        //현재 자신의 거리를 얻어낸다. 얻어내는 것은 sharedPreferences을 통해 알아낸다.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
        latitude = (double) sharedPreferences.getFloat("latitude", 1.0F);
        longitude = (double) sharedPreferences.getFloat("longitude", 1.0F);
        my_location = new Location("my_location");
        truck_location = new Location("truck_location");
        my_location.setLatitude(latitude);
        my_location.setLongitude(longitude);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        setRecycleView(rootView);
        return rootView;
    }
    
    //리싸이클뷰 설정
    public void setRecycleView(ViewGroup rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.favorite_recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(rootView.getContext());
        items = favoriteAdapter.getItems();
        recyclerView.setAdapter(favoriteAdapter);
        //클릭 리스너 설정
        favoriteAdapter.setOnItemClickListener(new onFavoriteItemClickListener() {
            @Override
            public void onItemClick(FavoriteAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(view.getContext(), DetailedTruckMenu.class);
                Truck truck = favoriteAdapter.getItem(position);
                intent.putExtra("Truck", truck );
                startActivity(intent);
            }
        });
        //데이터 받아오기
        favoriteData = FirebaseDatabase.getInstance().getReference("FoodTruck").child("UserAccount").child(user.getUid()).child("favorites");
        truckData = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        favoriteData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sum = 0;
                favorites.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    favorites.add(dataSnapshot);
                    sum++;
                }
                ((TextView) rootView.findViewById(R.id.favorite_sum)).setText("총 "+Integer.toString(sum)+"개");
                truckData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        items.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Truck truck = dataSnapshot.getValue(Truck.class);
                            for(DataSnapshot favorite_dataReference : favorites){
                                Favorite favorite = favorite_dataReference.getValue(Favorite.class);
                                if(favorite.getTruckId().equals(truck.getId())){
                                    //푸드트럭 위치 자기 자신위치 비교
                                    truck_location.setLongitude(Double.parseDouble(truck.getLocation().getLongitude()));
                                    truck_location.setLatitude(Double.parseDouble(truck.getLocation().getLatitude()));
                                    distance = my_location.distanceTo(truck_location);
                                    truck.setDistance(distance);
                                    items.add(truck);
                                }
                            }
                        }
                        favoriteAdapter.notifyDataSetChanged();
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


    }
}