package edu.ucsd.cse110.habitizer.app.ui.editroutine;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.fragment.app.FragmentActivity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.habitizer.app.databinding.ListItemEditTaskBinding;
import edu.ucsd.cse110.habitizer.app.ui.editroutine.dialog.DeleteTaskDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.MutableSubject;

public class EditRoutineAdapter extends ArrayAdapter<Task> {

    Consumer<Integer> onUpClick;
    Consumer<Integer> onDownClick;

    public EditRoutineAdapter(Context context, List<Task> editTasks, Consumer<Integer> onUpClick, Consumer<Integer> onDownClick) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(editTasks));
        this.onUpClick = onUpClick;
        this.onDownClick = onDownClick;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the edit task for this position.
        var editTask = getItem(position);
        assert editTask != null;

        // Check if a view is being reused...
        ListItemEditTaskBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemEditTaskBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemEditTaskBinding.inflate(layoutInflater, parent, false);
        }

        //wire the change ordering buttons
        binding.moveTaskUpButton.setOnClickListener(v -> {
            var task = getItem(position);
            onUpClick.accept(task.id());
        });

        binding.moveTaskDownButton.setOnClickListener(v -> {
            var task = getItem(position);
            onDownClick.accept(task.id());
        });

        // Populate the view with the edit tasks's data.
        binding.editTaskNameText.setText(editTask.name());
        binding.editTaskNameText.setOnClickListener(v -> {
            var task = getItem(position);
            if (task != null && task.id() != null) {
                var dialogFragment = edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog.EditTaskDialogFragment.newInstance(task.id(), task.name());
                // We need to get the FragmentManager from the context
                if (getContext() instanceof FragmentActivity) {
                    dialogFragment.show(
                            ((FragmentActivity) getContext()).getSupportFragmentManager(),
                            "EditTaskDialogFragment"
                    );
                }
            }
        });

        binding.cardDeleteButton.setOnClickListener(v -> {
            var id = getItem(position).id();
            assert id != null;
            showDeleteDialog(id);
        });

        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    private void showDeleteDialog(int taskId) {
        var dialog = DeleteTaskDialogFragment.newInstance(taskId);
        dialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "EditTaskDialog");
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var editTask = getItem(position);
        assert editTask != null;

        var id = editTask.id();
        assert id != null;

        return id;
    }
}
