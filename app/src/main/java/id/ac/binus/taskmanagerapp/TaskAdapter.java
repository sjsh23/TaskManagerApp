package id.ac.binus.taskmanagerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private Context context;

    public TaskAdapter(ArrayList<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }
    public void updateTaskList(ArrayList<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDeadline.setText(task.getDeadline());
        holder.textViewDescription.setText(task.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, EditTaskActivity.class);
            editIntent.putExtra("id", task.getId());
            editIntent.putExtra("title", task.getTitle());
            editIntent.putExtra("description", task.getDescription());
            editIntent.putExtra("deadline", task.getDeadline());
            ((Activity) context).startActivityForResult(editIntent, 2); // Request code 2 for editing
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewDeadline, textViewDescription;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDeadline = itemView.findViewById(R.id.textViewDeadline);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
