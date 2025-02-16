package edu.ucsd.cse110.habitizer.app.ui.tasklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import edu.ucsd.cse110.habitizer.app.HabitizerApplication;
import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.Screen;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog.SetGoalTimeDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = FragmentTaskListBinding.inflate(inflater, container, false);
        view.taskList.setAdapter(adapter);
        activityModel.getTitle().observe(title -> view.displayedTitle.setText(title));
        activityModel.getGoalTimeDisplay().observe(goalTime -> view.goalTime.setText(goalTime));

        view.nextButton.setOnClickListener(v -> {
            activityModel.nextRoutine();
        });

        view.startButton.setOnClickListener(v -> {
            activityModel.startTimer();
            List<ActiveTask> activeTasks = new ArrayList<>();
            var routine = activityModel.getCurrentRoutine().getValue();
            if (routine == null) return;
            for (var task : routine.tasks()) {
                ActiveTask newActiveTask = new ActiveTask(task, false, 0);
                activeTasks.add(newActiveTask);
            }

            activityModel.getActiveRoutine().setValue(new ActiveRoutine(routine, activeTasks));
            var app = (HabitizerApplication) requireActivity().getApplication();
            app.getScreen().setValue(Screen.ACTIVE_ROUTINE_SCREEN);
        });

        view.setGoalTime.setOnClickListener(v -> {
            var dialogFragment = SetGoalTimeDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "SetGoalTimeDialogFragment");
        });

        return view.getRoot();
    }
}
