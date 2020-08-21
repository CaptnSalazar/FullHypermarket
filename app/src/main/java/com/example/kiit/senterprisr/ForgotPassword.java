package com.example.kiit.senterprisr;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Boolean exit = false;
    TextInputEditText editTextCountryCode,editTextPhone,newpass;
    AppCompatButton buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextCountryCode = findViewById(R.id.editTextCountryCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonContinue = findViewById(R.id.buttonContinue);
        newpass=findViewById(R.id.newpassword);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCountryCode.getText().toString().trim();
                String number = editTextPhone.getText().toString().trim();
                String pass=newpass.getText().toString().trim();
                if (number.isEmpty() || number.length() <10) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }

                String phoneNumber = code + number;
                String num=number;
                Intent intent = new Intent(ForgotPassword.this, newpassverify.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("password",pass);
                intent.putExtra("num",num);
                startActivity(intent);

            }
        });
    }
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ForgotPassword.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }

    }


