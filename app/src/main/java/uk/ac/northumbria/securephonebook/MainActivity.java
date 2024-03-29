package uk.ac.northumbria.securephonebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.helpers.DatabaseHelper;
import uk.ac.northumbria.securephonebook.models.Contact;
import uk.ac.northumbria.securephonebook.models.Group;

public class MainActivity extends AppCompatActivity implements Constants {
    Toolbar toolbar;            // toolbar
    ListView contactsListView;  // list of all the contacts
    SearchView searchView;      // search view for searching
    DatabaseHelper db;          // instance of DatabaseHelper
    ArrayList<Contact> contacts;    // ArrayList of Contact
    ArrayList<Group> groups;        // ArrayList of Group
    Spinner groupSpinner;           // Groups list
    int selectedGroupId = -1;       // the default group id is -1 which is All Contacts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        // initializing
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        searchView = (SearchView) findViewById(R.id.searchView);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinner);
        db = new DatabaseHelper(getApplicationContext());
        contacts = db.getAllContacts();
        groups = db.getAllGroups();

        // Populating group spinner
        ArrayList<Group> displayGroup = db.getAllGroups();
        displayGroup.add(0, new Group(-1, "Contact Group - (All Contacts)"));
        GroupAdapter groupsAdapter = new GroupAdapter(this, android.R.layout.simple_spinner_dropdown_item, displayGroup);

        groupSpinner.setAdapter(groupsAdapter);

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
                intent.putExtra("group_name", db.getGroupNameById(groups, contact.getGroupId()));
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateQuery(query, selectedGroupId);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateQuery(newText, selectedGroupId);
                return true;
            }
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group) parent.getItemAtPosition(position);
                selectedGroupId = group.getId();
                if (selectedGroupId != -1) {
                    ArrayList<Contact> selectedContacts = db.getContactsByGroup(contacts, Integer.toString(selectedGroupId));
                    updateContactsView(selectedContacts);
                } else {
                    updateContactsView(contacts);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateContactsView(contacts);
            }
        });

        updateContactsView(contacts);

        searchView.clearFocus();
    }

    private void updateContactsView(ArrayList<Contact> contacts) {
        contactsListView.setAdapter(new ContactAdapter(getApplicationContext(), contacts));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking if the result is from the add contact activity
        if (requestCode == ADD_CONTACT_ACTIVITY_REQUEST) {
            // checking of the request was successful
            if (resultCode == RESULT_OK) {
                // refresh the adapter
                contacts = db.getAllContacts();
                updateContactsView(contacts);

                // display success message
                Toast.makeText(getApplicationContext(), "The contact was saved successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateQuery(String query, int selectedGroupId) {

        if (selectedGroupId == -1) {
            if (query.trim().isEmpty()) {
                updateContactsView(contacts);
            } else {
                updateContactsView(db.getContactsSearchResult(contacts, query.trim().toLowerCase()));
            }
        } else {
            ArrayList<Contact> selectedContacts = db.getContactsByGroup(contacts, Integer.toString(selectedGroupId));
            if (query.trim().isEmpty()) {
                updateContactsView(selectedContacts);
            } else {
                updateContactsView(db.getContactsSearchResult(selectedContacts, query.trim().toLowerCase()));
            }
        }


    }

    public void onAddContact(View v) {
        Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
        startActivityForResult(intent, ADD_CONTACT_ACTIVITY_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adding items to the Action Bar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.openContactMenu) {
            // logic for open contact
            Intent intent = new Intent(this, OpenReceivedContactActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
