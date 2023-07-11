package com.example.sample;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // ...

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
            Toast.makeText(SignupActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 이미 있는 아이디인지 확인
        Query query = databaseRef.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 이미 있는 아이디인 경우
                    Toast.makeText(SignupActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                } else {
                    // 새로운 아이디인 경우 Firebase 인증을 사용하여 사용자 등록
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_LONG).show();
                                        // 회원가입 성공 후, 로그인 화면으로 전환
                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        finish(); // 현재 회원가입 화면을 종료
                                    } else {
                                        Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 쿼리 취소 또는 실패 시 처리할 작업
            }
        });
    }
}
