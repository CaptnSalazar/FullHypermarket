package com.example.kiit.senterprisr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView suraj,raghav;
private Button logoutBtn,CheckOrderBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        logoutBtn=(Button)findViewById(R.id.adminlogout);
        suraj=(ImageView)findViewById(R.id.suraj);
        raghav=(ImageView)findViewById(R.id.raghav);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        suraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AddNewProductActivity.class);
                intent.putExtra("category", "suraj");
                startActivity(intent);
            }
        });
       raghav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AddNewProductActivity.class);
                intent.putExtra("category", "raghav");
                startActivity(intent);
            }
        });

    }
}
