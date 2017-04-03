package uk.ac.northumbria.securephonebook.schemas;

import android.provider.BaseColumns;



public final class DBContract {

    /**
     * This class is a contract for the database. In order to prevent someone
     * from accidently instantiating the contract class, the constructor has
     * been made private.
     */
    private DBContract() {}

    public static class ContactContract implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_GROUP_ID = "group_id";
    }

    public static class GroupContract implements  BaseColumns {
        public static final String TABLE_NAME = "groups";
        public static final String COLUMN_GROUP_NAME = "group_name";
    }

}
