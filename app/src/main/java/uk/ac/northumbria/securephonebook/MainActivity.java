package uk.ac.northumbria.securephonebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    byte[] plainText;
    byte[] cipherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText) findViewById(R.id.editText);
        final Button encryptButton = (Button) findViewById(R.id.encryptButton);
        Button decryptButton = (Button) findViewById(R.id.decryptButton);

        final EncryptionHelper encryptionHelper = new EncryptionHelper(getApplicationContext());

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cipherText = null;
                String plainText = editText.getText().toString();
                cipherText = encryptionHelper.onEcrypt(plainText.getBytes(), encryptionHelper.secretKey);
                editText.setText(new String(cipherText));
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plainText = null;
                plainText = encryptionHelper.onDecrypt(cipherText, encryptionHelper.secretKey);
                editText.setText(new String(plainText));
            }
        });
    }
}
