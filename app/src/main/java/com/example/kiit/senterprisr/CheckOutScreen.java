package com.example.kiit.senterprisr;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kiit.senterprisr.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckOutScreen extends AppCompatActivity {

    TextView amountEt, nameEt, upiIdEt;
    Button send,cod;
    private Boolean exit = false;
    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_screen);

        initializeViews();
cod.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final DatabaseReference OrderRef= FirebaseDatabase.getInstance().
                getReference().child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
        HashMap<String,Object> order=new HashMap<>();
        order.put("State","shipped");
        OrderRef.updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CheckOutScreen.this, "Ordered placed succesfully.", Toast.LENGTH_SHORT).show();


                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("User View")
                .child(Prevalent.currentOnlineUsers.getPhone())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {//clearing cart for confirming order
                            Intent intent=new Intent(CheckOutScreen.this,HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }
});
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting the values from the EditTexts
                String amount = getIntent().getStringExtra("amount");
                amountEt.setText(amount);

                String name = nameEt.getText().toString();
                String upiId = upiIdEt.getText().toString();
                payUsingUpi(amount, upiId, name);
            }
        });
    }

    void initializeViews() {
        send = findViewById(R.id.send);
        amountEt = findViewById(R.id.amount_et);
        nameEt = findViewById(R.id.name);
        upiIdEt = findViewById(R.id.upi_id);

cod=findViewById(R.id.cod);
    }

    void payUsingUpi(String amount, String upiId, String name) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(CheckOutScreen.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(CheckOutScreen.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(CheckOutScreen.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
                final DatabaseReference OrderRef= FirebaseDatabase.getInstance().
                        getReference().child("Orders")
                        .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
                HashMap<String,Object> order=new HashMap<>();
                order.put("State","shipped and paid");
                OrderRef.updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CheckOutScreen.this, "Ordered placed succesfully.", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
                FirebaseDatabase.getInstance().getReference().child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUsers.getPhone())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {//clearing cart for confirming order
                                    Intent intent=new Intent(CheckOutScreen.this,HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(CheckOutScreen.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();

                final DatabaseReference OrderRef = FirebaseDatabase.getInstance().
                        getReference().child("Orders")
                        .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
                HashMap<String, Object> order = new HashMap<>();
                order.put("State", "");
                OrderRef.updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckOutScreen.this, "Cannot place order", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CheckOutScreen.this, CartActivity.class);
                            startActivity(intent);

                        }
                    }
                });
            }
            else {
                Toast.makeText(CheckOutScreen.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                final DatabaseReference OrderRef = FirebaseDatabase.getInstance().
                        getReference().child("Orders")
                        .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
                HashMap<String, Object> order = new HashMap<>();
                order.put("State", "");
                OrderRef.updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckOutScreen.this, "Cannot place order", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CheckOutScreen.this, CartActivity.class);
                            startActivity(intent);

                        }
                    }
                });
            }
        } else {
            Toast.makeText(CheckOutScreen.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            final DatabaseReference OrderRef = FirebaseDatabase.getInstance().
                    getReference().child("Orders")
                    .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
            HashMap<String, Object> order = new HashMap<>();
            order.put("State", "");
            OrderRef.updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CheckOutScreen.this, "Cannot place order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckOutScreen.this, CartActivity.class);
                        startActivity(intent);

                    }
                }
            });
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        if (exit) {
            final DatabaseReference OrderRef = FirebaseDatabase.getInstance().
                    getReference().child("Orders")
                    .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
            HashMap<String, Object> order = new HashMap<>();
            order.put("State", "");
            OrderRef.updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CheckOutScreen.this, "Cannot place order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckOutScreen.this, CartActivity.class);
                        startActivity(intent);

                    }
                }
            });
        }

         else {
            Toast.makeText(CheckOutScreen.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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