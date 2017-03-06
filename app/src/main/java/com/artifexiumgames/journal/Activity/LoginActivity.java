package com.artifexiumgames.journal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.artifexiumgames.journal.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });
    }

    private void checkPassword(){
        if (((TextView) findViewById(R.id.passwordText)).getText().toString().equals("")){ //Hard coding a password is bad code, but it's for my personal use so WHATEVER
            Toast.makeText(getApplicationContext(), "Login Succesful", Toast.LENGTH_SHORT);
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
