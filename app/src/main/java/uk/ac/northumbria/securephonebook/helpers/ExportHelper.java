package uk.ac.northumbria.securephonebook.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.ac.northumbria.securephonebook.Constants;
import uk.ac.northumbria.securephonebook.models.Contact;



public class ExportHelper implements Constants {
    private Context context;        // Context of the application

    /**
     * Constructor for Expoert helper.
     * @param context - Context of the application.
     */
    public ExportHelper(Context context) {
        this.context = context;
    }

    /**
     * Encrypts the contact details with the secret.key. Base64 encodes to store it
     * in a JSONObject and saves the file.
     * @param contact - The contact to be encrypted and saved in a file.
     */
    public void onSave(Contact contact) {
        EncryptionHelper edHelper = new EncryptionHelper(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(EXPORT_CONTACT_FILENAME, Context.MODE_PRIVATE);

        // Encrypt the contact data
        byte[] encryptedFirstName = edHelper.onEncrypt(contact.getFirstName().getBytes(), edHelper.secretKey);
        byte[] encryptedLastName = edHelper.onEncrypt(contact.getLastName().getBytes(), edHelper.secretKey);
        byte[] encryptedEmail = edHelper.onEncrypt(contact.getEmail().getBytes(), edHelper.secretKey);
        byte[] encryptedNumber = edHelper.onEncrypt(contact.getNumber().getBytes(), edHelper.secretKey);

        // Base64 encode the data
        byte[] firstName = Base64.encode(encryptedFirstName, Base64.DEFAULT);
        byte[] lastName = Base64.encode(encryptedLastName, Base64.DEFAULT);
        byte[] email = Base64.encode(encryptedEmail, Base64.DEFAULT);
        byte[] number = Base64.encode(encryptedNumber, Base64.DEFAULT);


        // Create JSONObject
        JSONObject jsonContact = new JSONObject();
        try {
            jsonContact.put("first_name", firstName);
            jsonContact.put("last_name", lastName);
            jsonContact.put("email", email);
            jsonContact.put("number", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // save the output file
        FileOutputStream outputStream;
        try {
            // writing the secret key to the external storage.;
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), EXPORT_CONTACT_FILENAME);
            if (file != null) {
                file.delete();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(jsonContact.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    /**
     * Opens the received json file, decodes it from base64, decrypts it using the given keyfile and returns a Contact.
     * @param jsonFile - The json file in which the contact is transferred.
     * @param keyFile - The received key for decryption.
     * @return - Returns the contact if everything is successful else null
     */
    public Contact onOpen(File jsonFile, File keyFile) {
        EncryptionHelper edHelper = new EncryptionHelper(context);
        // read the file
        byte[] contents = null;
        FileInputStream fileIn = null;

        try {
            fileIn = new FileInputStream(jsonFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileIn != null) {
            try {
                contents = new byte[fileIn.available()];
                fileIn.read(contents);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (contents != null) {
            JSONObject jsonContact = null;

            try {
                jsonContact = new JSONObject(new String(contents));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonContact != null) {
                try {
                    // get the base64 from json
                    byte[] encodedFirstName = jsonContact.getString("first_name").getBytes();
                    byte[] encodedLastName = jsonContact.getString("last_name").getBytes();
                    byte[] encodedEmail = jsonContact.getString("email").getBytes();
                    byte[] encodedNumber = jsonContact.getString("number").getBytes();

                    // decode from base64
                    byte[] decodeFirstName = Base64.decode(encodedFirstName, Base64.DEFAULT);
                    byte[] decodedLastName = Base64.decode(encodedLastName, Base64.DEFAULT);
                    byte[] decodedEmail = Base64.decode(encodedEmail, Base64.DEFAULT);
                    byte[] decodedNumber = Base64.decode(encodedNumber, Base64.DEFAULT);

                    // decrypt using the secret.key and return contact
                    edHelper.secretKey = edHelper.readSecretKey(keyFile);
                    Contact contact = new Contact(-1,
                            new String(edHelper.onDecrypt(decodeFirstName, edHelper.secretKey)),
                            new String(edHelper.onDecrypt(decodedLastName, edHelper.secretKey)),
                            new String(edHelper.onDecrypt(decodedEmail, edHelper.secretKey)),
                            new String(edHelper.onDecrypt(decodedNumber, edHelper.secretKey)),
                            null);

                    // return contact
                    return contact;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }


}
