package org.techtown.foodtruck.search;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Favorite;
import org.techtown.foodtruck.DO.Food;
import org.techtown.foodtruck.DO.Location;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.login.LoginActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class TruckMenuFragment extends Fragment {
    
    //트럭 메뉴 리스트
    private ArrayList<Food> items;
    //트럭 객체
    private Truck truck;
    //어뎁터
    private TruckMenuAdapter truckMenuAdapter;
    //주소 내용
    private TextView address;
    //즐겨찾기 아이콘
    private ImageView favoriteIcon;
    //저장소 변수
    private FirebaseAuth auth;
    //truck 저장소 참조
    private DatabaseReference databaseReference;
    //favorite 저장소 참조
    private DatabaseReference databaseReference2;
    private FirebaseUser user;
    //즐겨찾기 체크했는지 여부 변수
    private Boolean favoriteCheck = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_truck_menu, container, false);
        //액티비티에서 bundle 받아오기
        Bundle bundle = getArguments();
        truck = (Truck) bundle.getSerializable("Truck");
        //truck 위치를 주소로 변환
        address = rootView.findViewById(R.id.fragment_truck_menu_address);
        convertLocationToAddress();
        //지도 아이콘 클릭시 지도 보여주기
        favoriteIcon = rootView.findViewById(R.id.fragment_truck_menu_favorite);
        //리싸이클뷰 설정
        //로그인 인증을 위한 객체 생성
        auth = FirebaseAuth.getInstance();
        setRecycleView(rootView);
        return  rootView;
    }
    
    //경위도를 주소로 변환해주는 메서드
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
        String distance = f.format(truck.getDistance()/1000);
        address.setText(list.get(0).getAddressLine(0).substring(5)+" (트럭위치로부터 "+distance+"km)");
    }

    //리싸이클러뷰 설정
    private void setRecycleView(ViewGroup rootView){
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_truck_menu_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        truckMenuAdapter = new TruckMenuAdapter(getContext());
        items = truckMenuAdapter.getItems();
        //트럭 데이터 참조
        databaseReference =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Food").child(truck.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                //메뉴를 받아온다.
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Food food = dataSnapshot.getValue(Food.class);
                    items.add(food);
                }
                user = auth.getCurrentUser();
                if(user != null){
                    //즐겨찾기 데이터를 가져온다.
                    databaseReference2 = FirebaseDatabase.getInstance().getReference("FoodTruck").child("UserAccount").child(user.getUid()).child("favorites");
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                                    Favorite favorite = dataSnapshot.getValue(Favorite.class);
                                    if(favorite.getTruckId().equals(truck.getId())){
                                        databaseReference = dataSnapshot.getRef();
                                        favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                        favoriteCheck = true;
                                        break;
                                    }
                                }
                                if(favoriteCheck == false){
                                    favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24_false);
                                }
                            }
                            else{
                                favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24_false);
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
                    favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24_false);
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
                    Intent intent = new Intent(view.getContext(), AddToBasketActivity.class);
                    intent.putExtra("Food",truckMenuAdapter.getItem(position));
                    intent.putExtra("Truck",truck);
                    startActivity(intent);
                }
            }
        });
        //즐겨찾기 클릭 리스너 설정
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
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
                       databaseReference.removeValue();
                   }
                   //즐겨찾기가 안되있을떄
                   else{
                       favoriteCheck = true;
                       databaseReference2.push().setValue(new Favorite(truck.getName(), truck.getId()));
                   }
               }
            }
        });
    }
}