package uk.ac.northumbria.securephonebook;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Siddhant on 29/03/2017.
 */

public class RSAHelper {
    private Context context;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Cipher cipher;
    private byte[] plainText;
    private byte[] cipherText;


    public RSAHelper(Context context) {
        // initializing
        this.context = context;

        // generate keys
        this.generateKeyPair();

        // initialize cipher
        this.initializeCipher();



    }

    /**
     * Generates KeyPair
     */
    private void generateKeyPair() {
        KeyPairGenerator keyPairGenerator = null;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance(context.getString(R.string.encryption_algorithm));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (keyPairGenerator != null) {
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        }

        // write the keys to the external storage
        this.writeKeysToStorage();
    }

    /**
     * Initializes Cipher
     */
    private void initializeCipher() {
        this.cipher = null;
        try {
            this.cipher = Cipher.getInstance(context.getString(R.string.encryption_algorithm));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the keys to external storage device
     */
    private void writeKeysToStorage() {
        FileOutputStream outputStream;
        try {
            // writing the public key
            outputStream = context.openFileOutput(context.getString(R.string.public_key_filename), Context.MODE_PRIVATE);
            outputStream.write(publicKey.getEncoded());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // writing the private key
            outputStream = context.openFileOutput(context.getString(R.string.private_key_filename), Context.MODE_PRIVATE);
            outputStream.write(privateKey.getEncoded());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts the text
     */
    public void onEncrypt() {
        // method variables
        PublicKey pubKey = null;
        FileInputStream fileIn = null;

        try {
            // fetching the public key
            fileIn = new FileInputStream(context.getFilesDir() + File.separator + context.getString(R.string.public_key_filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // if the public key exists
        if (fileIn != null) {
            byte[] data = null;
            try {
                // read the key into byte array
                data = new byte[fileIn.available()];
                fileIn.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // if the public key has data
            if (data != null) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
                KeyFactory keyFactory = null;
                try {
                    // get an instance of the KeyFactory
                    keyFactory = KeyFactory.getInstance(context.getString(R.string.encryption_algorithm));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                // if the KeyFactory is successfully generated
                if (keyFactory != null) {
                    try {
                        // generate the public key
                        pubKey = keyFactory.generatePublic(keySpec);
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Decrypts the data
     */
    public void onDecrypt() {
        PrivateKey priKey = null;
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(context.getFilesDir() + File.separator + context.getString(R.string.private_key_filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileIn != null) {
            byte[] data = null;
            // read the key into byte array
            try {
                data = new byte[fileIn.available()];
                fileIn.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (data != null) {
                // create the key from the byte array
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
                KeyFactory keyFactory = null;
                try {
                    keyFactory = KeyFactory.getInstance(context.getString(R.string.encryption_algorithm));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (keyFactory != null) {
                    try {
                        priKey = keyFactory.generatePrivate(keySpec);
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
