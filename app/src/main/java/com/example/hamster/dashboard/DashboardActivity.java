package com.example.hamster.dashboard;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamster.R;
import com.example.hamster.data.model.User;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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

            Toast.makeText(this, "Login Berhasil! Selamat datang, " + user.getFirstName(), Toast.LENGTH_LONG).show();

        }
    }
}