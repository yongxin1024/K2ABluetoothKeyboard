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


public class MainActivity extends AppCompatActivity {

    private EditText inputPassword;

    private SharedPreferences preferences;
    private final String PASSWORD_PREFERENCES_NAME = "PASSWORD_PREFERENCES_NAME";
    private final String PASSWORD_PREFERENCES_KEY = "PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = this.getSharedPreferences(PASSWORD_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final NotificationUtility notificationUtility = new NotificationUtility(this);
        notificationUtility.registerNotificationChannel();

        final Button btnSendTestString = findViewById(R.id.btnSendTestString);
        inputPassword = findViewById(R.id.inputPassword);
        inputPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == IME_ACTION_SEARCH ||
                    actionId == IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    preferences.edit().putString(PASSWORD_PREFERENCES_KEY, inputPassword.getText().toString()).apply();
                    return true;
                }
            }
            return false;
        });
        btnSendTestString.setOnClickListener((v) -> {
            final Intent intent = new Intent(MainActivity.this, KeyboardActivity.class);
            intent.putExtra(KeyboardActivity.INTENT_EXTRA_STRING_TO_TYPE, inputPassword.getText().toString());
            startActivity(intent);
        });
        initPasswordOnCreate();
    }


    public void onSelectAuthorizedDevices(View v) {
        final Intent intent = new Intent(this, AuthorizedDevicesActivity.class);
        startActivity(intent);
    }

    private void initPasswordOnCreate() {
        String editorPasswd = inputPassword.getText().toString();
        String savedPasswd = preferences.getString(PASSWORD_PREFERENCES_KEY,"");
        if(editorPasswd.isEmpty() && !savedPasswd.isEmpty()){
            inputPassword.setText(savedPasswd);
        }
    }

}
