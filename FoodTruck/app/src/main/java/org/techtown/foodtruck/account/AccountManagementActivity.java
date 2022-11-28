package org.techtown.foodtruck.account;


import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.R;
import java.util.HashMap;

public class AccountManagementActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //파이어페이스 인증
    private DatabaseReference databaseReference; //파이어베이스 실시간 데이터베이스
    private TextView account_manager_email;
    private EditText account_manager_name;
    private EditText account_manager_password;
    private EditText account_manager_phoneNumber;
    private Button account_change_button;
    private UserAccount userAccount;
    private static final String TAG = "CHANGE ACCOUNT_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //계정정보 표시 로직
        Intent intent = getIntent();
        userAccount = (UserAccount) intent.getSerializableExtra("UserAccount");
        account_manager_email = findViewById(R.id.account_manager_email);
        account_manager_name = findViewById(R.id.account_manager_name);
        account_manager_password = findViewById(R.id.account_manager_password);
        account_manager_phoneNumber = findViewById(R.id.account_manager_phoneNumber);
        account_manager_phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        account_manager_email.setText(userAccount.getEmail());
        account_manager_name.setText(userAccount.getName());
        account_manager_password.setText(userAccount.getPassword());
        account_manager_phoneNumber.setText(userAccount.getPhoneNumber());
        account_change_button = findViewById(R.id.account_change_button);
        account_change_button.setOnClickListener(listener);
        //FirebaseAuth 인스턴스를 초기화,
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodTruck");
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(userAccount.getName().equals(account_manager_name.getText().toString())
                    && userAccount.getPassword().equals(account_change_button.getText().toString())){
                finish();
            }
            else{
                FirebaseUser user = mAuth.getCurrentUser();
                String newPassword = account_manager_password.getText().toString();
                String newName = account_manager_name.getText().toString();
                String newPhoneNumber = account_manager_phoneNumber.getText().toString();
                //인증 비밀번호 업데이트
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User password updated.");
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //데이터베이스 업데이트
                HashMap hashMap = new HashMap();
                hashMap.put("password",newPassword);
                hashMap.put("name",newName);
                hashMap.put("phoneNumber",newPhoneNumber);
                databaseReference.child("UserAccount").child(user.getUid()).updateChildren(hashMap);
                finish();
            }
        }
    };
}