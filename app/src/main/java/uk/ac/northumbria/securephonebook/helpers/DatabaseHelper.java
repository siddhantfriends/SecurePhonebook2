package uk.ac.northumbria.securephonebook.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.models.Contact;
import uk.ac.northumbria.securephonebook.models.Group;
import uk.ac.northumbria.securephonebook.schemas.DBContract;



public class DatabaseHelper extends SQLiteOpenHelper {

    // Database version requires to increment if the schema is changed.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SecurePhonebook.db";   // Name of the database
    private EncryptionHelper edHelper;

    // SQL Query for creating contacts table
    private static final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + DBContract.ContactContract.TABLE_NAME + " (" +
                    DBContract.ContactContract._ID + " INTEGER PRIMARY KEY," +
                    DBContract.ContactContract.COLUMN_FIRST_NAME + " BLOB," +
                    DBContract.ContactContract.COLUMN_LAST_NAME + " BLOB," +
                    DBContract.ContactContract.COLUMN_EMAIL + " BLOB," +
                    DBContract.ContactContract.COLUMN_NUMBER + " BLOB," +
                    DBContract.ContactContract.COLUMN_GROUP_ID + " BLOB)";
    // SQL Query for deleting contacts table
    private static final String SQL_DELETE_CONTACTS_TABLE =
            "DROP TABLE IF EXISTS " + DBContract.ContactContract.TABLE_NAME;
    // SQL Query for creating groups table
    private static final String SQL_CREATE_GROUPS_TABLE =
            "CREATE TABLE " + DBContract.GroupContract.TABLE_NAME + " (" +
                    DBContract.GroupContract._ID + " INTEGER PRIMARY KEY," +
                    DBContract.GroupContract.COLUMN_GROUP_NAME + " BLOB)";
    // SQL Query for deleting groups table
    private static final String SQL_DELETE_GROUPS_TABLE =
            "DROP TABLE IF EXISTS " + DBContract.GroupContract.TABLE_NAME;

    // All columns of contacts table
    private static final String[] allColumnsContactsTable = {
            DBContract.ContactContract._ID,
            DBContract.ContactContract.COLUMN_FIRST_NAME,
            DBContract.ContactContract.COLUMN_LAST_NAME,
            DBContract.ContactContract.COLUMN_EMAIL,
            DBContract.ContactContract.COLUMN_NUMBER,
            DBContract.ContactContract.COLUMN_GROUP_ID
    };
    // All columns of groups table
    private  static final String[] allColumnsGroupsTable = {
            DBContract.GroupContract._ID,
            DBContract.GroupContract.COLUMN_GROUP_NAME
    };


    /**
     * Default constructor that takes in application context.
     * @param context - Application context.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        edHelper = new EncryptionHelper(context);
        this.findDataOrInsertData();
    }

    /**
     * Overriding onCreate.
     * @param db - SqliteDatabase instance passed by parent.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
        db.execSQL(SQL_CREATE_GROUPS_TABLE);
    }

    /**
     * Overriding onUpgrade method. To delete the table and recreate.
     * @param db - SqliteDatabase passed by parent.
     * @param oldVersion - int old version passed by the parent.
     * @param newVersion - int new version passed by the parent.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONTACTS_TABLE);
        db.execSQL(SQL_DELETE_GROUPS_TABLE);
        onCreate(db);
    }

    /**
     * Inserts a contact into database.
     * @param firstName - First name of the contact.
     * @param lastName - Last name of the contact.
     * @param email - Email of the contact.
     * @param number - Number of the contact.
     */
    public void insertContact(String firstName, String lastName, String email, String number, String groupId) {
        // Get the database in writable mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Mapping the new values with the column names being the keys.
        // encrypting the contact details while adding them to the database.
        ContentValues values = new ContentValues();
        values.put(DBContract.ContactContract.COLUMN_FIRST_NAME,
                edHelper.onEncrypt(firstName.getBytes(), edHelper.secretKey));
        values.put(DBContract.ContactContract.COLUMN_LAST_NAME,
                edHelper.onEncrypt(lastName.getBytes(), edHelper.secretKey));
        values.put(DBContract.ContactContract.COLUMN_EMAIL,
                edHelper.onEncrypt(email.getBytes(), edHelper.secretKey));
        values.put(DBContract.ContactContract.COLUMN_NUMBER,
                edHelper.onEncrypt(number.getBytes(), edHelper.secretKey));
        values.put(DBContract.ContactContract.COLUMN_GROUP_ID,
                edHelper.onEncrypt(groupId.getBytes(), edHelper.secretKey));

        // Inserting the record into database
        db.insert(DBContract.ContactContract.TABLE_NAME, null, values);
    }

    /**
     * Insert a new group into the database.
     * @param groupName - The name of the group.
     */
    public void insertGroup(String groupName) {
        // Get the database in writable mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Mapping the new values with the column names being the keys.
        // encrypting the group details while adding them to the database.
        ContentValues values = new ContentValues();
        values.put(DBContract.GroupContract.COLUMN_GROUP_NAME,
                edHelper.onEncrypt(groupName.getBytes(), edHelper.secretKey));;

        // Inserting the record into database
        db.insert(DBContract.GroupContract.TABLE_NAME, null, values);
    }

    /**
     * Retrieves all the contacts in ArrayList<Contact> format.
     * @return - returns ArrayList of Contact.
     */
    public ArrayList<Contact> getAllContacts() {
        // getting readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // retrieve all records
        Cursor cursor = db.query(DBContract.ContactContract.TABLE_NAME, allColumnsContactsTable, null, null, null, null, DBContract.ContactContract._ID);
        int[] columnIndices = {
                cursor.getColumnIndexOrThrow(DBContract.ContactContract._ID),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_FIRST_NAME),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_LAST_NAME),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_EMAIL),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_NUMBER),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_GROUP_ID)
        };

        ArrayList<Contact> contacts = new ArrayList<>();

        // Retrieves all the records and adds them to ArrayList
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // reading cipher text from database and converting to plain text byte array
                // in other words, decrypting the contacts while retrieving them.
                int id = cursor.getInt(columnIndices[0]);
                byte[] firstName = edHelper.onDecrypt(cursor.getBlob(columnIndices[1]), edHelper.secretKey);
                byte[] lastName = edHelper.onDecrypt(cursor.getBlob(columnIndices[2]), edHelper.secretKey);
                byte[] email = edHelper.onDecrypt(cursor.getBlob(columnIndices[3]), edHelper.secretKey);
                byte[] number = edHelper.onDecrypt(cursor.getBlob(columnIndices[4]), edHelper.secretKey);
                byte[] groupId = edHelper.onDecrypt(cursor.getBlob(columnIndices[5]), edHelper.secretKey);

                Contact contact =  new Contact(id, new String(firstName), new String(lastName), new String(email), new String(number), new String(groupId));

                contacts.add(contact);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return contacts;
    }

    /**
     * Returns all the groups from the database.
     * @return - Retrieves the groups from the database in ArrayList<Group> format.
     */
    public ArrayList<Group> getAllGroups() {
        // getting readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // retrieve all records
        Cursor cursor = db.query(DBContract.GroupContract.TABLE_NAME, allColumnsGroupsTable, null, null, null, null, DBContract.GroupContract._ID);
        int[] columnIndices = {
                cursor.getColumnIndexOrThrow(DBContract.GroupContract._ID),
                cursor.getColumnIndexOrThrow(DBContract.GroupContract.COLUMN_GROUP_NAME)
        };

        ArrayList<Group> groups = new ArrayList<>();

        // Retrieves all the records and adds them to ArrayList
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // reading cipher text from database and converting to plain text byte array
                // in other words, decrypting the contacts while retrieving them.
                int id = cursor.getInt(columnIndices[0]);
                byte[] groupName = edHelper.onDecrypt(cursor.getBlob(columnIndices[1]), edHelper.secretKey);

                Group group = new Group(id, new String(groupName));

                groups.add(group);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return groups;
    }


    /**
     * Try's to find existing data in the contacts table. If it does not exists then the data is
     * added to the table.
     */
    private void findDataOrInsertData() {
        ArrayList<Group> groups = this.getAllGroups();
        // if the database does not have data then insert data
        if (groups.size() < 1) {
            // Inserting 3 groups to the database
            this.insertGroup("Home");
            this.insertGroup("Work");
            this.insertGroup("Gym");
        }


        ArrayList<Contact> contacts = this.getAllContacts();
        // if the database does not have data then insert data
        if (contacts.size() < 1) {
            // This is total of 10 contacts
            this.insertContact("George", "Roberts", "GeorgeRoberts@fleckens.hu", "07927275337", "3");
            this.insertContact("Nathan", "James", "NathanJames@rhyta.com", "07062771217", "2");
            this.insertContact("Harry", "Kerr", "HarryKerr@gustr.com", "07907559378", "1");
            this.insertContact("Charlie", "Carr", "CharlieCarr@jourrapide.com", "07740979188", "1");
            this.insertContact("Samantha", "Barnes", "SamanthaBarnes@dayrep.com", "07841820917", "1");
            this.insertContact("Archie", "Peacock", "ArchiePeacock@rhyta.com", "07700966432", "2");
            this.insertContact("Elise", "Dodd", "EliseDodd@jourrapide.com", "07719206484", "3");
            this.insertContact("Kai", "Jarvis", "KaiJarvis@einrot.com", "07841552090", "3");
            this.insertContact("Brandon", "Bryant", "BrandonBryant@armyspy.com", "07081320441", "1");
            this.insertContact("James", "Metcalfe", "JamesMetcalfe@dayrep.com", "07975429676", "2");
        }
    }

    /**
     * Searches over all the contacts for first name, last name, email.
     * The matches are returned in ArrayList<Contact>
     * @param contacts - ArrayList of Contact.
     * @param search - The text to search.
     * @return - Returns ArrayList<Contact> of the matched results.
     */
    public ArrayList<Contact> getContactsSearchResult(ArrayList<Contact> contacts, String search) {
        ArrayList<Contact> searchResults = new ArrayList<>();

        for (Contact contact : contacts) {
            if (contact.findInContact(search)) {
                searchResults.add(contact);
            }
        }

        return searchResults;
    }

    /**
     * returns contacts by the group name.
     * @param contacts
     * @param groupId
     * @return
     */
    public ArrayList<Contact> getContactsByGroup(ArrayList<Contact> contacts, String groupId) {
        ArrayList<Contact> selectedContacts = new ArrayList<>();

        for(Contact contact: contacts) {
            if (contact.getGroupId().contains(groupId)) selectedContacts.add(contact);
        }

        return selectedContacts;
    }


    /**
     * Returns the name of the group if the id matches.
     * @param groups - ArrayList of Group.
     * @param id - Id of the group.
     * @return - Returns the name of the group.
     */
    public String getGroupNameById(ArrayList<Group> groups, String id) {
        for (Group group : groups) {
            if (group.hasId(id))  {
                return group.getGroupName();
            }
        }
        return null;
    }



}
