package edu.ucsd.cse110.habitizer.app.ui.tasklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import android.util.Log;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.RoutineTaskBinding;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;


public class RoutineTaskAdapter extends ArrayAdapter<ActiveTask> {

    private final Consumer<Integer> onCheckedClick;
    MutableSubject<Boolean> onFinishedRoutine;

    public RoutineTaskAdapter(Context context, List<ActiveTask> activeTasks, Consumer<Integer> onCheckedClick, MutableSubject<Boolean> onFinishedRoutine) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(activeTasks));
        this.onCheckedClick = onCheckedClick;
        this.onFinishedRoutine = onFinishedRoutine;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the task for this position
        var task = getItem(position);
        assert task != null;

        RoutineTaskBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = RoutineTaskBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = RoutineTaskBinding.inflate(layoutInflater, parent, false);
        }


        // Populate the view with the task
        binding.checkTask.setText(task.task().name());


        if (task.checked()) {
            long seconds = task.checkedElapsedTime() / CustomTimer.MILLISECONDS_PER_SECOND;
            long minutes = (seconds + 59)/ 60;

            minutes = Math.max(1, minutes);

            binding.timeText.setText(String.format("%dm", minutes));

        } else {
            binding.timeText.setText("-");
        }

        onFinishedRoutine.observe(finished -> {
            if(finished == null) return;
            if (finished) {
                binding.checkTask.setEnabled(false);
            } else {
                binding.checkTask.setEnabled(true);
            }
        });

        if (task.checked()){
            binding.checkTask.setEnabled(false);
        }

        binding.checkTask.setOnClickListener(view -> {
            var id = task.task().id();
            assert id != null;
            onCheckedClick.accept(id);
        });
        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var task = getItem(position);
        assert task != null;

        var id = task.task().id();
        assert id != null;

        return id;
    }
}
