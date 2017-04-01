package uk.ac.northumbria.securephonebook;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.helpers.DatabaseHelper;
import uk.ac.northumbria.securephonebook.models.Group;

public class AddContactActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    Toolbar toolbar;                // toolbar
    TextInputLayout firstNameText;  // first name of the contact
    TextInputLayout lastNameText;   // last name of the contact
    TextInputLayout emailText;      // email of the contact
    TextInputLayout numberText;     // numberText of the contact
    Spinner groupSpinner;           // dropdown to display groups
    private int groupId = 1;        // defaults group to home
    ArrayAdapter groupsAdapter;     // responsible for displaying groups.
    DatabaseHelper dbHelper;        // database helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        toolbar = (Toolbar) findViewById(R.id.addContactToolbar);
        setSupportActionBar(toolbar);

        // initializing variables
        firstNameText = (TextInputLayout) findViewById(R.id.firstNameText);
        lastNameText = (TextInputLayout) findViewById(R.id.lastNameText);
        emailText = (TextInputLayout) findViewById(R.id.emailText);
        numberText = (TextInputLayout) findViewById(R.id.numberText);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinner);

        // setting listeners for interactivity (error validation)
        firstNameText.getEditText().setOnFocusChangeListener(this);
        lastNameText.getEditText().setOnFocusChangeListener(this);
        emailText.getEditText().setOnFocusChangeListener(this);
        numberText.getEditText().setOnFocusChangeListener(this);

        // populating the group spinner
        dbHelper = new DatabaseHelper(this);
        ArrayList<Group> groups = dbHelper.getAllGroups();
        groupsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groups);
        groupSpinner.setAdapter(groupsAdapter);


        // group spinner listener
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group) parent.getItemAtPosition(position);
                groupId = group.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                groupId = 0;
            }
        });


    }

    /**
     * Validation for email address.
     * @param email - Email to be validated.
     * @return - true if the email is valid else false.
     */
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validation for the numberText.
     * @param number - Number to be validated.
     * @return - true if the numberText is valid else false.
     */
    private boolean isNumberValid(CharSequence number) {
        return Patterns.PHONE.matcher(number).matches();
    }

    /**
     * This method validates all the fields on the activity.
     * @return - true if valid else false.
     */
    private boolean validate() {
        // first name validation
        if (firstNameText.getEditText().getText().toString().trim().isEmpty()) {
            firstNameText.setError("Please enter the first name.");
            return false;
        } else firstNameText.setError(null);

        // last name validation
        if (lastNameText.getEditText().getText().toString().trim().isEmpty()) {
            lastNameText.setError("Please enter the last name.");
            return false;
        } else lastNameText.setError(null);

        // email validation
        // since emails can be optional in phonebooks the validation takes place
        // only when the email address is provided
        if (!emailText.getEditText().getText().toString().trim().isEmpty() && !isEmailValid(emailText.getEditText().getText().toString())) {
            emailText.setError("Please enter a valid email address.");
            return false;
        } else emailText.setError(null);

        // phone numberText validation
        if (numberText.getEditText().getText().toString().trim().isEmpty() ||       // The phone numberText is empty
                !isNumberValid(numberText.getEditText().getText().toString().trim())) {     // the phone numberText is not valid
            numberText.setError("Please enter a valid numberText.");
            return false;
        } else numberText.setError(null);

        return true;
    }

    /**
     * Focus change listener.
     * @param v - view to which the focus change listener is applied this is passed by the parent class.
     * @param hasFocus - Two events are triggered when the field has focus and another when the field has lost focus.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) validate();
    }

    /**
     * OnClickListener for save button.
     * @param v - View passed by the parent class.
     */
    public void onSave(View v) {
        // validate input and if valided continue else display error
        if (validate()) {
            // everything is valid at this phase
            // save to database
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            dbHelper.insertContact(
                    firstNameText.getEditText().getText().toString(),
                    lastNameText.getEditText().getText().toString(),
                    emailText.getEditText().getText().toString(),
                    numberText.getEditText().getText().toString(), Integer.toString(groupId));
            // return the result was ok to the previous activity
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    /**
     * OnClickListener for discard button.
     * @param v - View passed by the parent class.
     */
    public void onDiscard(View v) {
        // return the result was cancelled to the previous activity
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adding menu to the action bar
        getMenuInflater().inflate(R.menu.add_contact_menu, menu);
        return true;
    }
}
