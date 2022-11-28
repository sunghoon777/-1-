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

public class AddToBasketActivity extends AppCompatActivity {
    
    private ImageView foodImage;
    private TextView foodName;
    private TextView foodContent;
    private TextView foodCost;
    private TextView foodCount;
    private ImageView minusButton;
    private ImageView plusButton;
    //확인 버튼
    private Button commitButton;
    private Food food;
    private Truck truck;
    //음식의 개수
    private int orderNumber = 1;
    //총 가격
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_basket);
        Intent intent = getIntent();
        //전 액티비티에서 Food와 Truck 데이터를 가져옴
        food = (Food) intent.getSerializableExtra("Food");
        truck = (Truck) intent.getSerializableExtra("Truck");
        ActionBar ac = getSupportActionBar();
        ac.setTitle(food.getName());
        foodImage = findViewById(R.id.activity_add_to_basket_food_image);
        foodName = findViewById(R.id.activity_add_to_basket_food_name);
        foodContent = findViewById(R.id.activity_add_to_basket_food_content);
        foodCost = findViewById(R.id.activity_add_to_basket_food_cost);
        foodCount = findViewById(R.id.activity_add_to_basket_food_count);
        commitButton = findViewById(R.id.activity_add_to_basket_commit_button);
        //내용 설정
        Glide.with(this).load(food.getImage()).into(foodImage);
        foodName.setText(food.getName());
        foodContent.setText(food.getContent());
        foodCost.setText(food.getCost()+"원");
        foodCount.setText(Integer.toString(1)+"개");
        //계산기능 설정
        totalPrice = Integer.parseInt(food.getCost());
        minusButton = findViewById(R.id.activity_add_to_basket_minus_button);
        plusButton = findViewById(R.id.activity_add_to_basket_plus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderNumber > 1){
                    orderNumber -= 1;
                    totalPrice = orderNumber * Integer.parseInt(food.getCost());
                    foodCost.setText(Integer.toString(totalPrice)+"원");
                    foodCount.setText(Integer.toString(orderNumber)+"개");
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderNumber += 1;
                totalPrice = orderNumber * Integer.parseInt(food.getCost());
                foodCost.setText(Integer.toString(totalPrice)+"원");
                foodCount.setText(Integer.toString(orderNumber)+"개");
            }
        });
        //주문내역 저장 기능 설정 저장하고 다시 액티비티로 이동한다.
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Order>>() {}.getType();
                String data = sharedPreferences.getString("order_list","");
                //order_list에 데이터가 없으면 리스트를 생성해주고 리스트에 주문을 추가하고 있으면 그냥 리스트에 주문 추가
                if(!(data.equals(""))){
                    ArrayList<Order> order_list;
                    order_list = gson.fromJson(data,listType);
                    Order order = new Order();
                    order.setFood_cost(food.getCost());
                    order.setFood_name(food.getName());
                    order.setFood_number(orderNumber);
                    order_list.add(order);
                    String json = gson.toJson(order_list,listType);
                    editor.putString("order_list",json);
                    editor.commit();
                    Toast.makeText(v.getContext(),"주문한 음식이 장바구니에 담겼어요",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Order order = new Order();
                    order.setFood_cost(food.getCost());
                    order.setFood_name(food.getName());
                    order.setFood_number(orderNumber);
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