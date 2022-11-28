package org.techtown.foodtruck.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foodtruck.R;
import java.util.ArrayList;

public class RetrieveActivity extends AppCompatActivity {

    //데이터 참조 변수
    private DatabaseReference databaseReference;
    //자동완성 텍스트뷰
    private AutoCompleteTextView retrieveButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        retrieveButton = findViewById(R.id.activity_retrieve_retrieve_button);
        //사용자가 검색 버튼을 클릭안하더라도 포커스가 되게 만들어줌
        retrieveButton .requestFocus();
        //데이터 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //키보드가 안나타있을 때 키보드가 보이게 해준다.
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        //검색 버튼 리스너 설정
        retrieveButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_ENTER)){
                    //만약 사용자가 엔터를 누르면 키보드를 숨기고 검색결과로 이동
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                    Intent intent = new Intent(RetrieveActivity.this, RetrieveResultActivity.class);
                    String content = retrieveButton.getText().toString();
                    intent.putExtra("content",content);
                    startActivity(intent);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }

    //사용자가 검색을 하면 아래 연관언어를 표시해준다.
    private void  populateSearch(DataSnapshot dataSnapshot){
        ArrayList<String> list = new ArrayList<>();
        if(dataSnapshot.exists()){
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                String name = ds.child("name").getValue(String.class);
                String type = ds.child("type").getValue(String.class);
                if(!(list.contains(name))){
                    list.add(name);
                }
                if(!(list.contains(type))){
                    list.add(type);
                }
            }
            ArrayAdapter adapter = new ArrayAdapter(this,R.layout.recyclerview_retrieve_associative_search_term_item,R.id.recyclerview_retrieve_associative_search_term_item_button,list);
            retrieveButton.setAdapter(adapter);
            retrieveButton.setDropDownBackgroundResource(R.color.transparent);
            retrieveButton.setDropDownVerticalOffset(100);
        }
        else{

        }
    }
}