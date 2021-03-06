package net.iquesoft.android.seedprojectchat.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.backendless.push.BackendlessPushService;

import net.iquesoft.android.seedprojectchat.view.classes.activity.MainActivity;

public class PushServices extends BackendlessPushService
{
    private int NOTIFICATION_ID = 0;

    public PushServices() {
        super();
    }

    @Override
    public boolean onMessage(Context context, Intent intent) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(net.iquesoft.android.seedprojectchat.R.drawable.seed_logo)
                        .setContentTitle("You have new message")
                        .setContentText(intent.getStringExtra("message"));

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(++NOTIFICATION_ID, builder.build());
        return super.onMessage(context, intent);
    }

    @Override
    public void onError(Context context, String message) {
        super.onError(context, message);
    }
}
