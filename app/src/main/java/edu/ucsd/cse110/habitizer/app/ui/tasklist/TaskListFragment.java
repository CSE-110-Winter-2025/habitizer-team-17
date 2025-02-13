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
import java.util.stream.Stream;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog.SetGoalTimeDialogFragment;
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

        // Use the activity as the model owner
        var modelOwner = requireActivity(); // instead of 'this'
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
        activityModel.getTitle().observe(o -> view.displayedTitle.setText(activityModel.getTitle().getValue()));
        activityModel.getGoalTimeDisplay().observe(goalTime -> view.goalTime.setText(goalTime));

        activityModel.getIsTimerRunning().observe(isRunning -> {
            view.startButton.setEnabled(!isRunning);
            view.stopButton.setEnabled(isRunning);
        });

        activityModel.getCurrentTimeDisplay().observe(o -> view.timerDisplay.setText(activityModel.getCurrentTimeDisplay().getValue()));

        // Button click listeners
        view.startButton.setOnClickListener(v -> activityModel.startTimer());
        view.stopButton.setOnClickListener(v -> activityModel.stopTimer());
        view.fastForwardButton.setOnClickListener(v -> activityModel.forwardTimer());
        view.nextButton.setOnClickListener(v -> {
                    activityModel.nextRoutine();
                    adapter.clear();
                    ArrayList<Task> taskAdapterList = new ArrayList<>(activityModel.getOrderedTasks().getValue());
                    adapter.addAll(taskAdapterList);
                    adapter.notifyDataSetChanged();
                }
        );

        view.setGoalTime.setOnClickListener(v -> {
            var dialogFragment = SetGoalTimeDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "SetGoalTimeDialogFragment");
        });

        return view.getRoot();
    }
}
