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


import java.util.ArrayList;

public class SubActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> data;

    private DrawerLayout mDrawerLayout;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        listView = findViewById(R.id.list_view);

        // 데이터 생성
        data = new ArrayList<>();
        data.add("아이템 1");
        data.add("아이템 2");
        data.add("아이템 3");

        // 어댑터 생성
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);

        // 리스트뷰에 어댑터 설정
        listView.setAdapter(adapter);

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> showAddItemDialog()); // "+(추가)" 버튼 클릭 시 다이얼로그 표시

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.ingredient) {
                    // 'ingredient' 메뉴 아이템을 선택한 경우, IngredientActivity로 이동
                    Intent intent = new Intent(SubActivity.this, SubActivity.class);
                    startActivity(intent);
                } else if (id == R.id.camera) {
                    // 'camera' 메뉴 아이템을 선택한 경우, CameraActivity로 이동
                    Intent intent = new Intent(SubActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else if (id == R.id.recipe_recommend) {
                    // 'recipe_recommend' 메뉴 아이템을 선택한 경우, RecipeRecommendActivity로 이동
                    Intent intent = new Intent(SubActivity.this, RecipeActivity.class);
                    startActivity(intent);
                }

                return true;
            }
        });
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

    public void openDrawer(View view) {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void addItem(String newItem, String expiryDate, int quantity) {
        String itemDetails = newItem + " - 유통 기한: " + expiryDate + ", 수량: " + quantity;
        data.add(itemDetails); // data에 새로운 아이템 추가

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }




}
