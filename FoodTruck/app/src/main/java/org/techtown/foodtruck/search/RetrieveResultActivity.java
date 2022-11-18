package org.techtown.foodtruck.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.MainActivity;
import org.techtown.foodtruck.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class RetrieveResultActivity extends AppCompatActivity {

    //전 액티비티에서 받아온 위도 경도를 받아오기 위한 변수
    private Double latitude;
    private Double longitude;
    //자기 자신의 위치와 트럭의 위치를 저장하기 위한 변수
    android.location.Location my_location;
    android.location.Location truck_location;
    //자기 자신의 위치와 트럭의 위치 사이의 거리를 저장하기 위한 변수
    double distance;
    //파이어베이스 데이터를 받아오기위한 변수
    DatabaseReference databaseReference;
    //트럭 아이템들을 담는 리스트 변수
    ArrayList<Truck> items;
    //vertical 리싸이클뷰 어뎁터를 위한 변수
    TruckListAdapter adapter;
    //날짜 포멧팅을 위한 변수
    SimpleDateFormat dateFormat;
    //검색 내용
    String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_result);
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //전 액티비티에서 검색내용 가져오기
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        //현재 자신의 거리를 얻어낸다. 얻어내는 것은 sharedPreferences을 통해 알아낸다.
        SharedPreferences sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
        latitude = (double) sharedPreferences.getFloat("latitude", 1.0F);
        longitude = (double) sharedPreferences.getFloat("longitude", 1.0F);
        my_location = new Location("my_location");
        truck_location = new Location("truck_location");
        my_location.setLatitude(latitude);
        my_location.setLongitude(longitude);
        //날짜비교를 위한 날짜 format 객체 생성
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //수직 리싸이클뷰 설정 로직 시작
        setRecycleView();
        //검색창을 선택하면 다시 검색화면으로 간다.
        TextView textView = findViewById(R.id.activity_retrieve_result_retrieve_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),RetrieveActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //뒤로 가기 누르면 다시 MainActivity로 이동
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("startFragment",2);
        startActivity(intent);
    }

    //리싸이클러뷰 설정
    public void setRecycleView() {
        //리싸이클러뷰 객체 레이아웃 객체 생성 items 변수에 adapter.getItems()을 통해 adpater안에 리스트를 참조한다.
        RecyclerView recyclerView = findViewById(R.id.activity_retrieve_result_recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TruckListAdapter();
        items = adapter.getItems();
        //데이터 받아오기
        setDataList();
        //어뎁터 리싸이클뷰에 설정
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new onTruckItemClickListener() {
            @Override
            public void onItemClick(TruckListAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(view.getContext(), DetailedTruckMenuActivity.class);
                Truck truck = adapter.getItem(position);
                intent.putExtra("Truck", truck );
                startActivity(intent);
            }
        });
    }

    public void setDataList() {
        //데이터 받아오고 items에 데이터를 집어넣는다.
        databaseReference =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //데이터 받아오기
                items.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Truck truck = dataSnapshot.getValue(Truck.class);
                    //푸드트럭 위치 자기 자신위치 비교
                    truck_location.setLongitude(Double.parseDouble(truck.getLocation().getLongitude()));
                    truck_location.setLatitude(Double.parseDouble(truck.getLocation().getLatitude()));
                    distance = my_location.distanceTo(truck_location);
                    //검색어가 인기나 신규를 포함한다면 리스트에 넣는다.
                    if(content.contains("인기")||content.contains("신규")){
                        if(distance <= 5000 && content.contains("인기")){
                            items.add(truck);
                            truck.setDistance(distance);
                        }
                        else  if(distance <= 5000 || content.contains("신규")){
                            //신규 푸드트럭이면 open_date가 1달이내인것만 넣는다.
                            try {
                                Date date = dateFormat.parse(truck.getOpen_date());
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.MONTH,-1);
                                String day = dateFormat.format(cal.getTime());
                                if( date.after(dateFormat.parse(day))){
                                    items.add(truck);
                                    truck.setDistance(distance);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //그 밖에 검색어는 데이터들중이 이 검색어 문자열을 포함하고 있으면 리스트에 넣는다.
                    else if(truck.getName().contains(content) || truck.getType().contains(content)){
                        items.add(truck);
                        truck.setDistance(distance);
                    }
                    //정렬, 인기푸드트럭이면 주문횟수에 따라 신규 푸드트럭이면 시작일에 따라 나머지는 거리에 따라 푸드트럭 리스트를 정렬한다.
                    if(content.equals("인기")){
                        Collections.sort(items, new Comparator<Truck>() {
                            @Override
                            public int compare(Truck truck1, Truck truck2) {
                                int truck1_order_count = Integer.parseInt(truck1.getOrder_count());
                                int truck2_order_count= Integer.parseInt(truck2.getOrder_count());
                                //주문 횟수에 따라 정렬 주문횟수가 같으면 평점에 따라 정렬
                                if(( truck1_order_count> truck2_order_count?1:(truck1_order_count < truck2_order_count?-1:0)) == 0){
                                    double truck1_rate = Double.parseDouble(truck1.getRate());
                                    double truck2_rate = Double.parseDouble(truck2.getRate());
                                    return -( truck1_rate> truck2_rate?1:(truck1_rate < truck2_rate?-1:0));
                                }
                                else{
                                    return -( truck1_order_count> truck2_order_count?1:(truck1_order_count < truck2_order_count?-1:0));
                                }
                            }
                        });
                    }
                    else if(content.equals("신규")){
                        Collections.sort(items, new Comparator<Truck>() {
                            @Override
                            public int compare(Truck truck1, Truck truck2) {
                                try {
                                    Date date1 = dateFormat.parse(truck1.getOpen_date());
                                    Date date2 = dateFormat.parse(truck2.getOpen_date());
                                    return -date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                    }
                    else{
                        Collections.sort(items, new Comparator<Truck>() {
                            @Override
                            public int compare(Truck truck1, Truck truck2) {
                                //거리가 같으면 다시 주문횟수에 따라 정렬한다.
                                if(( truck1.getDistance()> truck2.getDistance()?1:(truck1.getDistance() < truck2.getDistance()?-1:0)) == 0){
                                    int truck1_order_count = Integer.parseInt(  truck1.getOrder_count());
                                    int truck2_order_count= Integer.parseInt(truck2.getOrder_count());
                                    return -( truck1_order_count> truck2_order_count?1:(truck1_order_count < truck2_order_count?-1:0));
                                }
                                else{
                                    return ( truck1.getDistance()> truck2.getDistance()?1:(truck1.getDistance() < truck2.getDistance()?-1:0));
                                }
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}