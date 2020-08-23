package com.example.kiit.senterprisr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiit.senterprisr.Prevalent.Prevalent;
import com.example.kiit.senterprisr.ViewHolder.CategoriesViewHolder;
import com.example.kiit.senterprisr.model.Categ;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Categories extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference Cat;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private Boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Paper.init(this);


        Cat= FirebaseDatabase.getInstance().getReference().child("TotalCategory");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Categories.this,CartActivity.class);
                startActivity(intent);

            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Categories");
        setSupportActionBar(toolbar);

        DrawerLayout  drawer = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView= navigationView.getHeaderView(0);
        TextView userNameTextView=headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView=headerView.findViewById(R.id.profile_image);
        userNameTextView.setText(Prevalent.currentOnlineUsers.getName());
        Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).into(profileImageView);
        recyclerView=findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout  drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (exit) {
            finishAffinity();

        } else {
            Toast.makeText(Categories.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Categ>options=
                new FirebaseRecyclerOptions.Builder<Categ>()
                        .setQuery(Cat, Categ.class)
                        .build();
        FirebaseRecyclerAdapter<Categ, CategoriesViewHolder>adapter=
                new FirebaseRecyclerAdapter<Categ, CategoriesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull final Categ model) {
                        holder.txtCat.setText(model.getCategory());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(Categories.this,CategoriesDetailsActivity.class);
                                intent.putExtra("cat",model.getCategory());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.catitem,parent,false);
                        CategoriesViewHolder holder=new CategoriesViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_return)
        {
            Intent intent=new Intent(Categories.this,ReturnActivity.class);
            startActivity(intent);

        }
        else  if(id==R.id.nav_dash)
        {
            Intent intent=new Intent(Categories.this, HomeActivity.class);
            startActivity(intent);

        }

        else  if(id==R.id.nav_about)
        {
            Intent intent=new Intent(Categories.this,About.class);
            startActivity(intent);

        }
        else  if(id==R.id.nav_changepass)
        {
            Intent intent=new Intent(Categories.this,ForgotPassword.class);
            startActivity(intent);
        }
        else  if(id==R.id.nav_logout)
        {
            Paper.book().destroy();
            Intent intent=new Intent(Categories.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }



        DrawerLayout  drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }


}


