package org.techtown.foodtruck.search;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.foodtruck.DO.Food;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Food_Detailed_activity extends AppCompatActivity {

    ImageView imageView;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    ImageView minus;
    ImageView plus;
    Button button;
    Food food;
    Truck truck;
    private int order_number = 1;
    private int total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detailed);
        Intent intent = getIntent();
        food = (Food) intent.getSerializableExtra("Food");
        truck = (Truck) intent.getSerializableExtra("Truck");
        ActionBar ac = getSupportActionBar();
        ac.setTitle(food.getName());
        imageView = findViewById(R.id.food_detailed_imageview);
        textView1 = findViewById(R.id.food_detailed_textview1);
        textView2 = findViewById(R.id.food_detailed_textview2);
        textView3 = findViewById(R.id.food_detailed_textview3);
        textView4 = findViewById(R.id.food_detailed_textview4);
        button = findViewById(R.id.food_detailed_button);
        //내용 설정
        Glide.with(this).load(food.getImage()).into(imageView);
        textView1.setText(food.getName());
        textView2.setText(food.getContent());
        textView3.setText(food.getCost()+"원");
        textView4.setText(Integer.toString(1)+"개");
        //계산기능 설정
        total_price = Integer.parseInt(food.getCost());
        minus = findViewById(R.id.food_detailed_minus);
        plus = findViewById(R.id.food_detailed_plus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order_number > 1){
                    order_number -= 1;
                    total_price = order_number * Integer.parseInt(food.getCost());
                    textView3.setText(Integer.toString(total_price)+"원");
                    textView4.setText(Integer.toString(order_number)+"개");
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order_number += 1;
                total_price = order_number * Integer.parseInt(food.getCost());
                textView3.setText(Integer.toString(total_price)+"원");
                textView4.setText(Integer.toString(order_number)+"개");
            }
        });
        //주문내역 저장 기능 설정 저장하고 다시 액티비티로 이동한다.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Order>>() {}.getType();
                String data = sharedPreferences.getString("order_list","");
                if(!(data.equals(""))){
                    ArrayList<Order> order_list;
                    order_list = gson.fromJson(data,listType);
                    Order order = new Order();
                    order.setTruck(truck);
                    order.setFood_cost(food.getCost());
                    order.setFood_name(food.getName());
                    order.setFood_number(order_number);
                    order_list.add(order);
                    String json = gson.toJson(order_list,listType);
                    editor.putString("order_list",json);
                    editor.commit();
                    Toast.makeText(v.getContext(),"주문한 음식이 장바구니에 담겼어요",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Order order = new Order();
                    order.setTruck(truck);
                    order.setFood_cost(food.getCost());
                    order.setFood_name(food.getName());
                    order.setFood_number(order_number);
                    ArrayList<Order> order_list = new ArrayList<>();
                    order_list.add(order);
                    String json = gson.toJson(order_list,listType);
                    editor.putString("order_list",json);
                    editor.commit();
                    Toast.makeText(v.getContext(),"주문한 음식이 장바구니에 담겼어요",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}