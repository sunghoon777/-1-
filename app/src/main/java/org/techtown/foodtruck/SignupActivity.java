package org.techtown.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {


    private FirebaseAuth mAuth; //파이어페이스 인증
    private DatabaseReference databaseReference; //파이어베이스 실시간 데이터베이스

    Button button;
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

        button = findViewById(R.id.signup_commit_button);
        button.setOnClickListener(signupClickListener);
    }

    View.OnClickListener signupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signUp();
        }
    };

    //회원가입 절차
    private void signUp(){
        String name = ((TextView)findViewById(R.id.signupName)).getText().toString();
        String email = ((TextView)findViewById(R.id.signupEmail)).getText().toString();
        String password =((TextView)findViewById(R.id.signupPassword)).getText().toString();
        String password_check =((TextView)findViewById(R.id.signupPassword_check)).getText().toString();
        String phoneNumber = ((TextView)findViewById(R.id.signupPhone_number)).getText().toString();
        if(email.length() != 0 && password.length() != 0 && password_check.length() != 0){
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
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
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
}