package uk.ac.northumbria.securephonebook.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.northumbria.securephonebook.OpenReceivedContactActivity;

/**
 * Created by Siddhant on 02/04/2017.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements PeerListListener {
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private OpenReceivedContactActivity activity;
    private boolean isP2pEnabled;
    private WifiP2pDeviceList peers;

    public WiFiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager, Channel channel, OpenReceivedContactActivity activity) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                isP2pEnabled = true;
            } else {
                // Wi-Fi P2P is not enabled
                isP2pEnabled = false;
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            wifiP2pManager.requestPeers(channel, this);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pGroup groupInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            activity.connectionChanged(networkInfo.getState(), groupInfo.isGroupOwner(), groupInfo.getClientList(), groupInfo.getOwner());
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    public void connectToPeer(WifiP2pDevice device, ActionListener actionListener) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        wifiP2pManager.connect(channel, config, actionListener);
    }

    public void disconnectFromPeer(final ActionListener actionListener) {
       wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null && wifiP2pManager != null && channel != null && group.isGroupOwner()) {
                    wifiP2pManager.removeGroup(channel, actionListener);
                }
            }
        });
    }

    public boolean isP2pEnabled() {
        return isP2pEnabled;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        this.peers = peers;
        activity.updateSpinner(peers.getDeviceList());
    }

    public ArrayList<WifiP2pDevice> getPeers(Collection<WifiP2pDevice> peers) {
        return (peers != null) ? new ArrayList<>(peers) : null;
    }
}
