package org.techtown.foodtruck.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.transferwise.sequencelayout.SequenceStep;

import org.techtown.foodtruck.DO.Location;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.OrderHistory;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderStateActivity extends AppCompatActivity {

    //얼마나 남았는지
    TextView durationTime;
    //시작, 준비완료 날짜
    TextView orderDate;
    //주소
    TextView address;
    //주소복사
    TextView addressCopyButton;
    //트럭이믊
    TextView truckName;
    //총 금액
    TextView totalCost;
    //주문목록
    TextView orderList;
    //현재 트럭
    Truck truck;
    //현재 OrderHistory
    OrderHistory orderHistory;
    //현재 유저
    FirebaseUser user;
    //주소 저장 변수
    String addressValue;
    //주문 기록 데이터
    DatabaseReference orderHistoryDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_state);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //전 액티비티에서 intent를 받아온다
        Intent intent = getIntent();
        truck = (Truck) intent.getSerializableExtra("Truck");
        orderHistory = (OrderHistory) intent.getSerializableExtra("OrderHistory");
        user = FirebaseAuth.getInstance().getCurrentUser();
        durationTime = findViewById(R.id.activity_order_state_duration_time);
        orderDate = findViewById(R.id.activity_order_state_order_date);
        address = findViewById(R.id.activity_order_state_address);
        addressCopyButton = findViewById(R.id.activity_order_state_copy_button);
        truckName = findViewById(R.id.activity_order_state_truck_name);
        totalCost = findViewById(R.id.activity_order_state_total_cost);
        orderList = findViewById(R.id.activity_order_state_order_list);
        setViewContent();
        //주문 기록 데이터 가져오기
        orderHistoryDatabase = FirebaseDatabase.getInstance().getReference("FoodTruck").child("OrderHistory").child(orderHistory.getOrderHistoryId());
        orderHistoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrderHistory orderHistory = snapshot.getValue(OrderHistory.class);
                String state = orderHistory.getOrderState();
                if(state.equals("접수")){
                    startProcessOrder();
                }
                else if(state.equals("취소")){
                    durationTime.setText("주문이 취소됨");
                    orderDate.setText("");
                    ((SequenceStep)findViewById(R.id.activity_order_state_step_0)).setTitle("주문이 취소됨");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //뷰 내용을 설정한다.
    private void setViewContent(){
        //매장 주소 설정
        convertLocationToAddress();
        //주소 복사
        copyLocation();
        truckName.setText(truck.getName());
        int sum = 0;
        String order_list = "";
        for(Order order : orderHistory.getOrders()){
            sum += Integer.parseInt(order.getFood_cost()) * order.getFood_number();
            order_list += order.getFood_name()+" x"+Integer.toString(order.getFood_number())+"   ";
        }
        totalCost.setText(Integer.toString(sum)+"원");
        orderList.setText(order_list);
        durationTime.setText("주문 수락을 대기 중");
        orderDate.setText("");
    }

    //주소 변환 메소드
    private void convertLocationToAddress(){
        DecimalFormat f = new DecimalFormat("#.##");
        Location location = truck.getLocation();
        List<Address> list= null;
        Geocoder g = new Geocoder(this);
        try{
            list = g.getFromLocation(Double.parseDouble(location.getLatitude()),Double.parseDouble(location.getLongitude()),10);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
        }
        addressValue = list.get(0).getAddressLine(0).substring(5);
        String distance = f.format(truck.getDistance()/1000);
        address.setText(addressValue+" (트럭위치로부터 "+distance+"km)");
    }

    //주소 복사 메소드
    private void copyLocation(){
        addressCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", addressValue);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(),"주소 복사됨",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startProcessOrder(){
        ((SequenceStep)findViewById(R.id.activity_order_state_step_0)).setActive(false);
        ((SequenceStep)findViewById(R.id.activity_order_state_step_1)).setActive(true);
        //몇 분남았는지 설정하기 위해 쓰레드 생성, 시작
        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        //예정시간을 알려주기 위한 로직, 예정시간은 주문한 시간 + wait_time을 더해준다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        //날짜 부분은 시간 분 초만 알려주기 때문에 앞부분은 잘라버린다.
        String str_date = orderHistory.getDate().substring(11);
        Date date = new Date();
        try {
            date = dateFormat.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //예정시간을 계산 에정시간 = 주문시간 + 대기시간
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,Integer.parseInt(truck.getWait_time()));
        //오전 오후를 표시하기 위한 로직
        str_date = dateFormat.format(cal.getTime());
        if(Integer.parseInt(str_date.substring(0,2))>12){
            String temporary = str_date.substring(2);
            int i = Integer.parseInt(str_date.substring(0,2));
            i = i-12;
            if(i>=22){
                temporary = "오후 "+Integer.toString(i)+temporary+" 픽업 가능 예정";
                orderDate.setText(temporary);
            }
            else{
                temporary = "오후 "+"0"+Integer.toString(i)+temporary+" 픽업 가능 예정";
                orderDate.setText(temporary);
            }
        }
        else{
            orderDate.setText("오전 "+str_date+" 픽업 가능 예정");
        }
    }


    //몇 분 남았는지 설정하기 위한 쓰레드
    public class BackgroundThread extends Thread  {

        Handler handler = new Handler();

        @Override
        public void run() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (true){
                //대기 시간을 계속 갱신해준다.
                Date date1 = new Date();
                Date date2 = null;
                try {
                    date2 = dateFormat.parse(orderHistory.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = date2.getTime() - date1.getTime();
                int diffMinutes = (int) (diff / (60*1000));
                diffMinutes += Integer.parseInt(truck.getWait_time());
                int finalDiffMinutes = diffMinutes;
                //만약 대기시간이 끝났으면 픽업가능 상태로 갱신해준다.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(finalDiffMinutes <= 0){
                            durationTime.setText("0분");
                            //메뉴 준비중 -> 픽업가능
                            ((SequenceStep)findViewById(R.id.activity_order_state_step_1)).setActive(false);
                            ((SequenceStep)findViewById(R.id.activity_order_state_step_2)).setActive(true);
                        }
                        else{
                            durationTime.setText(Integer.toString(finalDiffMinutes)+"분");
                        }
                    }
                });
                if(finalDiffMinutes <= 0){
                    break;
                }
                try {
                    Thread.sleep(10000);
                }catch (Exception e){}
            }
            durationTime.setText("0분");
            try {
                Thread.sleep(600000);
            }catch (Exception e){}

        }

    }


}