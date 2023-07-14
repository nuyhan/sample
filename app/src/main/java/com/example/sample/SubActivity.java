package com.example.sample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Context;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import android.widget.Toast;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.sample.NavigationActivity;
import com.example.sample.ProductItem;

import java.util.ArrayList;


public class SubActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ValueEventListener dataListener;

    private ListView listView;
    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;
    private DrawerLayout mDrawerLayout;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("productItems");

        listView = findViewById(R.id.list_view);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);


        // 데이터 로드 및 표시
        loadAndDisplayData();

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> showAddItemDialog());

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationActivity navigationActivity = new NavigationActivity(this, mDrawerLayout);
        navigationView.setNavigationItemSelectedListener(navigationActivity);

    }

    private void loadAndDisplayData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            String userPath = "users/" + userUid + "/productItems";

            DatabaseReference userRef = mDatabase.child(userPath);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    data.clear(); // Clear previous data
                    for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                        ProductItem item = dataSnapshotItem.getValue(ProductItem.class);
                        if (item != null) {
                            String itemName = item.getName();
                            String expiryDate = item.getExpiryDate();
                            int quantity = item.getQuantity();

                            addItem(itemName, expiryDate, quantity);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SubActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void saveToFirebase(ProductItem productItem) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            String userPath = "users/" + userUid + "/productItems"; // 사용자별 경로 생성

            DatabaseReference userRef = mDatabase.child(userPath);
            String key = userRef.push().getKey();
            if (key != null) {
                userRef.child(key).setValue(productItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SubActivity.this, "상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubActivity.this, "상품 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView)
                .setTitle("상품 추가")
                .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editItemName = dialogView.findViewById(R.id.edit_item_name);
                        String itemName = editItemName.getText().toString();

                        DatePicker datePicker = dialogView.findViewById(R.id.datepicker_expiry);
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        String expiryDate = String.format("%04d-%02d-%02d", year, month + 1, day); // 유통 기한을 YYYY-MM-DD 형식으로 변환하여 저장

                        TextView textQuantity = dialogView.findViewById(R.id.text_quantity);
                        int quantity = Integer.parseInt(textQuantity.getText().toString()); // 수량을 가져옴


                        // Firebase Realtime Database에 생성된 ProductItem을 저장합니다.
                        saveToFirebase(new ProductItem(itemName, expiryDate, quantity));


                        addItem(itemName, expiryDate, quantity); // 아이템 추가 메서드 호출
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // 수량 증가 버튼 클릭 이벤트 처리
        Button buttonIncrease = dialogView.findViewById(R.id.button_increase);
        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textQuantity = dialogView.findViewById(R.id.text_quantity);
                int quantity = Integer.parseInt(textQuantity.getText().toString());
                quantity++;
                textQuantity.setText(String.valueOf(quantity));
            }
        });

        // 수량 감소 버튼 클릭 이벤트 처리
        Button buttonDecrease = dialogView.findViewById(R.id.button_decrease);
        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textQuantity = dialogView.findViewById(R.id.text_quantity);
                int quantity = Integer.parseInt(textQuantity.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    textQuantity.setText(String.valueOf(quantity));
                }
            }
        });
    }


    private void addItem(String newItem, String expiryDate, int quantity) {
        String itemDetails = newItem + " - 유통 기한: " + expiryDate + ", 수량: " + quantity;
        data.add(itemDetails);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataListener != null) {
            mDatabase.removeEventListener(dataListener);
        }
    }

    public void openDrawer(View view) {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
