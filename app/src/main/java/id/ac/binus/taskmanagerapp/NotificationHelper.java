package id.ac.binus.taskmanagerapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationHelper {

    public static void scheduleTaskNotification(Context context, Task task) {
        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent to trigger when the alarm goes off
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("taskTitle", task.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getDeadlineDate());

        // Schedule the alarm with the AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    // This method is for creating a notification channel (Android O+)
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Deadline Channel";
            String description = "Channel for task deadline notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TASK_DEADLINE_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
