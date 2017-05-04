package com.example.deoncole.d_cole_android_usersanddata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.deoncole.d_cole_android_usersanddata.Objects.ExpenseObjects;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExpenseListActivity extends AppCompatActivity {

    Button readDbBt, deleteDbBt;
    TextView locationTv, amountTv, dateTv;
    String location, amount, date;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        dbRef = database.getReference("user_expenses").child("expenses");

        locationTv = (TextView)findViewById(R.id.locationTv);
        amountTv = (TextView)findViewById(R.id.amountTv);
        dateTv = (TextView)findViewById(R.id.dateTv);
        readDbBt = (Button)findViewById(R.id.readDbBt);
        deleteDbBt = (Button)findViewById(R.id.deleteDbBt);

        readDbBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readDatabase();
                locationTv.setText(location);
                amountTv.setText(amount);
                dateTv.setText(date);
            }
        });

        deleteDbBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.setValue(null);
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
                    ExpenseObjects obj = objSnapShot.getValue(ExpenseObjects.class);

                    location = obj.getExLocation();
                    amount = "Amount spent was " + String.valueOf(obj.getExAmount());
                    date = obj.getExDate();

//                    String amountText = "Amount spent was " + String.valueOf(obj.getExAmount());
//                    locationTv.setText(obj.getExLocation());
//                    amountTv.setText(amountText);
//                    dateTv.setText(obj.getExDate());
//                    System.out.println(obj.getExAmount());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
