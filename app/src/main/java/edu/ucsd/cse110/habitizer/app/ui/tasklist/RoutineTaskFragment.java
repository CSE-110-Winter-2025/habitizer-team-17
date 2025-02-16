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
import java.util.function.Consumer;
import java.util.stream.Stream;

import edu.ucsd.cse110.habitizer.app.HabitizerApplication;
import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.Screen;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.habitizer.app.databinding.RoutineScreenBinding;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.MutableSubject;

public class RoutineTaskFragment extends Fragment {
    private MainViewModel activityModel;
    private RoutineScreenBinding view;
    private RoutineTaskAdapter adapter;




    public RoutineTaskFragment() {
        // Required empty constructor
    }

    public static RoutineTaskFragment newInstance() {
        RoutineTaskFragment fragment = new RoutineTaskFragment();
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
        this.adapter = new RoutineTaskAdapter(requireContext(), List.of(), (id) -> {
            activityModel.checkTask(id);
            if(activityModel.checkIfAllCompleted()){
                activityModel.endRoutine();
            }
        }, activityModel.getOnFinishedRoutine());
        activityModel.getActiveRoutine().observe(routine -> {
            if (routine == null) return;
            adapter.clear();
            ArrayList<ActiveTask> taskAdapterList = new ArrayList<>(routine.activeTasks());
            adapter.addAll(taskAdapterList);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = RoutineScreenBinding.inflate(inflater, container, false);
        view.activeList.setAdapter(adapter);
        activityModel.getActiveRoutine().observe(activeRoutine -> {
            if (activeRoutine == null) return;
            view.routineTitle.setText(activeRoutine.routine().name());
        });

        activityModel.getOnFinishedRoutine().observe(finished -> {
            if (finished == null) return;
            if(finished) {
                view.stopButton.setEnabled(false);
                view.endRoutineButton.setEnabled(false);
                view.backButton.setVisibility(View.VISIBLE);
            }
        });

        view.backButton.setVisibility(View.GONE);
        view.backButton.setOnClickListener(v -> {
            var app = (HabitizerApplication) requireActivity().getApplication();
            app.getScreen().setValue(Screen.PREVIEW_SCREEN);
            activityModel.resetRoutine();
        });

        activityModel.getCurrentTimeDisplay().observe(o -> {
            if(activityModel.getIsTimerRunning().getValue()) {
                view.timerDisplay.setText(activityModel.getCurrentTimeDisplay().getValue());
            }
        });


        activityModel.getCompletedTimeDisplay().observe(o -> {
                if(!activityModel.getIsTimerRunning().getValue()) {
                    view.timerDisplay.setText(activityModel.getCompletedTimeDisplay().getValue());
                }
        });
        // Button click listeners
        view.endRoutineButton.setOnClickListener(v -> {
            activityModel.endRoutine();
        });

        if(activityModel.isMocked()) {
            view.fastForwardButton.setOnClickListener(v -> activityModel.forwardTimer());
            view.stopButton.setOnClickListener(v -> activityModel.stopTimer());
        } else {
            view.fastForwardButton.setVisibility(View.GONE);
            view.stopButton.setVisibility(View.GONE);
        }

        return view.getRoot();
    }
}
