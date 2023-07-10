package com.example.sample;

import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.login_button).setOnClickListener(onClickListener);
        TextView signupText = findViewById(R.id.signup_text);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 전환
                startActivity(new Intent(MainActivity.this, SignupActivity.class));

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_button:
                    login();
                    break;
            }
        }
    };

    private void login() {
        String email = ((EditText) findViewById(R.id.login_user_id)).getText().toString();
        EditText passwordEditText = findViewById(R.id.login_user_password);
        String password = passwordEditText.getText().toString();
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        // 로그인 성공 후, 원하는 화면으로 전환
                        startActivity(new Intent(MainActivity.this, SubActivity.class));

                    } else {
                        if (task.getException().toString() != null) {
                            Toast.makeText(MainActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}