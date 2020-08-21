package com.example.kiit.senterprisr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiit.senterprisr.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private CircleImageView profileImageView;
private EditText fullNameEditText,userPhoneEditText,addressEditText;
private TextView profileChangeTextBtn,closeTextbtn,saveTextButton;
private Uri ImageUri;
private String myUri="";
private StorageTask uploadTask;
private StorageReference storageProfilePictureRef;
private String checker="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView=(CircleImageView)findViewById(R.id.settings_profile_image);
        fullNameEditText=(EditText)findViewById(R.id.settings_full_name);
        userPhoneEditText=(EditText)findViewById(R.id.settings_phone_number);
        addressEditText=(EditText)findViewById(R.id.settings_address);
        profileChangeTextBtn=(TextView)findViewById(R.id.profile_image_change_btn);
        closeTextbtn=(TextView)findViewById(R.id.close_settings_btn);
        saveTextButton=(TextView)findViewById(R.id.update_account_settings_btn);
        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEditText);
        fullNameEditText.setText(Prevalent.currentOnlineUsers.getName());
        Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).into(profileImageView);
        userPhoneEditText.setText(Prevalent.currentOnlineUsers.getPhone());
        addressEditText.setText(Prevalent.currentOnlineUsers.getAddress());



        closeTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    userInfoSave();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(ImageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    {
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        ImageUri = result.getUri();
                        profileImageView.setImageURI(ImageUri);
                    }
                }
            }
        }
        else
        {
            Toast.makeText(this,"Error,Try Again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMAp=new HashMap<>();
        userMAp.put("name",fullNameEditText.getText().toString());
        userMAp.put("address",addressEditText.getText().toString());
        userMAp.put("phoneOrder",userPhoneEditText.getText().toString());

        startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));

        Toast.makeText(SettingsActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
        finish();
        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMAp);


    }

    private void userInfoSave() {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this,"Name is mandatory",Toast.LENGTH_SHORT).show();

        }

        else   if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this,"Address is mandatory",Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this,"Phone number is mandatory",Toast.LENGTH_SHORT).show();

        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }


    }

    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Wait,while we are updating the information.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(ImageUri !=null)
        {
            final StorageReference fileref=storageProfilePictureRef
                    .child(Prevalent.currentOnlineUsers.getPhone()+".jpg");
            uploadTask=fileref.putFile(ImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                  if(!task.isSuccessful())
                  {
                      throw task.getException();
                  }
                    return fileref.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                Uri downloaduri=task.getResult();
                                myUri=downloaduri.toString();
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                                HashMap<String,Object> userMAp=new HashMap<>();
                                userMAp.put("name",fullNameEditText.getText().toString());
                                userMAp.put("address",addressEditText.getText().toString());
                                userMAp.put("phoneOrder",userPhoneEditText.getText().toString());
                                userMAp.put("image",myUri);
                                ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMAp);

                                progressDialog.dismiss();
                                startActivity(new Intent(SettingsActivity.this,HomeActivity.class));

                                Toast.makeText(SettingsActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this,"Image not selected",Toast.LENGTH_SHORT).show();

        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image=dataSnapshot.child("image").getValue().toString();

                        String name=dataSnapshot.child("name").getValue().toString();

                        String phone=dataSnapshot.child("phone").getValue().toString();

                        String address=dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
