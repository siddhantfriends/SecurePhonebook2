package uk.ac.northumbria.securephonebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import uk.ac.northumbria.securephonebook.helpers.ExportHelper;
import uk.ac.northumbria.securephonebook.models.Contact;

public class EmailContactActivity extends AppCompatActivity implements View.OnFocusChangeListener, Constants {
    Toolbar toolbar;            // toolbar
    Button sendEmail;           // send button
    TextInputLayout emailText;  // email address
    Contact contact;            // contact
    ExportHelper exportHelper;  // export helper for exporting the contact

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_contact);
        toolbar = (Toolbar) findViewById(R.id.emailContactToolbar);
        setSupportActionBar(toolbar);

        // initialize
        sendEmail = (Button) findViewById(R.id.sendEmailButton);
        emailText = (TextInputLayout) findViewById(R.id.emailText);
        emailText.getEditText().setOnFocusChangeListener(this); // for validation
        exportHelper = new ExportHelper(getApplicationContext());

        // get the bundle and create a contact ready for creating a file when required
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("contact_bundle");
        contact = new Contact(-1,
                bundle.getString("first_name"),
                bundle.getString("last_name"),
                bundle.getString("email"),
                bundle.getString("number"),
                null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // apply the menu
        getMenuInflater().inflate(R.menu.email_contact_menu, menu);
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) validate();
    }

    /**
     * Validates email before sending to the next view.
     * @return - Returns true if valid else false.
     */
    private boolean validate() {
        if (emailText.getEditText().getText().toString().trim().isEmpty() ||
                !isEmailValid(emailText.getEditText().getText().toString())) {
            emailText.setError("Please enter a valid email address.");
            return false;
        } else emailText.setError(null);

        return true;
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
     * OnClickListener for send button
     * @param v
     */
    public void sendEmail(View v) {
        if (validate()) {
            // Create the encrypted contact file
            exportHelper.onSave(contact);

            // create an email
            File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), EXPORT_CONTACT_FILENAME);
            Uri path = Uri.fromFile(filelocation);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{ emailText.getEditText().getText().toString() });
            i.putExtra(Intent.EXTRA_SUBJECT, "Secure Phonebook Contact");
            i.putExtra(Intent.EXTRA_TEXT, "Hi,\n\nYou have received a Secure Phonebook Contact. " +
                    "To view the contact please install the app and open contact using the app. " +
                    "Note that you will require to securely receive the secret key for decryption." +
                    "\n\nRegards\nTeam Secure Phonebook");
            i.putExtra(Intent.EXTRA_STREAM, path);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
