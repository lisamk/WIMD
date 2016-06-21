package wimd;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WIMDActivity extends LocationActivity {
    private static final int EXIT_COMMAND = 0;
    private static final int START_SCAN_COMMAND = 1;

    private Client client;
    private TextView tv;
    private TextView myLocationView;
    private String myLocation;

    @Override
    protected void setupContentView() {
        setContentView(R.layout.wimd);
        myLocationView = (TextView) findViewById(R.id.myLocation);
        tv = (TextView) findViewById(R.id.tvTextView);
        client = new Client(this);
        startScanningThread();
    }

    @Override
    public void onReceiveWifiScanResults(List<ScanResult> results) {
        if (isActive && results!=null && results.size() > 0) {
            myLocation = null;

            String location = "Unknown location";
            for (ScanResult scanResult : results) {
                String mac = scanResult.BSSID;
                int rssi = scanResult.level;

                String result = app.findLocation(mac, rssi);
                location = (result!=null) ? result : location;
            }

            setMyLocation(location);
            isActive = false;
        }
    }

    public void startSearching() {
        runOnUiThread(new Runnable() {
            public void run() {
                tv.setText(client.getLocation());
            }
        });
    }

    private void setMyLocation(final String room) {
        myLocation = room;
        runOnUiThread(new Runnable() {
            public void run() {
                myLocationView.setText(myLocation);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, EXIT_COMMAND, Menu.NONE, "Exit");
        menu.add(Menu.NONE, START_SCAN_COMMAND, Menu.NONE, "Update Database");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        isActive = false;
        int id = item.getItemId();

        if (id == EXIT_COMMAND) {
            finish();
        } else if (id == START_SCAN_COMMAND) {
            Intent intent = new Intent(WIMDActivity.this, ScanDataActivity.class);
            startActivity(intent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        startScanningThread();
        return true;
    }

    public void startScanningThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isActive) {
                    wifi.startScan();
                    startSearching();
                    try {
                        Thread.sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}