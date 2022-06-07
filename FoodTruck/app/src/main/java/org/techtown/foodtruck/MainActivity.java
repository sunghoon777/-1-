package org.techtown.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import org.techtown.foodtruck.account.Account;
import org.techtown.foodtruck.featured.Featured;
import org.techtown.foodtruck.login.LoginActivity;
import org.techtown.foodtruck.map.Map;
import org.techtown.foodtruck.order.Order;
import org.techtown.foodtruck.search.Search;


public class MainActivity extends AppCompatActivity {

    Map map;
    Featured featured;
    Search search;
    Order order;
    Account account;
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //로그인 인증을 위한 객체 생성
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //fragment 객체 생성
        map = new Map();
        featured = new Featured();
        search = new Search();
        order = new Order();
        account = new Account();
        //main fragment 선택(홈, map fragment)
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,map).commit();
        //하단 탭 리스너 설정 메서드 호출
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentSelect(auth);
    }

    //하단탭 리스너 설정 메서드
    private void fragmentSelect(FirebaseAuth auth){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.map_tab){
                    fragmentManager.beginTransaction().replace(R.id.container,map).commit();
                    return true;
                }
                else if(id == R.id.featured_tab){
                    fragmentManager.beginTransaction().replace(R.id.container,featured).commit();
                    return true;
                }
                else if(id == R.id.search_tab){
                    fragmentManager.beginTransaction().replace(R.id.container,search).commit();
                    return true;
                }
                else if(id == R.id.order_tab){
                    if(auth.getCurrentUser() == null){
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        return false;
                    }
                    else{
                        fragmentManager.beginTransaction().replace(R.id.container,order).commit();
                        return true;
                    }
                }
                else if(id == R.id.account_tab){
                    if(auth.getCurrentUser() == null){
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        return false;
                    }
                    else{
                         fragmentManager.beginTransaction().replace(R.id.container,account).commit();
                        return true;
                    }
                }
                return false;
            }
        });
    }

}