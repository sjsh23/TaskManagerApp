package id.ac.binus.taskmanagerapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.widget.SearchView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "TASK_DEADLINE_CHANNEL";
    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Task> filteredTaskList = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private static final String PREFS_NAME = "task_prefs";
    private static final String TASK_LIST_KEY = "task_list";
    private SearchView searchView; // Declare SearchView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
        createNotificationChannel();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        searchView = findViewById(R.id.searchView); // Initialize SearchView

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load tasks from SharedPreferences
        loadTasksFromPreferences();

        // Set up RecyclerView
        taskAdapter = new TaskAdapter(filteredTaskList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        filteredTaskList.addAll(taskList); // Add all tasks to the filtered list initially

        // Add button action to go to AddTaskActivity
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, 1);
        });

        // Setup swipe-to-delete functionality using ItemTouchHelper
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No need for move functionality
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position of the swiped item
                int position = viewHolder.getAdapterPosition();

                // Confirm delete with an AlertDialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the task from the list
                            cancelTaskNotification(filteredTaskList.get(position)); // Cancel notification for filtered task
                            taskList.remove(filteredTaskList.get(position)); // Remove from original list
                            filteredTaskList.remove(position); // Remove from filtered list
                            taskAdapter.notifyItemRemoved(position);
                            saveTasksToPreferences(); // Save changes to preferences
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Restore the task if user cancels delete
                            taskAdapter.notifyItemChanged(position);
                        })
                        .show();
            }
        };

        // Attach ItemTouchHelper to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Setup SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });
    }

    // Method to filter tasks based on search query
    private void filterTasks(String query) {
        filteredTaskList.clear();
        if (query.isEmpty()) {
            filteredTaskList.addAll(taskList); // Show all tasks if query is empty
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Task task : taskList) {
                if (task.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        task.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredTaskList.add(task);
                }
            }
        }
        taskAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Deadline Notifications";
            String description = "Notifies when a task deadline is near";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Handling task creation
            String id = data.getStringExtra("id");
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            String deadline = data.getStringExtra("deadline");
            String category = data.getStringExtra("category"); // Get category from intent

            Task newTask = new Task(id, title, description, deadline, category);
            taskList.add(newTask);
            filteredTaskList.add(newTask); // Also add to filtered list
            taskAdapter.notifyDataSetChanged();
            saveTasksToPreferences(); // Save tasks to SharedPreferences after adding
            setTaskNotification(newTask);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            // Handle task editing
            String id = data.getStringExtra("id");
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            String deadline = data.getStringExtra("deadline");
            String category = data.getStringExtra("category"); // Get updated category

            // Find the task by ID and update it
            for (Task task : taskList) {
                if (task.getId().equals(id)) {
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setDeadline(deadline);
                    task.setCategory(category); // Update the category
                    cancelTaskNotification(task);
                    setTaskNotification(task);
                    break;
                }
            }
            taskAdapter.notifyDataSetChanged();
            saveTasksToPreferences(); // Save tasks to SharedPreferences after editing
        }
    }

    // Method to save tasks to SharedPreferences
    private void saveTasksToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(taskList); // Convert task list to JSON
        editor.putString(TASK_LIST_KEY, json);
        editor.apply(); // Commit the changes
    }

    // Method to load tasks from SharedPreferences
    private void loadTasksFromPreferences() {
        String json = sharedPreferences.getString(TASK_LIST_KEY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Task>>() {}.getType();
            taskList = gson.fromJson(json, type); // Convert JSON back to list
        }
    }

    private void setTaskNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("taskTitle", task.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the deadline date as a Date object
        Date deadlineDate = task.getDeadlineDate();
        if (deadlineDate != null) {
            // Schedule the alarm to go off at the deadline time
            long triggerTime = deadlineDate.getTime();
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    // Cancel notification when the task is deleted
    private void cancelTaskNotification(Task task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId().hashCode(), intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel(); // Optional: Cancel the PendingIntent
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with notifications setup
            } else {
                // Permission denied, notify the user or handle gracefully
            }
        }
    }
}
