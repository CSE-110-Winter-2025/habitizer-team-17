package edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogGoalTimeBinding;

public class SetGoalTimeDialogFragment extends DialogFragment {
    private FragmentDialogGoalTimeBinding view;
    private MainViewModel activityModel;

    SetGoalTimeDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    public static SetGoalTimeDialogFragment newInstance() {
        var fragment = new SetGoalTimeDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogGoalTimeBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Set Goal Time")
                .setMessage("Please provide a goal time")
                .setView(view.getRoot())
                .setPositiveButton("Set", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var time = Integer.parseInt(view.goalTimeEditText.getText().toString());
        int id = activityModel.getIsShowingMorning().getValue() ? 0 : 1;
        activityModel.setRoutineGoalTime(id, time);
        activityModel.updateGoalTimeDisplay(time);

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}