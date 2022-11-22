package com.stepbystep.bossapp;

import org.junit.Test;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepbystep.bossapp.DO.Order_history;
import com.stepbystep.bossapp.chart.StringtoDate;

import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Order_history> order_histories;
    @Test
    public void main() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyddMMHHmmss");
        LocalDateTime date = StringtoDate.changetodata("2022-08-04 09:48:49");
        System.out.println(date);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference =  firebaseDatabase.getReference("FoodTruck");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order_histories.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {  //반복문으로 리스트를 출력함
                    Order_history order_history = snapshot1.getValue(Order_history.class); // 객체에 데이터를 담는다
                    order_histories.add(order_history);
                    System.out.println(order_history);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error
                Log.e("Calculatesales", String.valueOf(error.toException()));
            }
        });

        for (int i = 0; i < order_histories.size(); i++) {
            String stringdate = order_histories.get(i).getDate();
            date = StringtoDate.changetodata(stringdate);
            order_histories.get(i).setDdate(date);
            System.out.println(order_histories);

        }
    }
    
}