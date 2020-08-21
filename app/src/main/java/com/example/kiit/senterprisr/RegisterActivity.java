package com.example.kiit.senterprisr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
private Button createaccountbutton;
private EditText Inputname,Inputphonenumber,Inputpassword,email;
private ProgressDialog lodingbar;
private TextView countrycode;

    private Boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createaccountbutton=(Button) findViewById(R.id.register_btn);
       Inputname=(EditText) findViewById(R.id.register_username_input);
       Inputphonenumber=(EditText) findViewById(R.id.register_phone_number_input);
        Inputpassword=(EditText) findViewById(R.id.register_password_input);
        countrycode=(TextView)findViewById(R.id.editTextCountryCode);
        email=(EditText)findViewById(R.id.register_email_input);
        lodingbar=new ProgressDialog(this);

        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }



    private void CreateAccount()
    {
        String name=Inputname.getText().toString();
        String phone=Inputphonenumber.getText().toString();
        String password=Inputpassword.getText().toString();
        String emaill=email.getText().toString();
        String code=countrycode.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(phone)||phone.length()!=10){
            Toast.makeText(this,"Please enter a vaild phone number",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(emaill)){
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show();

        }
        else
        {
            lodingbar.setTitle("Create Account");
            lodingbar.setMessage("Please wait while we check the credentials");
            lodingbar.setCanceledOnTouchOutside(false);
            lodingbar.show();
            ValidatephoneNumber(name,phone,password,code,emaill);

        }
    }
    private void ValidatephoneNumber(final String name, final String phone, final String password,final String code,final String emaill)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String,Object>userdatamap=new HashMap<>();
                    userdatamap.put("phone",phone);
                    userdatamap.put("password",password);
                    userdatamap.put("name",name);
                    userdatamap.put("email",emaill);
                    RootRef.child("Users").child(phone).updateChildren(userdatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        lodingbar.dismiss();
                                        String phoneNumber = code+phone;
                                        String phonen=phone;
                                        Intent intent = new Intent(RegisterActivity.this, VerificationCode.class);
                                        intent.putExtra("phoneNumber", phoneNumber);
                                        intent.putExtra("phone",phone);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this,"Check your internet connection",Toast.LENGTH_SHORT).show();
                                        lodingbar.dismiss();

                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Try with another phone number.This phone number already exist.",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(RegisterActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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

