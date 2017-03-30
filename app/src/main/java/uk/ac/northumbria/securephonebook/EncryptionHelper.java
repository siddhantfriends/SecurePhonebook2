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

/**
 * Created by Siddhant on 29/03/2017.
 */

public class EncryptionHelper implements Constants {
    private Context context;
    protected SecretKey secretKey;
    private Cipher cipher;

    /**
     * Constructor helps to generate a secret key and stores it on external storage.
     * @param context - App context. This is used for File I/O.
     */
    public EncryptionHelper(Context context) {
        // initializing
        this.context = context;

        // generating the secret key
        generateSecretKey();

        // write the secret key to external storage device
        
    }

    protected void generateSecretKey() {
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

        // getting the instance of cipher
        cipher = null;
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
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
     * Encrypts the given text and returns the cipher text in a byte[] format. Provided the secret key.
     * @param text - The text to be encrypted in byte[] format.
     * @param secretKey - Secret key required for encryption.
     * @return - Cipher text in byte[] format or null if something went wrong.
     */
    protected byte[] onEcrypt(byte[] text, SecretKey secretKey) {
        // init cipher to encrypt mode
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

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
        // init cipher to decrypt mode
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

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
