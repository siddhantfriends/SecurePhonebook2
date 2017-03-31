package uk.ac.northumbria.securephonebook;

import android.provider.BaseColumns;

/**
 * Created by Siddhant on 30/03/2017.
 */

final class DBContract {

    /**
     * This class is a contract for the database. In order to prevent someone
     * from accidently instantiating the contract class, the constructor has
     * been made private.
     */
    private DBContract() {}

    static class ContactContract implements BaseColumns {
        static final String TABLE_NAME = "contacts";
        static final String COLUMN_FIRST_NAME = "first_name";
        static final String COLUMN_LAST_NAME = "last_name";
        static final String COLUMN_EMAIL = "email";
        static final String COLUMN_NUMBER = "number";

    }

}
