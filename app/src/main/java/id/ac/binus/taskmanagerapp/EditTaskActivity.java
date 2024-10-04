package id.ac.binus.taskmanagerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView tvDeadline; // Change EditText to TextView for deadline
    private Button buttonUpdateTask;
    private String selectedDeadline = ""; // Store selected deadline

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        tvDeadline = findViewById(R.id.tvDeadline); // Initialize TextView for deadline
        buttonUpdateTask = findViewById(R.id.buttonUpdateTask);

        // Get the task details from the intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        selectedDeadline = intent.getStringExtra("deadline");

        // Populate the fields with current task details
        editTextTitle.setText(title);
        editTextDescription.setText(description);
        tvDeadline.setText(selectedDeadline); // Display existing deadline

        // Set OnClickListener to open the DatePickerDialog
        tvDeadline.setOnClickListener(v -> showDatePickerDialog());

        buttonUpdateTask.setOnClickListener(v -> {
            String updatedTitle = editTextTitle.getText().toString();
            String updatedDescription = editTextDescription.getText().toString();

            // Create an Intent to send updated data back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", id);
            resultIntent.putExtra("title", updatedTitle);
            resultIntent.putExtra("description", updatedDescription);
            resultIntent.putExtra("deadline", selectedDeadline); // Pass selected deadline
            setResult(RESULT_OK, resultIntent);
            finish(); // Close the activity
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date
                    selectedDeadline = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvDeadline.setText(selectedDeadline); // Display selected date
                }, year, month, day);
        datePickerDialog.show();
    }
}
