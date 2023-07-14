package com.example.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;


public class NavigationActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private DrawerLayout drawerLayout;

    public NavigationActivity(Context context, DrawerLayout drawerLayout) {
        this.context = context;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();
        String title = menuItem.getTitle().toString();

        if (id == R.id.ingredient) {
            Intent intent = new Intent(context, SubActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.camera) {
            Intent intent = new Intent(context, CameraActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.recipe_recommend) {
            Intent intent = new Intent(context, RecipeActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.logout) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        return true;
    }
}
