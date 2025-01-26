package mzrw.k2aplugin.bluetoothkeyboard;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import mzrw.k2aplugin.bluetoothkeyboard.core.AuthorizedDevicesManager;
import mzrw.k2aplugin.bluetoothkeyboard.core.HidService;
import mzrw.k2aplugin.bluetoothkeyboard.layout.KeyboardLayoutFactory;
import mzrw.k2aplugin.bluetoothkeyboard.util.PasswordGenerator;

@SuppressLint("MissingPermission")
public class KeyboardActivity extends AbstractBluetoothActivity implements HidService.StateChangeListener {
    private static final String TAG = KeyboardActivity.class.getName();
    public static final String INTENT_EXTRA_STRING_TO_TYPE = "intent_extra_string_to_type";
    public static final String BUNDLE_KEY_SELECTED_DEVICE = "bundle_key_selected_device";
    public static final String ENTER_STRING = "\n";

    public static String CURRENT_SNED_STRING = "";

    private HidService hidService;

    private SharedPreferences selectedEntriesPreferences;
    private String selectedDevice;
    private List<BluetoothDevice> devices;

    private Spinner deviceSpinner;

    private EditText inputPassword;
    private Button btnPassword;
    private Button btnEnter;

    private EditText generatedPassword;
    private Button btnGenPassword;
    private Button btnAccept;
    private Button btnToggleCollapsible;
    private Button btnNoDevicesAuthorized;
    private TextView txtState;

    protected SharedPreferences preferences;
    protected final String PASSWORD_PREFERENCES_NAME = "PASSWORD_PREFERENCES_NAME";
    protected final String PASSWORD_PREFERENCES_KEY = "PASSWORD";
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences(PASSWORD_PREFERENCES_NAME, Context.MODE_PRIVATE);
        hidService = new HidService(getApplicationContext(), bluetoothAdapter);
        selectedEntriesPreferences = getPreferences(MODE_PRIVATE);

        inputPassword = findViewById(R.id.inputPassword);
        deviceSpinner = findViewById(R.id.deviceSpinner);
        btnPassword = findViewById(R.id.btnPassword);
        btnEnter = findViewById(R.id.btnEnter);
        btnNoDevicesAuthorized = findViewById(R.id.btnNoDevicesAuthorized);
        txtState = findViewById(R.id.txtState);
        generatedPassword = findViewById(R.id.generatedPassword);
        btnGenPassword = findViewById(R.id.btnGenPassword);
        btnAccept = findViewById(R.id.btnAccept);
        btnToggleCollapsible = findViewById(R.id.btnToggleCollapsible);
        initPasswordOnCreate();
        registerListeners();
    }

    private void checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_BLUETOOTH_PERMISSIONS
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothPermissions();
        updateSelectionFromPreferences();
        checkBluetoothEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();

        selectedEntriesPreferences.edit()
                .putString(BUNDLE_KEY_SELECTED_DEVICE, selectedDevice)
                .apply();
    }

    private void registerListeners() {
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = devices.get(position).getAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnPassword.setOnClickListener(this::connectToDevice);
        btnEnter.setOnClickListener(this::connectToDevice);
        btnGenPassword.setOnClickListener(this::generatePassword);
        btnAccept.setOnClickListener(this::acceptPassword);
        LinearLayout collapsibleSection = findViewById(R.id.collapsibleSection);
        btnToggleCollapsible.setOnClickListener(v -> {
            if (collapsibleSection.getVisibility() == View.VISIBLE) {
                collapsibleSection.setVisibility(View.GONE);
            } else {
                collapsibleSection.setVisibility(View.VISIBLE);
            }
        });
        btnNoDevicesAuthorized.setOnClickListener(v -> startActivity(new Intent(KeyboardActivity.this, AuthorizedDevicesActivity.class)));
        hidService.setOnStateChangeListener(this);
    }

    private void acceptPassword(View view) {
        inputPassword.setText(generatedPassword.getText().toString());
        preferences.edit().putString(PASSWORD_PREFERENCES_KEY, generatedPassword.getText().toString()).apply();
    }

    private void generatePassword(View view) {
        String newPassword = PasswordGenerator.generatePassword();
        generatedPassword.setText(newPassword);
    }

    @Override
    protected void onBluetoothEnabled() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                == PackageManager.PERMISSION_GRANTED) {

            devices = new AuthorizedDevicesManager(this).filterAuthorizedDevices(bluetoothAdapter.getBondedDevices());
            btnNoDevicesAuthorized.setVisibility(devices.size() > 0 ? View.GONE : View.VISIBLE);
            deviceSpinner.setVisibility(devices.size() > 0 ? View.VISIBLE : View.GONE);

            List<String> names = devices.stream().map(BluetoothDevice::getName).collect(Collectors.toList());
            SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
            deviceSpinner.setAdapter(adapter);

            updateSelectionFromPreferences();
        } else {
            Log.e(TAG, "Missing BLUETOOTH_CONNECT permission");
        }
    }


    private void updateSelectionFromPreferences() {
        if (devices != null) {
            selectedDevice = selectedEntriesPreferences.getString(BUNDLE_KEY_SELECTED_DEVICE, null);
            IntStream.range(0, devices.size())
                    .filter(index -> devices.get(index).getAddress().equals(selectedDevice))
                    .findAny()
                    .ifPresent(index -> deviceSpinner.setSelection(index));
        }
    }

    private void initPasswordOnCreate() {
        String editorPasswd = inputPassword.getText().toString();
        String savedPasswd = preferences.getString(PASSWORD_PREFERENCES_KEY, "");
        if (editorPasswd.isEmpty() && !savedPasswd.isEmpty()) {
            inputPassword.setText(savedPasswd);
        }
    }

    public void connectToDevice(View v) {
        String editorPasswd = inputPassword.getText().toString();
        String savedPasswd = preferences.getString(PASSWORD_PREFERENCES_KEY, "");
        if (!editorPasswd.equals(savedPasswd)) {
            preferences.edit().putString(PASSWORD_PREFERENCES_KEY, inputPassword.getText().toString()).apply();
        }
        setSendString(v.getId());
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(selectedDevice);
        Log.i(TAG, "connecting to " + device.getName());
        try {
            hidService.connect(device, KeyboardLayoutFactory.getLayout("UK QWERTY"));
        } catch (Exception e) {
            Log.e(TAG, "failed to connect to " + device.getName());
        }
    }

    private void setSendString(int btnId) {
        String savedPasswd = preferences.getString(PASSWORD_PREFERENCES_KEY, "");
        CURRENT_SNED_STRING = btnId == R.id.btnEnter ? ENTER_STRING : savedPasswd;
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case HidService.STATE_DISCONNECTED:
//                finishAndRemoveTask();
                txtState.setText("Done");
                break;
            case HidService.STATE_CONNECTING:
                txtState.setText("Connecting");
                break;
            case HidService.STATE_CONNECTED:
                txtState.setText("Connected");
                hidService.sendText(CURRENT_SNED_STRING);
                break;
            case HidService.STATE_SENDING:
                txtState.setText("Sending");
                break;
            case HidService.STATE_SENT:
                txtState.setText("Sent");
                hidService.disconnect();
                break;
            case HidService.STATE_DISCONNECTING:
                txtState.setText("Disconnecting");
                break;
        }
    }
}
