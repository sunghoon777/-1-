package org.techtown.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import org.techtown.foodtruck.DO.Order_history;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.Favorite.Favorite_fragment;
import org.techtown.foodtruck.account.Account;
import org.techtown.foodtruck.login.LoginActivity;
import org.techtown.foodtruck.map.Map;
import org.techtown.foodtruck.order.Order;
import org.techtown.foodtruck.search.Search;


public class MainActivity extends AppCompatActivity {

    Map map;
    Favorite_fragment favorite_fragment;
    Search search;
    Order order;
    Account account;
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    //뒤로가기 로직을 위한 변수
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //로그인 인증을 위한 객체 생성
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //intent 받기
        Intent intent = getIntent();
        int flag = intent.getIntExtra("startFragment",0);
        //fragment 객체 생성
        map = new Map();
        favorite_fragment = new Favorite_fragment();
        search = new Search();
        order = new Order();
        account = new Account();
        //main fragment 선택(홈, map fragment)
        fragmentManager = getSupportFragmentManager();
        //하단 탭 리스너 설정 메서드 호출
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentSelect(auth);
        if(flag == 0){
            bottomNavigationView.setSelectedItemId(R.id.map_tab);
        }
        else if(flag == 1){
            bottomNavigationView.setSelectedItemId(R.id.favorite_tab);
        }
        else if(flag == 2){
            bottomNavigationView.setSelectedItemId(R.id.search_tab);
        }
        else if(flag == 3){
            Bundle bundle = new Bundle();
            bundle.putInt("flag",1);
            order.setArguments(bundle);
            bottomNavigationView.setSelectedItemId(R.id.order_tab);
        }
        else if(flag == 4){
            bottomNavigationView.setSelectedItemId(R.id.account_tab);
        }
    }

    //뒤로가기 2번하면 앱종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(this, "한번 더 누르면 앱이 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }
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
                else if(id == R.id.search_tab){
                    fragmentManager.beginTransaction().replace(R.id.container,search).commit();
                    return true;
                }
                else if(id == R.id.favorite_tab){
                    if(auth.getCurrentUser() == null){
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        return false;
                    }
                    else{
                        fragmentManager.beginTransaction().replace(R.id.container,favorite_fragment).commit();
                        return true;
                    }
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