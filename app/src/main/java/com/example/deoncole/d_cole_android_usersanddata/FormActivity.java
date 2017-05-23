//Deonslow Cole
//Cross Platform MD-1705
//FormActivity


package com.example.deoncole.d_cole_android_usersanddata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deoncole.d_cole_android_usersanddata.Objects.ExpenseObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormActivity extends AppCompatActivity {

    //Declare objects for the views
    EditText exLocationEt, exAmountEt, exDateEt;
    TextView exTitleTv;

    //Declare an object for the database
    FirebaseDatabase fbDatabase;

    //Declare an object to reference teh database
    DatabaseReference dbRef;

    //Declare an object for authorization to the database
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //Initialize the authorization
        mAuth = FirebaseAuth.getInstance();

        //Set the views to the xml
        exLocationEt = (EditText)findViewById(R.id.exLocationEt);
        exAmountEt = (EditText)findViewById(R.id.exAmountEt);
        exDateEt = (EditText)findViewById(R.id.exDateEt);
        exTitleTv = (TextView)findViewById(R.id.expenseTitleTv);

        //Get the date to pre-load to the date edit text field
//        SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy" );
//        exDateEt.setText( sdf.format( new Date() ));

        //Initialize the database and reference objects
        fbDatabase = FirebaseDatabase.getInstance();
        dbRef = fbDatabase.getReference("user_expenses").child(mAuth.getCurrentUser().getUid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_form_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Declare objects for the user input
        String exLocation = exLocationEt.getText().toString().trim();
        String amount = exAmountEt.getText().toString().trim();
        Double exAmount = 0.0;
        String exDate = exDateEt.getText().toString().trim();

        //Declare a variable to hold the item id
        int selectedAction = item.getItemId();

        //If the id is equal to the action icon id go to the next activity using an intent
        if (selectedAction == R.id.action_save){

            if(TextUtils.isEmpty(exLocation) && TextUtils.isEmpty(amount) && TextUtils.isEmpty(exDate)){
                Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(exLocation)){
                exLocationEt.setError("Please enter a location");
            } else if (TextUtils.isEmpty(amount)){
                exAmountEt.setError("Please enter a amount");
            } else if (TextUtils.isEmpty(exDate)){
                exDateEt.setError("Please enter a date in MM/DD/YY format");
            } else if (!checkDate(exDate)){
                exDateEt.setError("Please enter a date in MM/DD/YY format");
            } else {
                DecimalFormat dFormat = new DecimalFormat("######.##");
                String amtFormat = dFormat.format(Double.valueOf(amount));
                exAmount = Double.parseDouble(amtFormat);
//                exAmount = Double.parseDouble(amount);
                dbRef.setValue(new ExpenseObjects(exLocation, exAmount, exDate));
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
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
