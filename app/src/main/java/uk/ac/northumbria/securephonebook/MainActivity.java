package uk.ac.northumbria.securephonebook;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.helpers.DatabaseHelper;
import uk.ac.northumbria.securephonebook.models.Contact;

public class MainActivity extends AppCompatActivity implements Constants {
    FloatingActionButton addContactButton;
    ListView contactsListView;
    SearchView searchView;
    Button searchButton;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        addContactButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchButton = (Button) findViewById(R.id.searchButton);
        db = new DatabaseHelper(getApplicationContext());

        // setting listeners
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ViewContactActivity.class);
                intent.putExtra("first_name", contact.getFirstName());
                intent.putExtra("last_name", contact.getLastName());
                intent.putExtra("email", contact.getEmail());
                intent.putExtra("number", contact.getNumber());
                startActivity(intent);
            }
        });
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
                startActivityForResult(intent, ADD_CONTACT_ACTIVITY_REQUEST);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateQuery(newText);
                return true;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuery(searchView.getQuery().toString());
            }
        });


        updateContactsView(db.getAllContacts());
    }

    private void updateContactsView(ArrayList<Contact> contacts) {
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
                updateContactsView(db.getAllContacts());

                // display success message
                Toast.makeText(getApplicationContext(), "The contact was saved successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateQuery(String query) {
        if (query.trim().isEmpty()) {
            updateContactsView(db.getAllContacts());
        } else {
            updateContactsView(db.getContactsSearchResult(query.trim().toLowerCase()));
        }
    }
}
