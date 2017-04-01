package uk.ac.northumbria.securephonebook;

/**
 * Created by Siddhant on 30/03/2017.
 */

public interface Constants {
    static final String ENCRYPTION_ALGORITHM  = "AES";
    static final int ENCRYPTION_KEY_SIZE = 256;
    static final String SECRET_KEY_FILENAME = "secret.key";

    static final int ADD_CONTACT_ACTIVITY_REQUEST = 1;
    static final int RC_LOCATION_CONTACTS_PERM = 2;

    static final String EXPORT_CONTACT_FILENAME = "contact.spb";
}
