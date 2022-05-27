package org.techtown.foodtruck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth; //파이어페이스 인증
    private DatabaseReference databaseReference; //파이어베이스 실시간 데이터베이스
    static final String TAG = "SIGN IN";
    Button loginButton;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //액션바 지우기
        ActionBar abar = getSupportActionBar();
        abar.hide();
        //FirebaseAuth 인스턴스를 초기화,
        mAuth = FirebaseAuth.getInstance();
        //로그인 회원가입 버튼 이벤트 설정
        loginButton = findViewById(R.id.loginbutton);
        signupButton = findViewById(R.id.signupbutton);
        loginButton.setOnClickListener(loginClickListener);
        signupButton.setOnClickListener(signupClickListener);
    }

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //데이터 받아오기
            signIn();
        }
    };

    View.OnClickListener signupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
            startActivity(intent);
        }
    };

    private void signIn(){
        String email = ((TextView)findViewById(R.id.editTextEmail)).getText().toString();
        String password =((TextView)findViewById(R.id.editTextPassword)).getText().toString();
        //로그인 요청
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //로그인 확인
                            SharedPreferences auth = getSharedPreferences("for AUTH", MODE_PRIVATE);
                            SharedPreferences.Editor editor = auth.edit();
                            editor.putString("isLogin?","yes");
                            Toast.makeText(LoginActivity.this, "로그인하셨습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "잘못입력하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}