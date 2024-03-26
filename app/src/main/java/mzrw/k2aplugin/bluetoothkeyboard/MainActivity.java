package mzrw.k2aplugin.bluetoothkeyboard;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends KeyboardActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        final NotificationUtility notificationUtility = new NotificationUtility(this);
        notificationUtility.registerNotificationChannel();
        super.onCreate(savedInstanceState);
    }


    public void onSelectAuthorizedDevices(View v) {
        final Intent intent = new Intent(this, AuthorizedDevicesActivity.class);
        startActivity(intent);
    }



}
