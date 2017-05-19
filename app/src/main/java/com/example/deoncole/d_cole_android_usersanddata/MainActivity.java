//Deonslow Cole
//Cross Platform MD-1705
//MainActivity


package com.example.deoncole.d_cole_android_usersanddata;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Declare objects for the views
    EditText emailEt, passwordEt;
    TextView logInTv, logOutTv, verifyTv;
    Button logInBt;

    //Declare boolean object for when the device is connected to a network
    boolean isConnected;
    
    //Declare objects for user authorization and listener for changes
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    NetworkCheckReceiver mNetworkCheckReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the views with the xml
        emailEt = (EditText)findViewById(R.id.emailAddressEt);
        passwordEt = (EditText)findViewById(R.id.passwordEt);
        logInTv = (TextView)findViewById(R.id.logInTv);
        logOutTv = (TextView)findViewById(R.id.logOutTv);
        verifyTv = (TextView)findViewById(R.id.verifiedTv);
        logInBt = (Button)findViewById(R.id.logInBt);

        //Initialize the authorization
        mAuth = FirebaseAuth.getInstance();

        //Initialize the authorization listener to check when the user is signed in or out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    System.out.println("onAuthStateChanged:signed_out");
                }
            }
        };

        ConnectivityManager cManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_SHORT).show();
        }

        //Set on click listeners for the buttons
        findViewById(R.id.signUpBt).setOnClickListener(this);
        findViewById(R.id.hasAcctBt).setOnClickListener(this);
        findViewById(R.id.logInBt).setOnClickListener(this);
        findViewById(R.id.verifyBt).setOnClickListener(this);


        //Click listener for when the user wants to log out
        logOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

                if(!isConnected){
                    Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        Intent intent = new Intent(this, DatabaseAlarmManager.class);
        long alarmTime = 20000;
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                alarmTime, pIntent);

        Intent netIntent = new Intent(this, NetworkCheckReceiver.class);
        long netCheckTime = 2000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, netIntent, 0);
        AlarmManager netAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        netAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                netCheckTime, pendingIntent);

    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            updateUI(user);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Method for user to create an account and log in
    private void createAccount(String email, String password){

        if (!checkUserInputs()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println("createUserWithEmail:onComplete:" + task.isSuccessful());

                if(!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Authorization Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
            }
        });
    }

    //Method for user that already has an account to sign in
    private void signIn(String email, String password){

        if (!checkUserInputs()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println("signInWithEmail:onComplete:" + task.isSuccessful());

                if(!task.isSuccessful()){
                    System.out.println("signInWithEmail:failed");
                    Toast.makeText(MainActivity.this, "Email address or password not valid. " +
                                    "Please enter a valid email & password or create an account",
                            Toast.LENGTH_LONG).show();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
            }
        });
    }

    //Method for the user to sign out
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    //Method to check if the user has entered a email and password
    private boolean checkUserInputs(){

        boolean isEmpty = true;
        String email = emailEt.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            emailEt.setError("Email address is required");
            isEmpty = false;
        }

        if (!isValidEmail(email)){
            emailEt.setError("Please enter a valid Email address");
            isEmpty = false;
            System.out.println("EMAIL MATCH");
        }

        if(passwordEt.getText().length() < 6){
            passwordEt.setError("Please enter a password more than 6 characters");
            isEmpty = false;
        }

        return isEmpty;
    }


    //Method to update the UI when the user has signed in
    private void updateUI(FirebaseUser user) {

        if(user != null){
            String helloUser = getString(R.string.hello) + " " + user.getEmail();
            String notUser = "Not " + user.getEmail() + "? Click to log out.";
            emailEt.setVisibility(View.GONE);
            passwordEt.setVisibility(View.GONE);
            findViewById(R.id.signUpBt).setVisibility(View.GONE);
            findViewById(R.id.hasAcctBt).setVisibility(View.GONE);

            logInTv.setText(helloUser);
            logOutTv.setText(notUser);

            logInTv.setVisibility(View.VISIBLE);
            logOutTv.setVisibility(View.VISIBLE);

            logInBt.setVisibility(View.VISIBLE);

        } else {

            emailEt.setVisibility(View.VISIBLE);
            passwordEt.setVisibility(View.VISIBLE);
            findViewById(R.id.signUpBt).setVisibility(View.VISIBLE);
            findViewById(R.id.hasAcctBt).setVisibility(View.VISIBLE);

            logInTv.setVisibility(View.GONE);
            logOutTv.setVisibility(View.GONE);
            verifyTv.setVisibility(View.GONE);
            findViewById(R.id.logInBt).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        switch (id){
            case R.id.signUpBt:
                createAccount(email, password);
                break;
            case R.id.hasAcctBt:
                signIn(email, password);
                break;
            case R.id.logInBt:
                goToExpenseScreen();
                break;
            default:
                break;
        }
    }

    private void goToExpenseScreen(){
        Intent exActivityIntent = new Intent(this, ExpenseActivity.class);
        startActivity(exActivityIntent);
    }

    public final boolean isValidEmail(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    protected void onResume() {
        super.onResume();

        mNetworkCheckReceiver = new NetworkCheckReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_SERVICE);

        registerReceiver(mNetworkCheckReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mNetworkCheckReceiver);
    }

    private class NetworkCheckReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cManager = (ConnectivityManager)context.getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if(!isConnected){
                Toast.makeText(context, "Network is unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
