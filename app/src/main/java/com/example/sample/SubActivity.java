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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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

        // ValueEventListener for data persistence
        dataListener = new ValueEventListener() {
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
        };
        mDatabase.addValueEventListener(dataListener);

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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.ingredient) {
                    Intent intent = new Intent(SubActivity.this, SubActivity.class);
                    startActivity(intent);
                } else if (id == R.id.camera) {
                    Intent intent = new Intent(SubActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else if (id == R.id.recipe_recommend) {
                    Intent intent = new Intent(SubActivity.this, RecipeActivity.class);
                    startActivity(intent);
                } else if (id == R.id.logout) {
                    Intent intent = new Intent(SubActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });
    }

    private void loadAndDisplayData() {
        // Implement your data loading logic here
    }

    private void showAddItemDialog() {
        // Implement your dialog logic here
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
