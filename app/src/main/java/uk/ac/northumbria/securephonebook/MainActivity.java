package uk.ac.northumbria.securephonebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.helpers.DatabaseHelper;
import uk.ac.northumbria.securephonebook.models.Contact;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView contactsListView = (ListView) findViewById(R.id.contactsListView);

        db = new DatabaseHelper(getApplicationContext());

        ArrayList<Contact> contacts = db.retrieveAllContacts();

        contactsListView.setAdapter(new ContentAdapter(getApplicationContext(), contacts));

    }
}
