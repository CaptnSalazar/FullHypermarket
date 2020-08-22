package com.example.kiit.senterprisr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiit.senterprisr.Prevalent.Prevalent;
import com.example.kiit.senterprisr.ViewHolder.CartViewHolder;
import com.example.kiit.senterprisr.model.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private RecyclerView.LayoutManager layoutManager;
private TextView txtmsg;
private Button NextProcessBtn;
private int overalTotalPrice=0;

    private Boolean exit = false;
private String totalamount="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        totalamount = getIntent().getStringExtra("Total Price");
        txtmsg = (TextView) findViewById(R.id.msg1);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);


            NextProcessBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(overalTotalPrice !=0) {
                        Intent intent = new Intent(CartActivity.this, ConfirmFinalOrder.class);
                        intent.putExtra("Total Price", String.valueOf(overalTotalPrice));
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(CartActivity.this,"Your Cart is Empty.Add New Items in your Cart",Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart>options=
                new  FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUsers.getPhone()).child("Products"),Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder>adapter
                =new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull final Cart cart) {

                holder.txtProductQuantity.setText(cart.getQuantity());

                holder.txtProductname.setText(cart.getName());

                holder.txtProductPrice.setText("Rs."+cart.getPrice());
                int oneTypeProductPrice=((Integer.valueOf(cart.getPrice()))*(Integer.valueOf(cart.getQuantity())));
                overalTotalPrice=overalTotalPrice+oneTypeProductPrice;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]=new  CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("name",cart.getName());
                                    startActivity(intent);
                                }
                                else if(i==1)
                                {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUsers.getPhone())
                                            .child("Products")
                                            .child(cart.getName())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful())
                                               {
                                                   Toast.makeText(CartActivity.this,"Item removed Successfully.",Toast.LENGTH_SHORT).show();

                                                   Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                   startActivity(intent);
                                               }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void CheckOrderState()
    {
        DatabaseReference orderRef;
        orderRef=FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone()).child("UserInfo");
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String shippingstate=dataSnapshot.child("State").getValue().toString();

                    String username=dataSnapshot.child("name").getValue().toString();
                    if(shippingstate.equals("shipped"))
                    {
                        recyclerView.setVisibility(View.GONE);
                        txtmsg.setVisibility(View.VISIBLE);
                        txtmsg.setText("Congratulations Your Order is placed and soon will be verified by the owner.");

                        NextProcessBtn.setVisibility(View.GONE);
                    }
                    else  if(shippingstate.equals("shipped and paid"))
                    {
                        recyclerView.setVisibility(View.GONE);
                        txtmsg.setVisibility(View.VISIBLE);
                        txtmsg.setText("Congratulations Your Order is placed and soon will be verified by the owner.");

                        NextProcessBtn.setVisibility(View.GONE);
                    }
                    else if(shippingstate.equals("not shipped"))
                    {
                        recyclerView.setVisibility(View.GONE);
                        txtmsg.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
         {
            finish();

            Intent intent = new Intent(CartActivity.this,
                HomeActivity.class);
            startActivity(intent);

        }
    }
}
