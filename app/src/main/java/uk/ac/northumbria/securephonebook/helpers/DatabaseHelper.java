package uk.ac.northumbria.securephonebook.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.models.Contact;
import uk.ac.northumbria.securephonebook.schemas.DBContract;

/**
 * Created by Siddhant on 30/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database version requires to increment if the schema is changed.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SecurePhonebook.db";   // Name of the database

    // Query for creating the table
    private static final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + DBContract.ContactContract.TABLE_NAME + " (" +
                    DBContract.ContactContract._ID + " INTEGER PRIMARY KEY," +
                    DBContract.ContactContract.COLUMN_FIRST_NAME + " BLOB," +
                    DBContract.ContactContract.COLUMN_LAST_NAME + " BLOB," +
                    DBContract.ContactContract.COLUMN_EMAIL + " BLOB," +
                    DBContract.ContactContract.COLUMN_NUMBER + " BLOB)";
    // Query for deleting the table
    private static final String SQL_DELETE_CONTACTS_TABLE =
            "DROP TABLE IF EXISTS " + DBContract.ContactContract.TABLE_NAME;

    // All columns
    private static final String[] allColumns = {
            DBContract.ContactContract._ID,
            DBContract.ContactContract.COLUMN_FIRST_NAME,
            DBContract.ContactContract.COLUMN_LAST_NAME,
            DBContract.ContactContract.COLUMN_EMAIL,
            DBContract.ContactContract.COLUMN_NUMBER
    };

    /**
     * Default constructor that takes in application context.
     * @param context - Application context.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.findDataOrInsertData();
    }

    /**
     * Overriding onCreate.
     * @param db - SqliteDatabase instance passed by parent.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
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
        onCreate(db);
    }

    /**
     * Inserts a contact into database.
     * @param firstName - First name of the contact.
     * @param lastName - Last name of the contact.
     * @param email - Email of the contact.
     * @param number - Number of the contact.
     */
    public void insertContact(String firstName, String lastName, String email, String number) {
        // Get the database in writable mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Mapping the new values with the column names being the keys.
        ContentValues values = new ContentValues();
        values.put(DBContract.ContactContract.COLUMN_FIRST_NAME, firstName.getBytes());
        values.put(DBContract.ContactContract.COLUMN_LAST_NAME, lastName.getBytes());
        values.put(DBContract.ContactContract.COLUMN_EMAIL, email.getBytes());
        values.put(DBContract.ContactContract.COLUMN_NUMBER, number.getBytes());

        // Inserting the record into database
        db.insert(DBContract.ContactContract.TABLE_NAME, null, values);
    }

    /**
     * Retrieves all the contacts in ArrayList<Contact> format.
     * @return - returns ArrayList of Contact.
     */
    public ArrayList<Contact> retrieveAllContacts() {
        // getting readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // retrieve all records
        Cursor cursor = db.query(DBContract.ContactContract.TABLE_NAME, allColumns, null, null, null, null, DBContract.ContactContract._ID);
        int[] columnIndices = {
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_FIRST_NAME),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_LAST_NAME),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_EMAIL),
                cursor.getColumnIndexOrThrow(DBContract.ContactContract.COLUMN_NUMBER)
        };

        ArrayList<Contact> contacts = new ArrayList<>();

        // Retrieves all the records and adds them to ArrayList
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Contact contact =  new Contact(new String(cursor.getBlob(columnIndices[0])),
                        new String(cursor.getBlob(columnIndices[1])),
                        new String(cursor.getBlob(columnIndices[2])),
                        new String(cursor.getBlob(columnIndices[3])));

                contacts.add(contact);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return contacts;
    }

    /**
     * Try's to find existing data in the contacts table. If it does not exists then the data is
     * added to the table.
     */
    private void findDataOrInsertData() {
        ArrayList<Contact> contacts = this.retrieveAllContacts();
        // if the database does not have data then insert data
        if (contacts.size() < 1) {
            // This is total of 10 contacts
            this.insertContact("George", "Roberts", "GeorgeRoberts@fleckens.hu", "07927275337");
            this.insertContact("Nathan", "James", "NathanJames@rhyta.com", "07062771217");
            this.insertContact("Harry", "Kerr", "HarryKerr@gustr.com", "07907559378");
            this.insertContact("Charlie", "Carr", "CharlieCarr@jourrapide.com", "07740979188");
            this.insertContact("Samantha", "Barnes", "SamanthaBarnes@dayrep.com", "07841820917");
            this.insertContact("Archie", "Peacock", "ArchiePeacock@rhyta.com", "07700966432");
            this.insertContact("Elise", "Dodd", "EliseDodd@jourrapide.com", "07719206484");
            this.insertContact("Kai", "Jarvis", "KaiJarvis@einrot.com", "07841552090");
            this.insertContact("Brandon", "Bryant", "BrandonBryant@armyspy.com", "07081320441");
            this.insertContact("James", "Metcalfe", "JamesMetcalfe@dayrep.com", "07975429676");
        }
    }



}
