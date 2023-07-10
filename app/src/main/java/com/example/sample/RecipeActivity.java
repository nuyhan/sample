package com.example.sample;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Context;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;

import com.example.sample.SubActivity;
import com.example.sample.CameraActivity;



public class RecipeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home,settings,logout,notice,recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub3);

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
                    Intent intent = new Intent(RecipeActivity.this, SubActivity.class);
                    startActivity(intent);
                } else if (id == R.id.camera) {
                    // 'camera' 메뉴 아이템을 선택한 경우, CameraActivity로 이동
                    Intent intent = new Intent(RecipeActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else if (id == R.id.recipe_recommend) {
                    // 'recipe_recommend' 메뉴 아이템을 선택한 경우, RecipeRecommendActivity로 이동
                    Intent intent = new Intent(RecipeActivity.this,RecipeActivity.class);
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    public void openDrawer(View view) {
        mDrawerLayout.openDrawer(GravityCompat.START);
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