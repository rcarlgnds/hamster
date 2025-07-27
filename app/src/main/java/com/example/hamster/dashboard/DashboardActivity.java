package com.example.hamster.dashboard;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hamster.R;
import com.example.hamster.data.model.User;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_inventory) {
                Toast.makeText(DashboardActivity.this, "Menu Inventory diklik", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_activation) {
                Toast.makeText(DashboardActivity.this, "Menu Aktivasi diklik", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        displayUserData();
    }

    private void displayUserData() {
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);
        TextView textViewUserPosition = findViewById(R.id.textViewUserPosition);

        User user = (User) getIntent().getSerializableExtra("USER_DATA");

        if (user != null) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            textViewUserName.setText(fullName);
            textViewUserEmail.setText(user.getEmail());

            if (user.getPosition() != null) {
                textViewUserPosition.setText(user.getPosition().getName());
            }

            Toast.makeText(this, "Selamat datang, " + user.getFirstName(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}