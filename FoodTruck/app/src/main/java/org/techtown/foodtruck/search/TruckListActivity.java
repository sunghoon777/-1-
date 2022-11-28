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
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TruckListActivity extends AppCompatActivity {

    //전 액티비티에서 받아온 위도 경도를 받아오기 위한 변수
    Double latitude;
    Double longitude;
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
    //content를 저장하기 위한 변수 이것으로 파이어베이스에서 items에 어떠한 카테고리의 item을 넣을지 결정한다.
    String content;
    //날짜 포멧팅을 위한 변수
    SimpleDateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_list);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //전 액티비티로부터 데이터 받고 content 데이터 얻고  activity_truck_list_textView에 content 설정
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        //현재 자신의 거리를 얻어낸다. 얻어내는 것은 sharedPreferences을 통해 알아낸다.
        SharedPreferences sharedPreferences = getSharedPreferences("location",Context.MODE_PRIVATE);
        latitude = (double) sharedPreferences.getFloat("latitude", 1.0F);
        longitude = (double) sharedPreferences.getFloat("longitude", 1.0F);
        my_location = new Location("my_location");
        truck_location = new Location("truck_location");
        my_location.setLatitude(latitude);
        my_location.setLongitude(longitude);
        //날짜비교를 위한 날짜 format 객체 생성
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //setRecycleView 수평 리싸이클 수직 리싸이클 순대로 지정함.
        //수평 리싸이클뷰 설정 로직 시작
        setHorizontalRecycleView();
        //수직 리싸이클뷰 설정 로직 시작
        setRecycleView();
    }

    //아이템 클릭시 발생하는 로직2 , 로직1에서 말했듯이 content 데이터를 Intent를 통해 보냈기 때문에 Intent에서 content 데이터를 꺼내와서 content 변수에 저장한다.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Intent에서 content 데이터를 꺼내와서 content 변수에 저장한다.
        String content_for_submit = intent.getStringExtra("content_for_submit");
        if(content_for_submit != null && content_for_submit.length() >0){
            content = content_for_submit;
            //파이어베이스에서 데이터를 읽어온 다음에 리싸이클뷰 다시 설정
            setDataList();
        }
    }

    //수평 리싸이클뷰
    public void setHorizontalRecycleView(){
        //리싸이클뷰 객체를 생성후 레이아웃과 어뎁터 객체를 생성한다.
        RecyclerView recyclerView = findViewById(R.id.activity_truck_list_horizontal_recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        TruckListHorizontalAdapter truckListHorizontalAdapter = new TruckListHorizontalAdapter(content);
        //리싸이클뷰 안에 들어갈 아이템들을 생성한 다음 truckListHorizontalAdapter에 속하는 list 안에 아이템을 집어넣는다
        Image image1= new Image(getResources().getDrawable(R.drawable.fire), "인기");
        Image image2= new Image(getResources().getDrawable(R.drawable.open_icon), "신규");
        Image image3 = new Image(getResources().getDrawable(R.drawable.hamburger_icon), "햄버거");
        Image image4 = new Image(getResources().getDrawable(R.drawable.dessert_icon), "디저트");
        Image image5= new Image(getResources().getDrawable(R.drawable.chicken_skewer_icon), "닭꼬치");
        Image image6= new Image(getResources().getDrawable(R.drawable.steak_icon), "스테이크");
        Image image7= new Image(getResources().getDrawable(R.drawable.korean_icon), "분식");
        Image image8= new Image(getResources().getDrawable(R.drawable.japanese_icon), "일식");
        Image image9= new Image(getResources().getDrawable(R.drawable.pizza_icon), "피자");
        Image image10= new Image(getResources().getDrawable(R.drawable.chicken_icon), "치킨");
        truckListHorizontalAdapter.addItem(image1);
        truckListHorizontalAdapter.addItem(image2);
        truckListHorizontalAdapter.addItem(image3);
        truckListHorizontalAdapter.addItem(image4);
        truckListHorizontalAdapter.addItem(image5);
        truckListHorizontalAdapter.addItem(image6);
        truckListHorizontalAdapter.addItem(image7);
        truckListHorizontalAdapter.addItem(image8);
        truckListHorizontalAdapter.addItem(image9);
        truckListHorizontalAdapter.addItem(image10);

        //리싸이클뷰의 adapter 설정
        recyclerView.setAdapter(truckListHorizontalAdapter);
        //아이템 클릭시 발생하는 로직1 , 아이템 클릭시 결과적으로 파이어베이스에서 다시 데이터를 받아오고 라싸이클뷰를 다시 갱신하게 된다.
        truckListHorizontalAdapter.setOnItemClickListener(new onTruckHorizontalItemListener() {
            @Override
            public void onItemClick(TruckListHorizontalAdapter.ViewHolder holder, View view, int position) {
                //자기 자신으로 가는 intent 객체 생성
                Intent intent = new Intent(getApplicationContext(),TruckListActivity.class);
                //truckListHorizontalAdapter으로 부터 content를 얻어낸다.
                String content_for_submit = truckListHorizontalAdapter.getItem(position).getContent();
                //truckListHorizontalAdapter의 content는 인기 푸드트럭이 ui 디자인을 위해 인기로 신규 푸드트럭은 신규로 설정되어 있으므로 바꾸어준다 content를 원래대로 바꿈
                if(content_for_submit.equals("인기")){
                    content_for_submit = "인기 푸드트럭";
                }
                else if(content_for_submit.equals("신규")){
                    content_for_submit = "신규 푸드트럭";
                }
                //아이템 클릭을 하므로 기존의 activity_truck_list_list_if_empty 이미지(빈 상자 이미지)는 다시 크기를 0으로 바꾸어준다.
                ImageView activity_truck_list_list_if_empty = findViewById(R.id.activity_truck_list_list_if_empty);
                activity_truck_list_list_if_empty.getLayoutParams().width = 0;
                activity_truck_list_list_if_empty.getLayoutParams().height = 0;
                activity_truck_list_list_if_empty.requestLayout();
                //재사용될 activity에 content를 보내주기위해 intent를 생성한 다음 intent에 content 데이터를 넣어준다.
                intent.putExtra("content_for_submit",content_for_submit);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //리싸이클뷰 재설정을 위한 로직, 자세한 설명은 setDataList 메서드 마지막에 있는 주석과 같이 보세요
                SharedPreferences sharedPreferences = getSharedPreferences("IsChange?", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IsChange?",true);
                editor.commit();
                startActivity(intent);
            }
        });
    }

    //수직 리싸이클뷰
    public void setRecycleView(){
        //리싸이클러뷰 객체 레이아웃 객체 생성 items 변수에 adapter.getItems()을 통해 adpater안에 리스트를 참조한다.
        RecyclerView recyclerView = findViewById(R.id.activity_truck_list_recycleView);
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
    
    public void setDataList(){
        //데이터 받아오고 items에 데이터를 집어넣는다.
        databaseReference =  FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //데이터 받아오기
                items.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Truck truck = dataSnapshot.getValue(Truck.class);
                    //트럭 상태가 1 닫혀있는 상태이면 결과에 포함 안됨
                    if(truck.getVendor_status().equals("1"))
                        continue;
                    //푸드트럭 위치 자기 자신위치 비교
                    truck_location.setLongitude(Double.parseDouble(truck.getLocation().getLongitude()));
                    truck_location.setLatitude(Double.parseDouble(truck.getLocation().getLatitude()));
                    distance = my_location.distanceTo(truck_location);
                    //인기 푸드트럭이거나 신규 푸드트럭이면 우선 5km이내 인것은 다 리스트에 넣는다.
                    if(content.equals("인기 푸드트럭")||content.equals("신규 푸드트럭")){
                        if(distance <= 5000 && content.equals("인기 푸드트럭")){
                            items.add(truck);
                            truck.setDistance(distance);
                        }
                        else  if(distance <= 5000 || content.equals("신규푸드트럭")){
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
                    //그 외에는 5km 이내이기도 하고 자기 카테고리에도 맞아야 리스트에 넣는다.
                    else{
                        if(distance <= 5000 && truck.getType().equals(content)){
                            items.add(truck);
                            truck.setDistance(distance);
                        }
                    }
                }

                //정렬, 인기푸드트럭이면 주문횟수에 따라 신규 푸드트럭이면 시작일에 따라 나머지는 거리에 따라 푸드트럭 리스트를 정렬한다.
                if(content.equals("인기 푸드트럭")){
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
                else if(content.equals("신규 푸드트럭")){
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
                //받아온 데이터가 하나도 없으면 리스트의 길이가 0이 되고 그러면 activity_truck_list_list_if_empty 이미지 뷰(빈 상자 이미지)의 크기를 설정해준다.
                if(items.size() == 0){
                    ImageView activity_truck_list_list_if_empty = findViewById(R.id.activity_truck_list_list_if_empty);
                    activity_truck_list_list_if_empty.getLayoutParams().width = 500;
                    activity_truck_list_list_if_empty.getLayoutParams().height = 500;
                    activity_truck_list_list_if_empty.requestLayout();
                }

                //아이템 클릭시 발생하는 로직3 , 만약 리싸이클뷰가 처음 생겨나 설정된것이라면 뒤에 코드는 실행되지 않고 수평 리싸이클뷰 클릭으로 인해
                //리싸이클뷰가 재설정 될 때만 이것이 실행되어진다.
                SharedPreferences sharedPreferences = getSharedPreferences("IsChange?", MODE_PRIVATE);
                boolean isChange = sharedPreferences.getBoolean("IsChange?",false);
                if(isChange == true){
                    adapter.notifyDataSetChanged();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("IsChange?",false);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}