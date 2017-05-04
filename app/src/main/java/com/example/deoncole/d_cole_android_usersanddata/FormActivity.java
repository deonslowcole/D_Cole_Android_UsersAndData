package com.example.deoncole.d_cole_android_usersanddata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.deoncole.d_cole_android_usersanddata.Objects.ExpenseObjects;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FormActivity extends AppCompatActivity {

    EditText exTypeEt, exLocationEt, exAmountEt, exDateEt;
    String dbRefString;
    FirebaseDatabase fbDatabase;
    public static DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        exTypeEt = (EditText)findViewById(R.id.exTypeEt);
        exLocationEt = (EditText)findViewById(R.id.exLocationEt);
        exAmountEt = (EditText)findViewById(R.id.exAmountEt);
        exDateEt = (EditText)findViewById(R.id.exDateEt);

        dbRefString = "expenses";

        SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy" );
        exDateEt.setText( sdf.format( new Date() ));

        fbDatabase = FirebaseDatabase.getInstance();
        dbRef = fbDatabase.getReference("user_expenses");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_form_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String exType = exTypeEt.getText().toString().trim();
        String exLocation = exLocationEt.getText().toString().trim();
        String amount = exAmountEt.getText().toString().trim();
        Double exAmount = Double.parseDouble(amount);
        String exDate = exDateEt.getText().toString().trim();

        //Declare a variable to hold the item id
        int selectedAction = item.getItemId();

        //If the id is equal to the action icon id go to the next activity using an intent
        if (selectedAction == R.id.action_save){

//            ExpenseObjects exObject = new ExpenseObjects(exLocation, exAmount, exDate);

            DatabaseReference expenseRef = dbRef.child(dbRefString);

//            HashMap<String, ExpenseObjects> expenses = new HashMap<>();
//            expenses.put(exType, new ExpenseObjects(exLocation, exAmount, exDate));
            DatabaseReference newExpRef = expenseRef.push();
            newExpRef.setValue(new ExpenseObjects(exLocation, exAmount, exDate));

//            expenseRef.setValue(expenses);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
