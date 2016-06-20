package wimd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanDataActivity extends LocationActivity {

    private static final String SCANNING = "Scanning...";

    private int START_COMMAND;
    private int EXIT_COMMAND;
    private int ROOM_COMMAND;

    /* A map containing command ids and their names <command_id, command_name>*/
    private Map<Integer, String> commands;
    /* Bar that is displayed during the scan */
    private ProgressDialog pDialog;
    /* Sub Menu when prompted to select a room for the scan*/
    protected SubMenu rSubMenu;

    private String selectedLocation = PLACES[0];
    private boolean isScanning = false;

    AlertDialog noRoomSelectedDialog;

    @Override
    protected void setupContentView() {
        setTitle("Scanning Locations");
        setContentView(R.layout.scanning_layout);
        setupDialogs();
    }

    private void setupDialogs(){
        noRoomSelectedDialog = new AlertDialog.Builder(ScanDataActivity.this).create();
        noRoomSelectedDialog.setTitle("Alert");
        noRoomSelectedDialog.setMessage("No location selected.\nPlease select a location to start scanning.");
        noRoomSelectedDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
    }

    public void onReceiveWifiScanResults(List<ScanResult> results) {
        if (isScanning && !selectedLocation.equals(PLACES[0])) {
            String mac = "";
            int rssi = 0;
            for (ScanResult result : results) {
                app.addLocation(new Location(selectedLocation, mac, rssi, System.currentTimeMillis()/1000));
            }

            pDialog.dismiss();
            isScanning = false;

            Toast.makeText(getApplicationContext(), "Location added.", Toast.LENGTH_LONG).show();
        }
    }

    public void startScan() {
        if(selectedLocation.equals(PLACES[0])) {
            noRoomSelectedDialog.show();
        } else {
            pDialog = ProgressDialog.show(this, "", SCANNING, true);
            isScanning = true;
            wifi.startScan();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        commands = new HashMap<>();

        int id = 0;

        START_COMMAND = id;
        commands.put(id++, "Start Scanning");
        EXIT_COMMAND = id;
        commands.put(id++, "Exit");
        ROOM_COMMAND = id;
        commands.put(id++, "Choose Room");
        for (int place = 0; id < PLACES.length; place++, id++) {
            commands.put(id, PLACES[place]);
        }

        for (id = 0; id < commands.size(); id++) {
            if (id == START_COMMAND || id == EXIT_COMMAND) {
                menu.add(Menu.NONE, id, Menu.NONE, commands.get(id));
            } else if (id == ROOM_COMMAND) {
                rSubMenu = menu.addSubMenu(Menu.NONE, id, Menu.NONE, commands.get(id));
            } else if (id == ROOM_COMMAND + 1){
                rSubMenu.add(Menu.NONE, id, Menu.NONE, commands.get(id));
            } else if (id > ROOM_COMMAND) {
                rSubMenu.add(Menu.NONE, id, Menu.NONE, commands.get(id));
            }
        }
        super.onCreateOptionsMenu(menu);

        TextView selectedRoomView = (TextView) findViewById(R.id.selected_room_View);
        selectedRoomView.setText(commands.get(ROOM_COMMAND + 1));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == START_COMMAND) {
            startScan();
        } else if (id == EXIT_COMMAND) {
            finish();
        } else if (id > ROOM_COMMAND && id < commands.size()) {
            TextView selectedRoomView = (TextView) findViewById(R.id.selected_room_View);
            selectedLocation = commands.get(id);
            selectedRoomView.setText(commands.get(id));
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
