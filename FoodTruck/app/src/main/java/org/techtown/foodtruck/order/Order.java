package org.techtown.foodtruck.order;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.techtown.foodtruck.DO.Order_history;
import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Order extends Fragment {

    TabLayout tabLayout;
    Order_history_fragment order_history_fragment;
    Ready ready;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_order, container, false);
        //프래그먼트 객체 생성
        order_history_fragment = new Order_history_fragment();
        ready = new Ready();
        Bundle bundle = getArguments();
        //탭레이아웃 설정
        tabLayout = rootView.findViewById(R.id.fragment_order_tab);
        tabLayout.addTab(tabLayout.newTab().setText("주문 내역"));
        tabLayout.addTab(tabLayout.newTab().setText("준비중"));
        setTabLayout();
        if(bundle != null){
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container,ready).commit();
            tab.select();
        }
        else{
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container,order_history_fragment).commit();
            tab.select();
        }
        return rootView;
    }

    private void setTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container,order_history_fragment).commit();
                }
                else if(position == 1){
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container,ready).commit();
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