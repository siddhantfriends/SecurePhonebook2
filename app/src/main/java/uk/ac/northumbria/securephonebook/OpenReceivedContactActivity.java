package uk.ac.northumbria.securephonebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.northumbria.securephonebook.helpers.FileServerAsyncTask;
import uk.ac.northumbria.securephonebook.helpers.WiFiDirectBroadcastReceiver;

public class OpenReceivedContactActivity extends AppCompatActivity implements WifiP2pManager.ActionListener, WifiP2pManager.ConnectionInfoListener {
    private Toolbar toolbar;
    private WifiP2pManager manager;
    private Channel channel;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private Spinner spinner;
    private Button findDevicesButton;
    private Button connectButton;
    private Button disconnectButton;
    private boolean isConnected;
    private Button shareKeyButton;
    private TextView shareKeyStatus;
    private static final String TAG = "ORC Activty";
    private WifiP2pInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_received_contact);
        toolbar = (Toolbar) findViewById(R.id.openReceivedContactToolbar);
        setSupportActionBar(toolbar);

        // initializing
        spinner = (Spinner) findViewById(R.id.wifiP2pDeviceListSpinner);
        findDevicesButton = (Button) findViewById(R.id.findDevicesButton);
        connectButton = (Button) findViewById(R.id.connectButton);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        shareKeyButton = (Button) findViewById(R.id.sendKeyButton);
        shareKeyStatus = (TextView) findViewById(R.id.shareStatusText);
        connectButton.setEnabled(false);
        disconnectButton.setVisibility(View.INVISIBLE);
        shareKeyButton.setVisibility(View.INVISIBLE);
        shareKeyStatus.setVisibility(View.INVISIBLE);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        // adding intent filter which is same as the broadcast receiver
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // start the discovery of the peers
        discoverPeers(findDevicesButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adding items to the Action Bar
        getMenuInflater().inflate(R.menu.open_received_contact_menu, menu);
        return true;
    }

    public void discoverPeers(View v) {
        // disable the find button
        manager.discoverPeers(channel, this);
    }

    public void connectToPeer(View v) {
        // if the device is selected
        WifiP2pDevice peer = (WifiP2pDevice) spinner.getSelectedItem();
        if (peer != null) {
            // connect to the device
            broadcastReceiver.connectToPeer(peer, this);
        }
    }

    public void connectionChanged(NetworkInfo.State state, boolean isGroupOwner, Collection<WifiP2pDevice> deviceList, WifiP2pDevice owner) {
        switch (state) {
            case CONNECTED:
                isConnected = true;
                Toast.makeText(this, "Connection Established", Toast.LENGTH_SHORT).show();
                manager.requestConnectionInfo(channel, this);
                // connected successfully
                findDevicesButton.setEnabled(false);
                connectButton.setVisibility(View.INVISIBLE);
                disconnectButton.setVisibility(View.VISIBLE);
                shareKeyButton.setVisibility(View.VISIBLE);
                shareKeyStatus.setVisibility(View.VISIBLE);

                if (!isGroupOwner) {
                    ArrayList<WifiP2pDevice> list = new ArrayList<>();
                    list.add(owner);
                    updateSpinner(list);
                } else {
                    updateSpinner(deviceList);
                }


                spinner.setEnabled(false);
                break;
            default:
                isConnected = false;
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();

                // could not connect
                // enable the button
                connectButton.setEnabled(true);
                findDevicesButton.setEnabled(true);
                connectButton.setVisibility(View.VISIBLE);
                disconnectButton.setVisibility(View.INVISIBLE);
                shareKeyButton.setVisibility(View.INVISIBLE);
                shareKeyStatus.setVisibility(View.INVISIBLE);

                discoverPeers(findDevicesButton);
                spinner.setEnabled(true);
                break;
        }
    }

    public void disconnectFromPeer(View v) {
        // disconnect from peer
        broadcastReceiver.disconnectFromPeer(this);
    }

    public void updateSpinner(Collection<WifiP2pDevice> deviceList) {
        // get the peers
        ArrayList<WifiP2pDevice> peers = broadcastReceiver.getPeers(deviceList);

        // update the spinner
        ArrayAdapter<WifiP2pDevice> groupsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, peers);
        spinner.setAdapter(groupsAdapter);
    }

    public void shareKey(View v) {
        if (isConnected) new FileServerAsyncTask(getApplicationContext(), shareKeyStatus).execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onSuccess() {
    }

    @Override
    public void onFailure(int reason) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        shareKeyStatus.setText("Sending: " + uri);
        Log.d(TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(this, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.info = info;
    }
}
