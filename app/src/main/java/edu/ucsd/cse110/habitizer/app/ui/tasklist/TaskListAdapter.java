package edu.ucsd.cse110.habitizer.app.ui.tasklist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.databinding.TaskBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskListAdapter extends ArrayAdapter<Task> {
    public TaskListAdapter(Context context, List<Task> tasks) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(tasks));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the task for this position
        var task = getItem(position);
        assert task != null;

        // Check if a view is being reused...
        TaskBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = TaskBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = TaskBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the task
        binding.taskName.setText(task.getName());

        //update time and check off task
        if (task.isCompleted()) {
            binding.taskName.setPaintFlags(binding.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String completionTime = task.getCompletionTime();
            if (!completionTime.isEmpty()) {
                binding.completionTime.setText(completionTime);
                binding.completionTime.setVisibility(View.VISIBLE);
            } else {
                binding.taskName.setPaintFlags(binding.taskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                binding.completionTime.setVisibility(View.GONE);
            }
        }
        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var task = getItem(position);
        assert task != null;

        var id = task.id();
        assert  id != null;

        return id;
    }
}
