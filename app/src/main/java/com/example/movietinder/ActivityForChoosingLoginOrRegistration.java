package com.example.movietinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityForChoosingLoginOrRegistration extends AppCompatActivity {

    private Button loginButton ;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_choosing_login_or_registration);

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityForChoosingLoginOrRegistration.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityForChoosingLoginOrRegistration.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}