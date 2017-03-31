package uk.ac.northumbria.securephonebook;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import uk.ac.northumbria.securephonebook.helpers.DatabaseHelper;

public class AddContactActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    TextInputLayout firstNameText;  // first name of the contact
    TextInputLayout lastNameText;   // last name of the contact
    TextInputLayout emailText;      // email of the contact
    TextInputLayout numberText;     // number of the contact
    Button saveButton;              // save button
    Button discardButton;           // discard bbutton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // initializing variables
        firstNameText = (TextInputLayout) findViewById(R.id.firstNameText);
        lastNameText = (TextInputLayout) findViewById(R.id.lastNameText);
        emailText = (TextInputLayout) findViewById(R.id.emailText);
        numberText = (TextInputLayout) findViewById(R.id.numberText);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button discardButton = (Button) findViewById(R.id.discardButton);

        // setting listeners for interactivity (error validation)
        firstNameText.getEditText().setOnFocusChangeListener(this);
        lastNameText.getEditText().setOnFocusChangeListener(this);
        emailText.getEditText().setOnFocusChangeListener(this);
        numberText.getEditText().setOnFocusChangeListener(this);


        // save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate input and if valided continue else display error
                if (validate()) {
                    // everything is valid at this phase
                    // save to database
                    DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                    dbHelper.insertContact(
                            firstNameText.getEditText().getText().toString(),
                            lastNameText.getEditText().getText().toString(),
                            emailText.getEditText().getText().toString(),
                            numberText.getEditText().getText().toString());
                    // return the result was ok to the previous activity
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

        // discard button
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return the result was cancelled to the previous activity
                setResult(Activity.RESULT_CANCELED);
                finish();
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
     * Validation for the number.
     * @param number - Number to be validated.
     * @return - true if the number is valid else false.
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

        // phone number validation
        if (numberText.getEditText().getText().toString().trim().isEmpty() ||       // The phone number is empty
                !isNumberValid(numberText.getEditText().getText().toString().trim())) {     // the phone number is not valid
            numberText.setError("Please enter a valid number.");
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
}
