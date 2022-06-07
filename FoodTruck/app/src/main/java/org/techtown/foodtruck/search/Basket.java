package org.techtown.foodtruck.search;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.DO.Location;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.MainActivity;
import org.techtown.foodtruck.R;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Basket extends AppCompatActivity {

    //주소설정 textview
    TextView textView1;
    //주소복사 버튼
    TextView textView2;
    //푸드트럭명 textview
    TextView textView3;
    //총 합 금액 textview
    TextView textView4;
    //지도 열기 버튼
    ImageView imageView;
    //메뉴추가 버튼
    Button button1;
    //주문버튼
    Button button2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //order 객체 저장 리스트
    ArrayList<Order> order_list;
    //truck 객체
    Truck truck;
    //총 금액 저장 변수
    int total_cost;
    //주소 저장 변수
    String address;
    //BasketAdapter 변수 설정
    BasketAdapter adapter;
    Gson gson;
    Type listType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        textView1 = findViewById(R.id.basket_textview);
        textView2 = findViewById(R.id.basket_textview2);
        textView3 = findViewById(R.id.basket_textview3);
        textView4 = findViewById(R.id.basket_textview4);
        imageView = findViewById(R.id.basket_imageview);
        button1 = findViewById(R.id.basket_button);
        button2 = findViewById(R.id.basket_button2);
        //order 객체 내용제공자로부터 가져오기
        gson = new GsonBuilder().create();
        sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String data = sharedPreferences.getString("order_list","");
        listType = new TypeToken<ArrayList<Order>>() {}.getType();
        order_list = gson.fromJson(data,listType);
        truck = order_list.get(0).getTruck();
        ActionBar ac = getSupportActionBar();
        ac.setTitle("장바구니");
        total_cost = 0;
        ArrayList<Order> temporary_list = new ArrayList<>();
        //order_list 재구성(list안에 똑같은 food가 중복될수 있어 그것을 제거해주는 로직임)  ex)   list : 1. food1 2개  2.  food2 2개  3. food1 1개 ->  1. food1 3개 2. food2 2개
        for(Order order:order_list){
            if(temporary_list.size() > 0){
                boolean a = false;
                int i = 0;
                //임시 리스트 전체를 탐색
                for(Order temporary_order : temporary_list){
                    //만약 임시리스트에서 order과 같은 종류의 객체가 발견되면(food name이 같은 객체) temporary_list에 add하지 않고 food_number만 증가시켜준다.
                    if(order.getFood_name().equals(temporary_order.getFood_name())){
                        a = true;
                        temporary_list.get(i).setFood_number(temporary_list.get(i).getFood_number() + order.getFood_number());
                        break;
                    }
                    i++;
                }
                //앞에서 임시리스트 전체를 탐색해도 같은 종류의 객체가 발견되지 않았기 때문에 temporary_list에 add해준다.
                if(a == false){
                    temporary_list.add(order);
                }
            }
            //처음에는 그냥 임시 리스트에 order객체 하나 저장
            else{
                temporary_list.add(order);
            }
        }
        order_list = temporary_list;
        for(Order order: order_list ){
            total_cost = total_cost + (order.getFood_number() * Integer.parseInt(order.getFood_cost()));
        }
        //주소 설정
        convertLocationToAddress();
        //푸드트럭명 설정
        textView3.setText(truck.getName());
        //총 금액 설정
        textView4.setText(Integer.toString(total_cost)+"원");
        //recyclerview 설정
        setRecycleView();
        //주소 복사 listener 설정
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(),"주소 복사됨",Toast.LENGTH_SHORT).show();
            }
        });
        //메뉴 추가 listener 설정
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = gson.toJson(order_list,listType);
                editor.putString("order_list",json);
                editor.commit();
                finish();
            }
        });
        //주문 버튼 listener 설정
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences 통해 저장되왔던 장바구니 데이터를 전부 삭제
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(getApplicationContext(),"주문 완료",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(order_list.size() == 0){
            editor.clear();
            editor.commit();
        }
        else{
            String json = gson.toJson(order_list,listType);
            editor.putString("order_list",json);
            editor.commit();
        }
        super.onBackPressed();
    }

    private void convertLocationToAddress(){
        DecimalFormat f = new DecimalFormat("#.##");
        Location location = truck.getLocation();
        List<Address> list= null;
        Geocoder g = new Geocoder(this);
        try{
            list = g.getFromLocation(Double.parseDouble(location.getLatitude()),Double.parseDouble(location.getLongitude()),10);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"실패",Toast.LENGTH_SHORT).show();
        }
        address = list.get(0).getAddressLine(0).substring(5);
        String distance = f.format(truck.getDistance()/1000);
        textView1.setText(address+" (트럭위치로부터 "+distance+"km)");
    }

    private void setRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.basket_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BasketAdapter(this);
        adapter.setItems(order_list);
        recyclerView.setAdapter(adapter);
    }
}