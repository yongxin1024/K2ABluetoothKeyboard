package mzrw.k2aplugin.bluetoothkeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NotificationUtility notificationUtility = new NotificationUtility(this);
        notificationUtility.registerNotificationChannel();

        final Button btnSendTestString = findViewById(R.id.btnSendTestString);

        btnSendTestString.setOnClickListener((v) -> {
            final Intent intent =  new Intent(MainActivity.this, KeyboardActivity.class);
            intent.putExtra(KeyboardActivity.INTENT_EXTRA_STRING_TO_TYPE, "WelcomeJoe1990!");
            startActivity(intent);
        });
    }


    public void onSelectAuthorizedDevices(View v) {
        final Intent intent = new Intent(this, AuthorizedDevicesActivity.class);
        startActivity(intent);
    }

}
