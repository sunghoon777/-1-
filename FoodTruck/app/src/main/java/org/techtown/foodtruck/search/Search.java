package org.techtown.foodtruck.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.R;

public class Search extends Fragment {

    SearchImageAdapter searchImageApdater;

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
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(container.getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        searchImageApdater = new SearchImageAdapter();
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
        searchImageApdater.addItem(image1);
        searchImageApdater.addItem(image2);
        searchImageApdater.addItem(image3);
        searchImageApdater.addItem(image4);
        searchImageApdater.addItem(image5);
        searchImageApdater.addItem(image6);
        searchImageApdater.addItem(image7);
        searchImageApdater.addItem(image8);
        searchImageApdater.addItem(image9);
        searchImageApdater.addItem(image10);
        recyclerView.setAdapter(searchImageApdater);
        searchImageApdater.setOnItemClickListener(new onSearchItemClickListener() {
            @Override
            public void onItemClick(SearchImageAdapter.ViewHolder holder, View view, int position) {
                Image image = searchImageApdater.getItem(position);
                String content = image.getContent();
                Intent intent = new Intent(view.getContext(), TruckListActivity.class);
                intent.putExtra("content",content);
                startActivity(intent);
            }
        });
    }
}