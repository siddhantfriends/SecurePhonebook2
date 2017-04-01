package uk.ac.northumbria.securephonebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import pub.devrel.easypermissions.EasyPermissions;

public class ViewContactActivity extends AppCompatActivity implements Constants {
    Toolbar toolbar;                // toolbar
    TextView firstNameText;         // First name of the contact
    TextView lastNameText;          // Last name of the contact
    TextView emailText;             // Email of the contact
    TextView numberText;                // Phone numberText of the contact
    TextView contactGroupText;          // Group the contact is associated with

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        toolbar = (Toolbar) findViewById(R.id.viewContactToolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adding options to the toolbar
        getMenuInflater().inflate(R.menu.view_contact_menu, menu);

        // Coloring the email icon
        Drawable drawable = menu.findItem(R.id.emailMenu).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.emailMenu).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.emailMenu) {
            // logic for email
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
