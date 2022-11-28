package org.techtown.foodtruck.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import org.techtown.foodtruck.R;

public class OrderFragment extends Fragment {

    private TabLayout tabLayout;
    private OrderHistoryFragment orderHistoryFragment;
    private OrderReadyListFragment orderReadyListFragment;


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
        orderHistoryFragment = new OrderHistoryFragment();
        orderReadyListFragment = new OrderReadyListFragment();
        Bundle bundle = getArguments();
        //탭레이아웃 설정
        tabLayout = rootView.findViewById(R.id.fragment_order_tab);
        tabLayout.addTab(tabLayout.newTab().setText("주문 내역"));
        tabLayout.addTab(tabLayout.newTab().setText("준비중"));
        setTabLayout();
        //번들 안에 내용이 있으면 readyFragment 담고 아니면 order_history_fragment 담는다.
        if(bundle != null){
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container, orderReadyListFragment).commit();
            tab.select();
        }
        else{
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container, orderHistoryFragment).commit();
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
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container, orderHistoryFragment).commit();
                }
                else if(position == 1){
                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_order_container, orderReadyListFragment).commit();
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