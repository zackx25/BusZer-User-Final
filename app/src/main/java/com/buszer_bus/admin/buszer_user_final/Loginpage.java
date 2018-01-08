package com.buszer_bus.admin.buszer_user_final;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Loginpage extends AppCompatActivity implements View.OnClickListener {
    EditText password,email;
    TextView sign_up;
    Button login_btn;
    ProgressDialog loader;
    private static final String TAG = "Loginpage";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean connnecton_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_loginpage);


        connnecton_status = isConnect(this);

        loader = new ProgressDialog(this);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.access);
      sign_up = (TextView)findViewById(R.id.textview);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };

        login_btn.setOnClickListener(this);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registration = new Intent(Loginpage.this,Registrationpage.class);
                startActivity(registration);
                finish();
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }



    @Override
    public void onClick(View v) {
        String in_email = email.getText().toString().trim();
        String in_password = password.getText().toString().trim();

        if(in_email.equals("")){
            Toast.makeText(Loginpage.this,"Email is Required",Toast.LENGTH_LONG).show();
            return;
        }
        if (in_password.equals("")){
            Toast.makeText(Loginpage.this,"Password is Required",Toast.LENGTH_LONG).show();
            return;
        }

        loader.setMessage(" Signing In...");
        loader.show();
          firebaseAuth.signInWithEmailAndPassword(in_email,in_password)
                  .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          FirebaseUser user = firebaseAuth.getCurrentUser();
                          boolean emailVerified = user.isEmailVerified();

                          if(emailVerified==false){
                              Toast.makeText(Loginpage.this,"Unable to login, please verify your email",Toast.LENGTH_LONG).show();
                              loader.dismiss();
                              return;
                          }
                          if (task.isSuccessful()) {
                              loader.dismiss();
                              Toast.makeText(Loginpage.this, "Successfully Sign-in",
                                      Toast.LENGTH_SHORT).show();
                              Intent access_mainpage = new Intent(Loginpage.this,MainActivity.class);
                              startActivity(access_mainpage);
                              finish();
                          }
                          else {
                              loader.dismiss();
                              Toast.makeText(Loginpage.this, "Authentication failed. Invalid email or password",
                                      Toast.LENGTH_SHORT).show();
                          }

                          // ...
                      }
                  });


          firebaseAuth.signInWithEmailAndPassword(in_email,in_password).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  e.printStackTrace();
                  Toast.makeText(Loginpage.this,"Or check your internet connection",Toast.LENGTH_LONG).show();
              }
          });
      }



    public static boolean isConnect(Activity activity) {
        boolean flag = false;

        ConnectivityManager cwjManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();

        return flag;
    }
  

}
