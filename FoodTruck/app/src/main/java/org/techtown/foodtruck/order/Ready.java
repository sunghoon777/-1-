package org.techtown.foodtruck.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.transferwise.sequencelayout.SequenceStep;

import org.techtown.foodtruck.DO.Location;
import org.techtown.foodtruck.DO.Order_history;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.MainActivity;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.DO.Order;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Ready extends Fragment {

    //얼마나 남았는지
    TextView textView1;
    //날짜
    TextView textView2;
    //주소
    TextView textView3;
    //주소복사
    TextView textView4;
    //트럭이믊
    TextView textView5;
    //총 금액
    TextView textView6;
    //주문목록
    TextView textView7;

    Order_history order_history;
    Truck truck;
    ViewGroup viewGroup;
    //파이어베이스 인증
    FirebaseAuth mAuth;
    FirebaseUser user;
    //주소 저장 변수
    String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        SharedPreferences sp = getActivity().getSharedPreferences(user.getUid(), getActivity().MODE_PRIVATE);
        Gson gson = new GsonBuilder().create();
        String data1 = sp.getString("Order_history","");
        String data2 = sp.getString("Truck","");
        if(data1 == null || data2 == null){
            rootView = (ViewGroup) inflater.inflate(R.layout.empty_layout, container, false);
            viewGroup = rootView;
        }
        else if(data1.equals("") || data2.equals("")){
            rootView = (ViewGroup) inflater.inflate(R.layout.empty_layout, container, false);
            viewGroup = rootView;
        }
        else{
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_ready, container, false);
            viewGroup = rootView;
            order_history = gson.fromJson(data1,Order_history.class);
            truck = gson.fromJson(data2,Truck.class);
            textView1 = rootView.findViewById(R.id.fragment_ready_durationTime);
            textView2 =  rootView.findViewById(R.id.fragment_ready_endTime);
            textView3 =  rootView.findViewById(R.id.fragment_ready_address);
            textView4 =  rootView.findViewById(R.id.fragment_ready_address_copy);
            textView5= rootView.findViewById(R.id.fragment_ready_truckName);
            textView6 = rootView.findViewById(R.id.fragment_ready_sum);
            textView7 = rootView.findViewById(R.id.fragment_ready_order_list);
            //몇 분남았는지 설정하기 위해 쓰레드 생성, 시작
            BackgroundThread backgroundThread = new BackgroundThread();
            backgroundThread.start();
            //예정시간을 알려주기 위한 로직, 예정시간은 주문한 시간 + wait_time을 더해준다.
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            //날짜 부분은 시간 분 초만 알려주기 때문에 앞부분은 잘라버린다.
            String str_date = order_history.getDate().substring(11);
            Date date = null;
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
                Log.d("da","adadad");
                String temporary = str_date.substring(2);
                int i = Integer.parseInt(str_date.substring(0,2));
                i = i-12;
                if(i>=22){
                    temporary = "오후 "+Integer.toString(i)+temporary+" 픽업 가능 예정";
                    textView2.setText(temporary);
                }
                else{
                    temporary = "오후 "+"0"+Integer.toString(i)+temporary+" 픽업 가능 예정";
                    textView2.setText(temporary);
                }
            }
            else{
                textView2.setText("오전 "+str_date+" 픽업 가능 예정");
            }
            //매장 주소 설정
            convertLocationToAddress();
            //주소 복사
            copyLocation();
            textView5.setText(truck.getName());
            int sum = 0;
            String order_list = "";
            for(Order order : order_history.getOrders()){
                sum += Integer.parseInt(order.getFood_cost()) * order.getFood_number();
                order_list += order.getFood_name()+" x"+Integer.toString(order.getFood_number())+"   ";
            }
            textView6.setText(Integer.toString(sum)+"원");
            textView7.setText(order_list);

        }
        return rootView;
    }

    //몇 분 남았는지 설정하기 위한 쓰레드
    public class BackgroundThread extends Thread  {

        Handler handler = new Handler();

        @Override
        public void run() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (true){
                Date date1 = new Date();
                Date date2 = null;
                try {
                    date2 = dateFormat.parse(order_history.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = date2.getTime() - date1.getTime();
                int diffMinutes = (int) (diff / (60*1000));
                diffMinutes += Integer.parseInt(truck.getWait_time());
                int finalDiffMinutes = diffMinutes;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(finalDiffMinutes == 0){
                            textView1.setText("0");
                            ((SequenceStep)viewGroup.findViewById(R.id.fragment_ready_step_1)).setActive(false);
                            ((SequenceStep)viewGroup.findViewById(R.id.fragment_ready_step_2)).setActive(true);
                        }
                        else if(finalDiffMinutes < 0){
                            textView1.setText("0");
                        }
                        else{
                            textView1.setText(Integer.toString(finalDiffMinutes));
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
            textView1.setText("0");
            try {
                Thread.sleep(600000);
            }catch (Exception e){}

        }

    }

    //주소 변환 메소드
    private void convertLocationToAddress(){
        DecimalFormat f = new DecimalFormat("#.##");
        Location location = truck.getLocation();
        List<Address> list= null;
        Geocoder g = new Geocoder(getContext());
        try{
            list = g.getFromLocation(Double.parseDouble(location.getLatitude()),Double.parseDouble(location.getLongitude()),10);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"실패",Toast.LENGTH_SHORT).show();
        }
        address = list.get(0).getAddressLine(0).substring(5);
        String distance = f.format(truck.getDistance()/1000);
        textView3.setText(address+" (트럭위치로부터 "+distance+"km)");
    }
    
    //주소 복사 메소드
    private void copyLocation(){
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(),"주소 복사됨",Toast.LENGTH_SHORT).show();
            }
        });
    }

}