//Deonslow Cole
//Cross Platform MD-1705
//ExpenseActivity


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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ExpenseActivity extends AppCompatActivity {

    //Declare objects for the views
    Button deleteDbBt;
    TextView locationTv, amountTv, dateTv;

    //Declare Strings for to hold the data from the database
    String location, amount, date;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Declare a string to hold the user id
        String authString = mAuth.getCurrentUser().getUid();

        //Reference the database using the users id
        dbRef = database.getReference("user_expenses").child(authString);

        //Set the objects to the xml
        locationTv = (TextView)findViewById(R.id.locationTv);
        amountTv = (TextView)findViewById(R.id.amountTv);
        dateTv = (TextView)findViewById(R.id.dateTv);
        deleteDbBt = (Button)findViewById(R.id.deleteDbBt);

        //Call the method to read the database
        readDatabase();

        //Set an on click listener for the delete button. Set the value of the database to null
        // to empty it and the strings to null to clear them out.
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
        menuInflater.inflate(R.menu.actionbar_expense, menu);
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

    //Method ot read the database
    private void readDatabase(){

        //Set a listener for when the data in the database changes
       dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Loop through the children in the database
                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()){

                    //Get the string value of the child in the database and set it to the the
                    // string objects
                    String childString = objSnapShot.getValue().toString();
                    if (Objects.equals(objSnapShot.getKey(), "exDate")) {
                        date = childString;
                    } else if (Objects.equals(objSnapShot.getKey(), "exAmount")) {
                        amount = childString;
                    } else if (Objects.equals(objSnapShot.getKey(), "exLocation")) {
                        location = childString;
                    }
                }

                //Set the text views text to the string objects.
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
