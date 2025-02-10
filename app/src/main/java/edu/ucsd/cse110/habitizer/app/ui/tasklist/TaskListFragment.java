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
import java.util.stream.Stream;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTaskListBinding;
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
        var modelOwner = this;
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new TaskListAdapter(requireContext(), List.of());
        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            ArrayList<Task> taskAdapterList = new ArrayList<>(tasks);
            Stream<Task> filteredIdList = taskAdapterList.stream().filter(
                    o -> o.routineId().equals(activityModel.getIsShowingMorning().getValue() ? 0 : 1)
            );
            adapter.addAll(filteredIdList.toList());
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = FragmentTaskListBinding.inflate(inflater, container, false);
        view.taskList.setAdapter(adapter);

        // Update title and timer display
        activityModel.getTitle().observe(o -> view.displayedTitle.setText(o));
        activityModel.getCompletedTime().observe(o -> {
            if (view.timerDisplay != null) {
                view.timerDisplay.setText(o != null ? o : "00:00");
            }
        });

        activityModel.getIsTimerRunning().observe(isRunning -> {
            if (view.startButton != null && view.stopButton != null) {
                view.startButton.setEnabled(!isRunning);
                view.stopButton.setEnabled(isRunning);
            }
        });

        // Button click listeners
        view.startButton.setOnClickListener(v -> activityModel.startTimer());
        view.stopButton.setOnClickListener(v -> activityModel.stopTimer());
        view.fastForwardButton.setOnClickListener(v -> activityModel.forwardTimer());

        // Next Routine Button
        view.nextButton.setOnClickListener(v -> {
            activityModel.nextRoutine();
            List<Task> tasks = activityModel.getOrderedTasks().getValue();
            if (tasks == null) return;
            adapter.clear();
            ArrayList<Task> taskAdapterList = new ArrayList<>(tasks);
            Stream<Task> filteredIdList = taskAdapterList.stream().filter(
                    o -> o.routineId().equals(activityModel.getIsShowingMorning().getValue() ? 0 : 1)
            );
            adapter.addAll(filteredIdList.toList());
            adapter.notifyDataSetChanged();
        });

        return view.getRoot();

    }
}
