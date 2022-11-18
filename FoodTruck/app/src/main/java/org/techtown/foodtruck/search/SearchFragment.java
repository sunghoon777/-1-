package org.techtown.foodtruck.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.R;

public class SearchFragment extends Fragment {

    //어뎁터 변수
    private CategoryImageAdapter categoryImageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        //리싸이클뷰 설정
        setRecycleView(rootView, container);
        return rootView;
    }

    private void  setRecycleView(ViewGroup rootView, ViewGroup container){
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_search_recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(container.getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        categoryImageAdapter = new CategoryImageAdapter();
        Image image1= new Image(container.getResources().getDrawable(R.drawable.most_popular), "인기 푸드트럭");
        Image image2= new Image(container.getResources().getDrawable(R.drawable.open_now), "신규 푸드트럭");
        Image image3 = new Image(container.getResources().getDrawable(R.drawable.hamburger), "햄버거");
        Image image4 = new Image(container.getResources().getDrawable(R.drawable.dessert), "디저트");
        Image image5= new Image(container.getResources().getDrawable(R.drawable.chicken_skewer), "닭꼬치");
        Image image6= new Image(container.getResources().getDrawable(R.drawable.stake), "스테이크");
        Image image7= new Image(container.getResources().getDrawable(R.drawable.korean), "분식");
        Image image8= new Image(container.getResources().getDrawable(R.drawable.japanese), "일식");
        Image image9= new Image(container.getResources().getDrawable(R.drawable.pizza), "피자");
        Image image10= new Image(container.getResources().getDrawable(R.drawable.chicken), "치킨");
        categoryImageAdapter.addItem(image1);
        categoryImageAdapter.addItem(image2);
        categoryImageAdapter.addItem(image3);
        categoryImageAdapter.addItem(image4);
        categoryImageAdapter.addItem(image5);
        categoryImageAdapter.addItem(image6);
        categoryImageAdapter.addItem(image7);
        categoryImageAdapter.addItem(image8);
        categoryImageAdapter.addItem(image9);
        categoryImageAdapter.addItem(image10);
        recyclerView.setAdapter(categoryImageAdapter);
        //아이템 리스너 설정
        categoryImageAdapter.setOnItemClickListener(new onCategoryItemClickListener() {
            @Override
            public void onItemClick(CategoryImageAdapter.ViewHolder holder, View view, int position) {
                Image image = categoryImageAdapter.getItem(position);
                String content = image.getContent();
                Intent intent = new Intent(view.getContext(), TruckListActivity.class);
                intent.putExtra("content",content);
                startActivity(intent);
            }
        });
        TextView retrieveBox = rootView.findViewById(R.id.fragment_search_retrieve_button);
        //검색버튼 리스너 설정
        retrieveBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RetrieveActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
}