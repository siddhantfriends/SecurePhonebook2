package uk.ac.northumbria.securephonebook;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.helpers.DatabaseHelper;
import uk.ac.northumbria.securephonebook.models.Contact;

public class MainActivity extends AppCompatActivity implements Constants {

    ListView contactsListView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsListView = (ListView) findViewById(R.id.contactsListView);
        FloatingActionButton addContactButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
                startActivityForResult(intent, ADD_CONTACT_ACTIVITY_REQUEST);
            }
        });

        updateContactsView();
    }

    private void updateContactsView() {
        db = new DatabaseHelper(getApplicationContext());

        ArrayList<Contact> contacts = db.retrieveAllContacts();

        contactsListView.setAdapter(new ContentAdapter(getApplicationContext(), contacts));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking if the result is from the add contact activity
        if (requestCode == ADD_CONTACT_ACTIVITY_REQUEST) {
            // checking of the request was successful
            if (resultCode == RESULT_OK) {
                // refresh the adapter
                updateContactsView();

                // display success message
                Toast.makeText(getApplicationContext(), "The contact was saved successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
