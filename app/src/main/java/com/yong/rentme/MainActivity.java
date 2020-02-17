package com.yong.rentme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnLogout;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.main_btn_logout);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        ed = prefs.edit();

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.main_btn_logout:
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();

                        ed.putBoolean("isLogined", false);
                        ed.apply();

                        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                        finish();
                }
            }
        };
        btnLogout.setOnClickListener(btnClickListener);
    }
}