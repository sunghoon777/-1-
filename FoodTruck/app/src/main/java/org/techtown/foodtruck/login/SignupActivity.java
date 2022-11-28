package org.techtown.foodtruck.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.foodtruck.R;
import org.techtown.foodtruck.DO.UserAccount;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {


    private FirebaseAuth mAuth; //파이어페이스 인증
    private DatabaseReference databaseReference; //파이어베이스 실시간 데이터베이스
    private EditText signupEmail;
    private Button commitButton;
    static final String TAG = "SIGN UP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //FirebaseAuth 인스턴스를 초기화
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodTruck");
        commitButton = findViewById(R.id.signup_commit_button);
        commitButton.setOnClickListener(signupClickListener);
        //사용자가 이메일 형식으로 적지 않으면 글자를 빨간색으로
        signupEmail = findViewById(R.id.signupEmail);
        signupEmail.addTextChangedListener(emailWatcher);
        ((EditText) findViewById(R.id.signupPhoneNumber)).addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    //signUp리스너
    View.OnClickListener signupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signUp();
        }
    };

    //이메일 입력 리스너
    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String email = signupEmail.getText().toString().trim();
            Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
            if(pattern.matcher(email).matches()&&email.length()>0){
                //패턴이 맞으면 변화 없음
                signupEmail.setTextColor(Color.parseColor("#000000"));
            }
            else{
                //패턴이 틀리면 글자 빨간색
                signupEmail.setTextColor(Color.parseColor("#FF0000"));
            }
        }
    };


    //회원가입 절차
    private void signUp(){
        String name = ((TextView)findViewById(R.id.signupName)).getText().toString();
        String email = ((TextView)findViewById(R.id.signupEmail)).getText().toString();
        String password =((TextView)findViewById(R.id.signupPassword)).getText().toString();
        String password_check =((TextView)findViewById(R.id.signupPassword_check)).getText().toString();
        String phoneNumber = ((TextView)findViewById(R.id.signupPhoneNumber)).getText().toString();
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        //이메일 형식이 맞는지
        //패스워드 이메일이 길이가 0인지 확인
        if(pattern.matcher(email).matches()){
            if(email.length() != 0 && password.length() != 0 && password_check.length() != 0 && phoneNumber.length() != 0){
                if(password.equals(password_check)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserAccount account = new UserAccount();
                                        account.setIdToken(user.getUid());
                                        account.setEmail(user.getEmail());
                                        account.setPassword(password);
                                        account.setName(name);
                                        account.setPhoneNumber(phoneNumber);
                                        //setValue insert임
                                        databaseReference.child("UserAccount").child(user.getUid()).setValue(account);
                                        Toast.makeText(SignupActivity.this, "회원가입완료", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                        //성공
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignupActivity.this, "이미 존재하는 회원입니다.", Toast.LENGTH_SHORT).show();
                                        //실패
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SignupActivity.this, "패스워드가 일치하지 않음", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(SignupActivity.this, "잘못입력하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(SignupActivity.this, "이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }
}