package edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;

public class EditTaskDialogFragment extends DialogFragment {
    private MainViewModel activityModel;
    private EditText taskNameInput;
    private final int taskId;
    private final String currentName;

    private EditTaskDialogFragment(int taskId, String currentName) {
        this.taskId = taskId;
        this.currentName = currentName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    public static EditTaskDialogFragment newInstance(int taskId, String currentName) {
        var fragment = new EditTaskDialogFragment(taskId, currentName);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        taskNameInput = new EditText(requireContext());
        taskNameInput.setText(currentName);
        taskNameInput.setSelection(currentName.length());

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Edit Task Name")
                .setMessage("Enter new task name")
                .setView(taskNameInput)
                .setPositiveButton("Save", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        String newName = taskNameInput.getText().toString();
        if (!newName.isEmpty()) {
            activityModel.renameTask(taskId, newName);
        }
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}