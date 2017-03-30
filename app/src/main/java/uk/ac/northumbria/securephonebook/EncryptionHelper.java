package uk.ac.northumbria.securephonebook;

import android.content.Context;
import android.os.Environment;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Siddhant on 29/03/2017.
 */

public class EncryptionHelper implements Constants {
    private Context context;
    protected SecretKey secretKey;

    /**
     * Constructor helps to generate a secret key and stores it on external storage.
     * @param context - App context. This is used for File I/O.
     */
    public EncryptionHelper(Context context) {
        // initializing
        this.context = context;

        if (secretKeyExists(SECRET_KEY_FILENAME)) {
            secretKey = readSecretKey(SECRET_KEY_FILENAME);
        } else {
            secretKey = generateSecretKey();
            storeSecretKey(secretKey, SECRET_KEY_FILENAME);
        }
    }

    /**
     * Checks whether the secret.key file exists.
     * @return - returns true if the file exists else returns false.
     */
    protected boolean secretKeyExists(String filename) {
        File file = context.getFileStreamPath(filename);
        return file.exists() && file.isFile();
    }


    /**
     * Generates an instance of SecretKey.
     * @return - returns the generated SecretKey, if something is wrong null is returned.
     */
    protected SecretKey generateSecretKey() {
        SecretKey secretKey = null;
        KeyGenerator keyGenerator = null;
        try {
            // get the instance of the keygenerator with the specified algorithm
            keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // if the generator instance is returned successfully
        if (keyGenerator != null) {
            keyGenerator.init(ENCRYPTION_KEY_SIZE);
            secretKey = keyGenerator.generateKey();
        }

        return secretKey;
    }

    /**
     * Writing the secret key to external storage.
     * @param secretKey - Secret key used for encryption and decryption.
     * @param filename - Filename in which the key will be stored.
     */
    protected void storeSecretKey(SecretKey secretKey, String filename) {
        FileOutputStream outputStream;
        try {
            // writing the secret key to the external storage.
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(secretKey.getEncoded());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the filename for secret.key and returns the instance of SecretKey.
     * @param filename - The filename for the secret.key stored in the files directory.
     * @return - SecretKey instance for the given secret.key file.
     */
    protected  SecretKey readSecretKey(String filename) {
        SecretKey secretKey = null;
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(context.getFilesDir() + File.separator + filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileIn != null) {
            byte[] data = null;

            try {
                data = new byte[fileIn.available()];
                fileIn.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (data != null) {
                // Create the key from the byte array
                secretKey = new SecretKeySpec(data, 0, data.length, ENCRYPTION_ALGORITHM);
            }
        }
        return secretKey;
    }

    /**
     * Returns an instance of cipher to encrypt or decrypt using the secret key.
     * @param mode - Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE should be provided.
     * @param secretKey - Secret key used for encryption or decryption.
     * @return - Returns an instance of Cipher for encrypting or decrypting as supplied by the mode parameter.
     */
    protected Cipher getInstanceOfCipher(int mode, SecretKey secretKey) {
        // getting the instance of cipher
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        // init cipher to either encrypt or decrypt mode
        try {
            cipher.init(mode, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return cipher;
    }

    /**
     * Encrypts the given text and returns the cipher text in a byte[] format. Provided the secret key.
     * @param text - The text to be encrypted in byte[] format.
     * @param secretKey - Secret key required for encryption.
     * @return - Cipher text in byte[] format or null if something went wrong.
     */
    protected byte[] onEcrypt(byte[] text, SecretKey secretKey) {
        Cipher cipher = getInstanceOfCipher(Cipher.ENCRYPT_MODE, secretKey);

        if (cipher != null) {
            // encryption
            byte[] cipherText = null;
            try {
                cipherText = cipher.doFinal(text);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }

            // returning the encrypted cypher text in string format
            return cipherText;
        }

        // if cipher is null default fallback to return null
        return null;
    }

    /**
     * Decrypts the given cipher text to a text in byte[] format. Provided the secret key.
     * @param cipherText - Cipher text to be decrypted in byte[] format.
     * @param secretKey - The scret key required for decryption.
     * @return - Decrypted text in byte[] format or null if something went wrong.
     */
    protected  byte[] onDecrypt(byte[] cipherText, SecretKey secretKey) {
        Cipher cipher = getInstanceOfCipher(Cipher.DECRYPT_MODE, secretKey);

        if (cipher != null) {
            // decryption
            byte[] plainText = null;
            try {
                plainText = cipher.doFinal(cipherText);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }

            // returning the plain text in string format
            return plainText;
        }

        // if cipher is null fallback to return null
        return null;
    }


}
