package com.buszer_bus.admin.buszer_user_final;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

/**
 *
 * Created by Admin on 11/22/2017.
 */

public class Logout extends MainActivity {

   Activity main;

    Context context;


    public Logout(Context context, Activity main
    ) {

       this.main = main;
        this.context = context;
    }


    public void logout_dialog(final ProgressDialog loader, FirebaseAuth firebaseAuth){
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseAuth finalFirebaseAuth = firebaseAuth;
        AlertDialog.Builder logout_dialog = new AlertDialog.Builder(main);
        logout_dialog.setMessage("Are you sure you want to log-out?");



        logout_dialog.setPositiveButton("Yes",
                (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        loader.setMessage(" Signing Out...");
                        loader.show();
                        finalFirebaseAuth.signOut();
                        Intent exit = new Intent(context,Loginpage.class)
                                  .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(exit);
                        finish();


                    }
                });

        logout_dialog.setNegativeButton("No",
                (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        AlertDialog alertDialog = logout_dialog.create();
        alertDialog.show();


    }


}
