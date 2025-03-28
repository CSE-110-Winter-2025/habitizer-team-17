package edu.ucsd.cse110.habitizer.app.ui.tasklist;

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
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.habitizer.app.ui.editroutine.dialog.AddRoutineDialogFragment;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemEditTaskBinding;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;
    private TaskListAdapter adapter;

    public TaskListFragment() {
        // Required empty constructor
    }

    public static TaskListFragment newInstance() {
        TaskListFragment fragment = new TaskListFragment();
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
        this.adapter = new TaskListAdapter(requireContext(), List.of());

        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            ArrayList<Task> taskAdapterList = new ArrayList<>(tasks);
            adapter.addAll(taskAdapterList);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = FragmentTaskListBinding.inflate(inflater, container, false);
        view.taskList.setAdapter(adapter);
        activityModel.getTitle().observe(title -> view.displayedTitle.setText(title));
        activityModel.getGoalTimeDisplay().observe(goalTime -> view.goalTime.setText(goalTime));

        view.nextButton.setOnClickListener(v -> activityModel.nextRoutine());

        view.startButton.setOnClickListener(v -> activityModel.startRoutine());

        view.editButton.setOnClickListener(
                v -> activityModel.getScreen().setValue(Screen.EDIT_ROUTINE_SCREEN)
        );

        view.addButton.setOnClickListener(v -> {
            var dialogFragment = AddRoutineDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "AddRoutineDialogFragment");
        });

        return view.getRoot();
    }
}
