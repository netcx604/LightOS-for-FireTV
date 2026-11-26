package de.customtv.start;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    private static final String ACTION_RELAUNCH = "de.customtv.start.BOOT_RELAUNCH";
    private static final long DELAY_MS = 6000L;
    private static final long SECOND_DELAY_MS = 20000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            schedule(context, DELAY_MS, 42);
            schedule(context, SECOND_DELAY_MS, 43);
        } else if (ACTION_RELAUNCH.equals(action)) {
            launch(context);
        }
    }

    private void schedule(Context context, long delayMs, int requestCode) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            launch(context);
            return;
        }
        Intent again = new Intent(context, BootReceiver.class);
        again.setAction(ACTION_RELAUNCH);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, again,
                PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + delayMs, pi);
    }

    private void launch(Context context) {
        Intent launch = new Intent(context, MainActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(launch);
    }
}
