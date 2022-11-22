package com.stepbystep.bossapp.account;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.stepbystep.bossapp.DO.Festival;
import com.stepbystep.bossapp.R;

import java.util.Calendar;

public class FestivalContentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button btn_push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_content);
        btn_push = findViewById(R.id.noti_set);

        btn_push.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        Intent intent = getIntent();
        Festival festival = (Festival) intent.getSerializableExtra("Festival");
        ((TextView) findViewById(R.id.activity_festival_content_title2)).setText(festival.getTitle().replace("\\n", System.getProperty("line.separator")));
        ((TextView) findViewById(R.id.activity_festival_content_content)).setText(festival.getContent().replace("\\n", System.getProperty("line.separator")));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        // 알림 받을 날짜 선택 후 set - 그 날 아침 9시에 알림 가도록 임시 설정
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 9, 0, 0);

        startAlarm(c);
    }
    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (c.before(Calendar.getInstance())) {
            Toast.makeText(this, "알람시간이 현재시간보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}
