package id.ac.binus.taskmanagerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private TextView tvDeadline;
    private Spinner spinnerCategory;
    private String selectedDeadline = ""; // Store selected deadline

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        tvDeadline = findViewById(R.id.tvDeadline); // Initialize TextView for deadline
        spinnerCategory = findViewById(R.id.spinnerCategory); // Initialize Spinner for category
        Button buttonSaveTask = findViewById(R.id.buttonSaveTask);

        // Set up Spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set an OnClickListener to open the DatePickerDialog
        tvDeadline.setOnClickListener(v -> showDatePickerDialog());

        buttonSaveTask.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString(); // Get selected category

            // Pass task data back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", id);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("description", description);
            resultIntent.putExtra("deadline", selectedDeadline); // Use selected deadline
            resultIntent.putExtra("category", category); // Pass the selected category
            setResult(RESULT_OK, resultIntent);
            finish();
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
