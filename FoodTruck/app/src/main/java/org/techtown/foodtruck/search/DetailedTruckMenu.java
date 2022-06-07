package org.techtown.foodtruck.search;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

public class DetailedTruckMenu extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    TabLayout tabLayout;
    Truck_menu menu;
    Truck_review review;
    Bundle bundle;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_truck_menu);
        //전 액티비티에서 intent를 받아온다
        Intent intent = getIntent();
        Truck truck = (Truck) intent.getSerializableExtra("Truck");
        imageView = findViewById(R.id.detailed_truck_menu_imageview);
        textView = findViewById(R.id.detailed_truck_menu_textview);
        //대표 이미지 , 푸드트럭명 설정
        Glide.with(this).load(truck.getImage()).into(imageView);
        textView.setText(truck.getName());
        //탭레이아웃 설정
        tabLayout = findViewById(R.id.detailed_truck_menu_tab);
        tabLayout.addTab(tabLayout.newTab().setText("메뉴"));
        tabLayout.addTab(tabLayout.newTab().setText("리뷰"));
        //fragment 객체 생성
        menu = new Truck_menu();
        review = new Truck_review();
        //fragment에게 전달해줄 bundle 설정
        bundle = new Bundle();
        bundle.putSerializable("Truck",truck);
        menu.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.detailed_truck_menu_container,menu).commit();
        //tab 리스너 설정
        setTabListener();
        //타이틀 이름 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle(truck.getName());
        //floating 버튼 리스너 설정
        floatingActionButton = findViewById(R.id.fragment_truck_menu_floatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
                if(sharedPreferences.getString("order_list","empty").equals("empty")){
                    Toast.makeText(v.getContext(),"장바구니가 비었어요",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(v.getContext(),Basket.class);
                    startActivity(intent);
                }
            }
        });
    }

    //탭 리스너 설정 메소드
    private void setTabListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    menu.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detailed_truck_menu_container,menu).commit();
                }
                else if(position == 1){
                    review.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detailed_truck_menu_container,review).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}