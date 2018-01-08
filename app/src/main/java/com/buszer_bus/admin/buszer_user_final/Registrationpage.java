package com.buszer_bus.admin.buszer_user_final;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import at.markushi.ui.CircleButton;

import static android.R.attr.tag;

public class Registrationpage extends AppCompatActivity implements View.OnClickListener{





    EditText name, username, email, password, address;
    Button signup;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference mroot = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user_email,user_id;
    private ProgressDialog loader;
    static String pass_email,pass_username,pass_name , pass_address;


    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_registrationpage);

        loader = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_email = FirebaseAuth.getInstance().getCurrentUser();

        name = (EditText)findViewById(R.id.name);
        username = (EditText)findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        address = (EditText)findViewById(R.id.address);
        password = (EditText)findViewById(R.id.password);
        signup = (Button)findViewById(R.id.signup);



        signup.setOnClickListener(this);
    }


    private void sendEmail() {
  user_email.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(String.valueOf(tag), "Email Sent");
                }else {
                    Log.d(String.valueOf(tag), "Email Not Sent");
                }
            }
        });
    }
    private void registerUser() {

        final String input_email= email.getText().toString().trim();
        final String input_password = password.getText().toString().trim();
        final String in_name = name.getText().toString().trim();
        final String in_address = address.getText().toString();
        final String in_username = username.getText().toString();

        if(in_username.equals("")){
            Toast.makeText(Registrationpage.this,"Username is Required",Toast.LENGTH_LONG).show();
            return;
        }
        if(in_name.equals("")){
            Toast.makeText(Registrationpage.this,"Name is Required",Toast.LENGTH_LONG).show();
            return;
        }
        if (in_address.equals("")){
            Toast.makeText(Registrationpage.this,"Address is Required",Toast.LENGTH_LONG).show();
            return;
        }
        if(input_email.equals("")){
            Toast.makeText(Registrationpage.this,"Email is Required",Toast.LENGTH_LONG).show();
            return;
        }
        if (input_password.equals("")){
            Toast.makeText(Registrationpage.this,"Password is Required",Toast.LENGTH_LONG).show();
            return;
        }




        loader.setMessage(" Registering  User...");
        loader.show();

       firebaseAuth.createUserWithEmailAndPassword(input_email,input_password)
               .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){

                   Toast.makeText(Registrationpage.this,"Please verify your email to complete registration",Toast.LENGTH_LONG).show();
                   loader.dismiss();
                   sendEmail();
                   user_id = firebaseAuth.getCurrentUser();
                   userid = user_id.getUid();
                 DatabaseReference passenger =  mroot.child("Passenger").child(userid);
                                   passenger.child("Username").setValue(in_username);
                                   passenger.child("Name").setValue(in_name);
                                   passenger.child("Address").setValue(in_address);
                                   passenger.child("Email").setValue(input_email);
                 DatabaseReference public_info = mroot.child("Users").child(in_username);
                   public_info.child("Username").setValue(in_username);
                   public_info.child("Name").setValue(in_name);
                   public_info.child("Address").setValue(in_address);
                   public_info.child("Email").setValue(input_email);


                   Intent back_to_login = new Intent(Registrationpage.this,Loginpage.class);
                   startActivity(back_to_login);
                   finish();
               }
               else {
                   Toast.makeText(Registrationpage.this,"Error, Please check your internet connection",Toast.LENGTH_LONG).show();
                   loader.dismiss();
               }
           }


               });


    }
    @Override
    public void onClick(View v) {
        if(v == signup){
           registerUser();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back_to_login = new Intent(Registrationpage.this,Loginpage.class);
        startActivity(back_to_login);
        finish();

    }
}
