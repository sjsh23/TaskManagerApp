package id.ac.binus.taskmanagerapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("taskTitle");

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_DEADLINE_CHANNEL")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Task Deadline Approaching")
                .setContentText("Your task \"" + taskTitle + "\" is due soon!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(taskTitle.hashCode(), builder.build());
    }
}
