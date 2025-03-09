package edu.ucsd.cse110.habitizer.app.ui.editroutine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.Screen;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentEditRoutineBinding;
import edu.ucsd.cse110.habitizer.app.ui.editroutine.dialog.AddTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.editroutine.dialog.SetGoalTimeDialogFragment;

public class EditRoutineFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentEditRoutineBinding view;
    private EditRoutineAdapter adapter;

    public static EditRoutineFragment newInstance() {
        EditRoutineFragment fragment = new EditRoutineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new EditRoutineAdapter(requireContext(), List.of());

        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(tasks));
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.view = FragmentEditRoutineBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.routineTaskList.setAdapter(adapter);

        activityModel.getTitle().observe(title -> view.routineNameText.setText(title));
        activityModel.getGoalTimeDisplay().observe(goalTime -> view.routineGoalTimeText.setText(goalTime));

        view.routineSetGoalTimeButton.setOnClickListener(v -> {
            var dialogFragment = SetGoalTimeDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "SetGoalTimeDialogFragment");
        });

        view.routineAddTaskButton.setOnClickListener(v -> {
            var dialogFragment = AddTaskDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "AddTaskDialogFragment");
        });

        view.routineEndEditButton.setOnClickListener(
                v -> activityModel.getScreen().setValue(Screen.PREVIEW_SCREEN)
        );

        view.routineDeleteButton.setOnClickListener(
                v -> activityModel.removeRoutine(activityModel.getCurrentRoutine().getValue())
        );

        return view.getRoot();
    }
}
