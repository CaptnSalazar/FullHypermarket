package com.example.kiit.senterprisr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiit.senterprisr.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrder extends AppCompatActivity {
    private TextView totalamt;
private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
private Button confirmorderbtn;
private String totalAmount="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        totalAmount=getIntent().getStringExtra("Total Price");
        totalamt=(TextView)findViewById(R.id.totalamt);
        totalamt.setText(totalAmount);
        confirmorderbtn=(Button)findViewById(R.id.confirm_final_btn);
        nameEditText=(EditText)findViewById(R.id.shipment_name);
        phoneEditText=(EditText)findViewById(R.id.shipment_phone);
        addressEditText=(EditText)findViewById(R.id.shipment_address);
        cityEditText=(EditText)findViewById(R.id.shipment_city);
        confirmorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });

    }

    private void Check() {
        if((TextUtils.isEmpty(nameEditText.getText().toString()))
        ||(TextUtils.isEmpty(addressEditText.getText().toString()))
                ||(TextUtils.isEmpty(phoneEditText.getText().toString()))
                ||(TextUtils.isEmpty(cityEditText.getText().toString())))
        {
            Toast.makeText(this,"All fields must be fill",Toast.LENGTH_SHORT).show();

        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
       final String savecurrentdate,savecurrenttime;
        Calendar calfordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate=currentDate.format(calfordate.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime=currentDate.format(calfordate.getTime());

        final DatabaseReference OrderRef= FirebaseDatabase.getInstance().
                getReference().child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());
        HashMap<String,Object> orderMAp=new HashMap<>();
        orderMAp.put("totalAmount",totalAmount);
        orderMAp.put("name",nameEditText.getText().toString());
        orderMAp.put("phone",phoneEditText.getText().toString());
        orderMAp.put("date",savecurrentdate);
        orderMAp.put("time",savecurrenttime);
        orderMAp.put("city",cityEditText.getText().toString());
        orderMAp.put("address",addressEditText.getText().toString());
        orderMAp.put("State","not shipped");
        OrderRef.updateChildren(orderMAp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {String amount=totalAmount;
                    Intent intent=new Intent(ConfirmFinalOrder.this,CheckOutScreen.class);
                    intent.putExtra("amount",amount);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }
}
