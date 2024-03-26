package mzrw.k2aplugin.bluetoothkeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


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
