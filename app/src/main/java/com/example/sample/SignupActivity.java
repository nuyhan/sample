package com.example.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // 회원가입 버튼 클릭 시
        Button signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        EditText emailEditText = findViewById(R.id.signup_email);
        EditText passwordEditText = findViewById(R.id.signup_password);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignupActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase 인증을 사용하여 사용자 등록
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                            // 회원가입 성공 후, 로그인 화면으로 전환
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish(); // 현재 회원가입 화면을 종료
                        } else {
                            Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
