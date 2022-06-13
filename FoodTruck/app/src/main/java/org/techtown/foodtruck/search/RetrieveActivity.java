package org.techtown.foodtruck.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    DatabaseReference databaseReference;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        autoCompleteTextView = findViewById(R.id.retrieve_searchView);
        autoCompleteTextView.requestFocus();
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodTruck").child("Truck");
        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(event);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_ENTER)){
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                    Intent intent = new Intent(RetrieveActivity.this,RetrieveResult.class);
                    String content = autoCompleteTextView.getText().toString();
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
            ArrayAdapter adapter = new ArrayAdapter(this,R.layout.retrieve_row,R.id.retrieve_row_text,list);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setDropDownBackgroundResource(R.color.transparen);
            autoCompleteTextView.setDropDownVerticalOffset(100);
        }
        else{

        }
    }
}