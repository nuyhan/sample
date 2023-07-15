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
import android.view.ViewGroup;
import androidx.annotation.Nullable;

import android.app.NotificationManager;
import android.os.Build;
import android.app.NotificationChannel;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;


public class SubActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ValueEventListener dataListener;

    private ListView listView;
    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    private String clickedItemKey;

    private boolean[] itemClicked;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private NotificationActivity notificationActivity; // NotificationActivity 인스턴스 추가

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationActivity navigationActivity = new NavigationActivity(this, mDrawerLayout);
        navigationView.setNavigationItemSelectedListener(navigationActivity);

        notificationActivity = new NotificationActivity(this); // NotificationActivity 인스턴스 초기화
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
                    data.clear();
                    for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                        ProductItem item = dataSnapshotItem.getValue(ProductItem.class);
                        if (item != null) {
                            String itemName = item.getName();
                            String expiryDate = item.getExpiryDate();
                            int quantity = item.getQuantity();
                            String key = dataSnapshotItem.getKey();
                            addItem(itemName, expiryDate, quantity, key);
                            checkExpiration(item); // 유통기한 체크 메소드 호출
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

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (itemClicked[position]) {
                    view.setBackgroundColor(getResources().getColor(R.color.clicked_item_color));
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        if (clickedItemKey != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("상품 삭제")
                    .setMessage("선택한 상품을 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> deleteSelectedItem())
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            Toast.makeText(SubActivity.this, "삭제할 상품을 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelectedItem() {
        if (clickedItemKey != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userUid = currentUser.getUid();
                String userPath = "users/" + userUid + "/productItems/" + clickedItemKey;

                DatabaseReference userRef = mDatabase.child(userPath);
                userRef.removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(SubActivity.this, "상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(SubActivity.this, "상품 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show());
            }
        } else {
            Toast.makeText(SubActivity.this, "삭제할 상품을 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFirebase(ProductItem productItem) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            String userPath = "users/" + userUid + "/productItems";

            DatabaseReference userRef = mDatabase.child(userPath);
            String key = userRef.push().getKey();
            if (key != null) {
                userRef.child(key).setValue(productItem)
                        .addOnSuccessListener(aVoid -> Toast.makeText(SubActivity.this, "상품이 추가되었습니다.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(SubActivity.this, "상품 추가에 실패했습니다.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView)
                .setTitle("상품 추가")
                .setPositiveButton("추가", (dialog, which) -> {
                    EditText editItemName = dialogView.findViewById(R.id.edit_item_name);
                    String itemName = editItemName.getText().toString();

                    DatePicker datePicker = dialogView.findViewById(R.id.datepicker_expiry);
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int day = datePicker.getDayOfMonth();
                    String expiryDate = String.format("%04d-%02d-%02d", year, month + 1, day);

                    TextView textQuantity = dialogView.findViewById(R.id.text_quantity);
                    int quantity = Integer.parseInt(textQuantity.getText().toString());

                    saveToFirebase(new ProductItem(itemName, expiryDate, quantity));
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonIncrease = dialogView.findViewById(R.id.button_increase);
        buttonIncrease.setOnClickListener(v -> {
            TextView textQuantity = dialogView.findViewById(R.id.text_quantity);
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            quantity++;
            textQuantity.setText(String.valueOf(quantity));
        });

        Button buttonDecrease = dialogView.findViewById(R.id.button_decrease);
        buttonDecrease.setOnClickListener(v -> {
            TextView textQuantity = dialogView.findViewById(R.id.text_quantity);
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                textQuantity.setText(String.valueOf(quantity));
            }
        });
    }

    private void addItem(String newItem, String expiryDate, int quantity, String key) {
        String itemDetails = newItem + " - 유통 기한: " + expiryDate + ", 수량: " + quantity;
        data.add(itemDetails);

        itemClicked = new boolean[data.size()];
        Arrays.fill(itemClicked, false);

        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (clickedItemKey != null && clickedItemKey.equals(key)) {
                clickedItemKey = null;
                itemClicked[position] = false;
            } else {
                clickedItemKey = key;
                itemClicked[position] = true;
            }
            adapter.notifyDataSetChanged();
        });
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

    private void checkExpiration(ProductItem item) {
        try {
            Date expiryDate = dateFormat.parse(item.getExpiryDate());
            long timeDifference = expiryDate.getTime() - System.currentTimeMillis();

            if (timeDifference <= 0) { // 유통기한이 지났을 경우
                // 유통기한이 지난 경우에 대한 처리 코드 추가
            } else if (timeDifference <= 86400000 && timeDifference > 0) { // 24시간(86400000밀리초) 이내에 유통기한이 끝나는 상품 체크
                ArrayList<ProductItem> itemList = new ArrayList<>();
                itemList.add(item);
                notificationActivity.sendNotification(itemList, "1일 전"); // NotificationActivity의 sendNotification 메서드 호출
            } else if (timeDifference <= 259200000 && timeDifference > 0) { // 3일(72시간) 이내에 유통기한이 끝나는 상품 체크
                ArrayList<ProductItem> itemList = new ArrayList<>();
                itemList.add(item);
                notificationActivity.sendNotification(itemList, "3일 전"); // NotificationActivity의 sendNotification 메서드 호출
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
