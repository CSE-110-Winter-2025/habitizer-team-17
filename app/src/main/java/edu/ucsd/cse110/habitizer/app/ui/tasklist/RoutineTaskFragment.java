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

import edu.ucsd.cse110.habitizer.app.databinding.RoutineScreenBinding;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;

import edu.ucsd.cse110.habitizer.app.Screen;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;

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
            if (activityModel.checkIfAllCompleted()){
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = RoutineScreenBinding.inflate(inflater, container, false);
        view.activeList.setAdapter(adapter);

        activityModel.getActiveRoutine().observe(activeRoutine -> {
            if (activeRoutine == null) return;
            view.routineTitle.setText(activeRoutine.routine().name());
        });

        activityModel.getGoalTimeDisplay().observe(
                goalTimeDisplay -> view.goalTimeDisplay.setText(goalTimeDisplay)
        );

        activityModel.getTimerState().observe(timerState -> {
            if (timerState == null) return;
            view.pauseButton.setEnabled(timerState == TimerState.RUNNING);
            view.fastForwardButton.setEnabled(timerState == TimerState.RUNNING);
        });

        activityModel.getOnFinishedRoutine().observe(finished -> {
            if (finished == null) return;
            if(finished) {
                view.pauseButton.setEnabled(false);
                view.endRoutineButton.setEnabled(false);
                view.backButton.setVisibility(View.VISIBLE);
            }
        });

        view.backButton.setVisibility(View.GONE);
        view.backButton.setOnClickListener(v -> {
            activityModel.getScreen().setValue(Screen.PREVIEW_SCREEN);
            activityModel.resetRoutine();
        });

        activityModel.getCurrentTimeDisplay().observe(o -> {
            if(activityModel.getTimerState().getValue() != TimerState.STOPPED) {
                view.timerDisplay.setText(activityModel.getCurrentTimeDisplay().getValue());
            }
        });

        activityModel.getCompletedTimeDisplay().observe(o -> {
                if(activityModel.getTimerState().getValue() == TimerState.STOPPED) {
                    view.timerDisplay.setText(activityModel.getCompletedTimeDisplay().getValue());
                }
        });

        activityModel.getElapsedSinceLastTaskDisplay().observe(elapsedTime -> {
            if (elapsedTime == null) return;
            view.elapsedSinceLastTask.setText(elapsedTime);
        });

        // Button click listeners
        view.endRoutineButton.setOnClickListener(v -> {
            activityModel.endRoutine();
        });

        if(activityModel.isMocked()) {
            view.fastForwardButton.setOnClickListener(v -> activityModel.forwardTimer());
        } else {
            view.fastForwardButton.setVisibility(View.GONE);
        }

        view.resumeButton.setVisibility(View.GONE);
        view.pauseButton.setOnClickListener(v ->
                {
                    activityModel.pauseTimer();
                    view.resumeButton.setVisibility(View.VISIBLE);
                }
        );

        view.resumeButton.setOnClickListener(v -> {
            if (activityModel.getTimerState().getValue() == TimerState.PAUSED) {
                activityModel.resumeTimer();
                view.resumeButton.setVisibility(View.GONE);
                view.pauseButton.setVisibility(View.VISIBLE);
            }
        });


        return view.getRoot();
    }
}
