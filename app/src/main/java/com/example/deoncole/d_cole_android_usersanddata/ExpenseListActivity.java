package com.example.deoncole.d_cole_android_usersanddata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.deoncole.d_cole_android_usersanddata.Objects.ExpenseObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ExpenseListActivity extends AppCompatActivity {

    Button deleteDbBt;
    TextView locationTv, amountTv, dateTv;
    String location, amount, date;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String authString = mAuth.getCurrentUser().getUid();
        dbRef = database.getReference("user_expenses").child(authString);

        locationTv = (TextView)findViewById(R.id.locationTv);
        amountTv = (TextView)findViewById(R.id.amountTv);
        dateTv = (TextView)findViewById(R.id.dateTv);
        deleteDbBt = (Button)findViewById(R.id.deleteDbBt);

        readDatabase();

        deleteDbBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.setValue(null);
                location = null;
                date = null;
                amount = null;
                locationTv.setText("");
                amountTv.setText("");
                dateTv.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_expense_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Declare a variable to hold the item id
        int selectedAction = item.getItemId();

        //If the id is equal to the action icon id go to the next activity using an intent
        if (selectedAction == R.id.action_add){
            Intent goToFormScreen = new Intent(getApplicationContext(), FormActivity.class);
            startActivity(goToFormScreen);
        }

        return super.onOptionsItemSelected(item);
    }

    private void readDatabase(){

       dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()){

                    String childString = objSnapShot.getValue().toString();
                    if (Objects.equals(objSnapShot.getKey(), "exDate")) {
                        date = childString;
                    } else if (Objects.equals(objSnapShot.getKey(), "exAmount")) {
                        amount = childString;
                    } else if (Objects.equals(objSnapShot.getKey(), "exLocation")) {
                        location = childString;
                    }

                }

                locationTv.setText(location);
                if(amount != null){
                    amountTv.setText(amount);
                }else{
                    amountTv.setText("No expenses logged");
                }
                dateTv.setText(date);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
