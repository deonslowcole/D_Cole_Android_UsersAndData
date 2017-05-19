//Deonslow Cole
//Cross Platform MD-1705
//ExpenseActivity


package com.example.deoncole.d_cole_android_usersanddata;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseActivity extends AppCompatActivity {

    //Declare objects for the views
    Button deleteDbBt;
    TextView locationTv, amountTv, dateTv;

    //Declare Strings for to hold the data from the database
    String location, amount, date;

    private static final String ACTION_POLL = "com.example.deoncole.d_cole_android_usersanddata" +
            ".ACTION_POLL";
    public static final String EXTRA_LOCATION = "com.example.deoncole.d_cole_android_usersanddata" +
            ".EXTRA_NAME";
    public static final String EXTRA_AMOUNT = "com.example.deoncole.d_cole_android_usersanddata" +
            ".EXTRA_AMOUNT";
    public static final String EXTRA_DATE = "com.example.deoncole.d_cole_android_usersanddata" +
            ".EXTRA_DATE";

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
        switch (selectedAction){
            case R.id.action_add:
                Intent goToFormScreen = new Intent(getApplicationContext(), FormActivity.class);
                startActivity(goToFormScreen);
                break;
            case R.id.action_update:
                updateExpenseDialog();
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
                        DecimalFormat dFormat = new DecimalFormat("#######.##");
                        String amtFormat = dFormat.format(Double.valueOf(childString));
                        amount = "$" + amtFormat;
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

    private void updateExpenseDialog(){

        final AlertDialog alert = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        alert.setView(dialogView);

        final EditText updateNameEt = (EditText) dialogView.findViewById(R.id.exUpdateNameEt);
        final EditText updateAmtEt = (EditText) dialogView.findViewById(R.id.exUpdateAmtEt);
        final EditText updateDateEt = (EditText) dialogView.findViewById(R.id.exUpdateDateEt);

        alert.setTitle("Update Expense");
        alert.setMessage("Enter Item you would like to update below");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uptName = updateNameEt.getText().toString().trim();
                String uptAmt = updateAmtEt.getText().toString().trim();
                Double amt = 0.0;
                String uptDate = updateDateEt.getText().toString().trim();

                HashMap<String, Object> updtChildren = new HashMap<>();

                if (TextUtils.isEmpty(uptAmt) && TextUtils.isEmpty(uptDate)){
                    updtChildren.put("exLocation", uptName);

                    dbRef.updateChildren(updtChildren);

                } else if(TextUtils.isEmpty(uptName) && TextUtils.isEmpty(uptDate)){
                    amt = Double.valueOf(uptAmt);
                    updtChildren.put("exAmount", amt);

                    dbRef.updateChildren(updtChildren);

                } else if(TextUtils.isEmpty(uptName) && TextUtils.isEmpty(uptAmt)){
                    if(checkDate(uptDate)){

                        updtChildren.put("exDate", uptDate);
                        dbRef.updateChildren(updtChildren);

                    } else {
                        Toast.makeText(ExpenseActivity.this, "Update date in MM/DD/YY format", Toast
                                .LENGTH_LONG).show();
                    }


                } else {
                    updtChildren.put("exLocation", uptName);
                    updtChildren.put("exAmount", amt);
                    updtChildren.put("exDate", uptDate);
                }
            }


        });

        alert.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private boolean checkDate(String date){

        String regEx = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.]\\d{2}$";

        Matcher matchObj = Pattern.compile(regEx).matcher(date);
        if (matchObj.matches()){
            System.out.println("THEY MATCH");
            return true;
        } else {
            System.out.println("THEY DON'T MATCH");
            return false;
        }
    }
}
