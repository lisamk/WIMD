package wimd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.BroadcastReceiver;

import java.util.List;

public abstract class LocationActivity extends Activity {

    /* Wait time between records*/
    protected static final int WAIT_TIME = 1000;
    /* Stores the measurements during the scan */
    protected WifiManager wifi;
    protected BroadcastReceiver receiver;
    protected MainApp app;
    protected boolean isActive = true;

    protected static final String[] PLACES = {"NONE", "Mensa", "Bibliothek", "SP1",
            "SP2", "SP3", "HS1", "HS2", "HS3", "HS4", "HS5", "HS6", "HS7", "HS8",
            "HS9", "HS10", "HS11", "HS12", "HS13", "HS14", "HS15", "HS16", "HS17",
            "HS18", "HS19", "Ch@t", "Teichwerk", "LUI", "Sassi", "KeplerGebaeude",
            "Hoersaaltrakt", "Physikgebaeude", "Juridicum", "ManagementGebaude", "Bankgebaeude"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MainApp) getApplication();
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()) wifi.setWifiEnabled(true);
        setupContentView();
    }

    protected abstract void setupContentView();

    @Override
    public void onStart() {
        super.onStart();

        // Setup receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                onReceiveWifiScanResults(wifi.getScanResults());
            }
        };

        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public abstract void onReceiveWifiScanResults(List<ScanResult> results);

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    public void onResume() {
        super.onResume();
        isActive = true;
    }

    public void onPause() {
        super.onPause();
        isActive = false;
    }
}
