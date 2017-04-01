package uk.ac.northumbria.securephonebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pub.devrel.easypermissions.EasyPermissions;

public class ViewContactActivity extends AppCompatActivity implements Constants {
    TextView firstNameText;         // First name of the contact
    TextView lastNameText;          // Last name of the contact
    TextView emailText;             // Email of the contact
    TextView numberText;                // Phone numberText of the contact
    TextView contactGroupText;          // Group the contact is associated with

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        // initializing
        firstNameText = (TextView) findViewById(R.id.firstNameText);
        lastNameText = (TextView) findViewById(R.id.lastNameText);
        emailText = (TextView) findViewById(R.id.emailText);
        numberText = (TextView) findViewById(R.id.numberText);
        contactGroupText = (TextView) findViewById(R.id.contactGroupText);

        // displaying data
        Intent intent = getIntent();
        firstNameText.setText(intent.getStringExtra("first_name"));
        lastNameText.setText(intent.getStringExtra("last_name"));
        emailText.setText(intent.getStringExtra("email"));
        numberText.setText(intent.getStringExtra("number"));
        contactGroupText.setText(intent.getStringExtra("group_name"));
    }

    public void onCall(View v) {
        String[] permissions = {Manifest.permission.CALL_PHONE};
        // if permissions are not granted request them
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "You have requested to call. " +
                    "Kindly accept the permissions.", RC_LOCATION_CONTACTS_PERM, permissions);
        }

        // verifiying that the user has granted permissions
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberText.getText().toString()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
}
