package mzrw.k2aplugin.bluetoothkeyboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Abstract Activity to ensure Bluetooth is activated.
 */
public abstract class AbstractBluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 17;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 18;

    protected BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * 检查并请求 BLUETOOTH_CONNECT 权限。
     */
    protected void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    /**
     * Call this method to check that Bluetooth is enabled in {@link #onCreate(Bundle)}.
     */
    @SuppressLint("MissingPermission")
    protected void checkBluetoothEnabled() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Bluetooth unsupported
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            onBluetoothEnabled();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            onBluetoothEnabled();
        }
    }

    /**
     * This method is called when Bluetooth is activated.
     * Overwrite this method to take some action when Bluetooth is enabled.
     */
    protected abstract void onBluetoothEnabled();
}
